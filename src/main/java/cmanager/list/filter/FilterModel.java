package cmanager.list.filter;

import cmanager.geo.Geocache;
import cmanager.gui.components.CacheListFilterPanel;
import cmanager.list.CacheListFilterType;
import cmanager.util.ThreadStore;
import java.util.ArrayList;
import java.util.List;

/** Handle list filtering. */
public abstract class FilterModel extends CacheListFilterPanel {

    private static final long serialVersionUID = 6947085305393841410L;

    /**
     * Create a new instance with the given filter type.
     *
     * @param filterType The type of the filter.
     */
    public FilterModel(CacheListFilterType filterType) {
        super(filterType);
    }

    /**
     * Return a filtered copy of the given list.
     *
     * <p>This will use multiple cores for processing.
     *
     * @param originalList The list to filter.
     * @return The filtered list.
     */
    public List<Geocache> getFiltered(final List<Geocache> originalList) {
        final int listSize = originalList.size();

        // Stop if this is an empty list. Otherwise we get a division by zero as the number of cores
        // cannot be greater than the list size.
        if (listSize == 0) {
            return originalList;
        }

        final ThreadStore threadStore = new ThreadStore();
        final int cores = threadStore.getCores(listSize);
        final int perProcess = listSize / cores;

        final List<List<Geocache>> lists = new ArrayList<>(5);
        for (int core = 0; core < cores; core++) {
            lists.add(new ArrayList<>());
        }
        for (int core = 0; core < cores; core++) {
            final int start = perProcess * core;
            final int coreFinal = core;

            int temp = Math.min(perProcess * (core + 1), listSize);
            if (core == cores - 1) {
                temp = listSize;
            }
            final int end = temp;

            threadStore.addAndRun(
                    new Thread(() -> filterList(originalList, lists, start, end, coreFinal)));
        }
        try {
            threadStore.joinAndThrow();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        final List<Geocache> listAll = new ArrayList<>();
        for (final List<Geocache> list : lists) {
            listAll.addAll(list);
        }

        return listAll;
    }

    /**
     * Filter the given list.
     *
     * @param originalList The list to filter.
     * @param lists The output lists, with each sub-list belonging to one core.
     * @param start The start index to use inside the original list.
     * @param end The end index to use inside the original list.
     * @param core The core this filtering is done with. This basically is the index of the sub-list
     *     to write the data to.
     */
    private void filterList(
            final List<Geocache> originalList,
            final List<List<Geocache>> lists,
            int start,
            int end,
            int core) {
        final List<Geocache> list = lists.get(core);
        try {
            for (int i = start; i < end; i++) {
                final Geocache geocache = originalList.get(i);
                if ((!inverted && isGood(geocache)) || (inverted && !isGood(geocache))) {
                    list.add(geocache);
                }
            }
        } catch (Throwable throwable) {
            final Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, throwable);
        }
    }

    /**
     * Check whether the given geocache matches the filter expression.
     *
     * @param geocache The geocache to check.
     * @return The check result.
     */
    protected abstract boolean isGood(final Geocache geocache);
}
