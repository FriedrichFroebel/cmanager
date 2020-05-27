package cmanager;

import cmanager.geo.Geocache;
import java.util.ArrayList;
import java.util.List;

public abstract class CacheListFilterModel extends CacheListFilterPanel {

    private static final long serialVersionUID = 6947085305393841410L;

    public CacheListFilterModel(FILTER_TYPE filterType) {
        super(filterType);
    }

    public List<Geocache> getFiltered(final List<Geocache> originalList) {
        final int listSize = originalList.size();
        ThreadStore threadStore = new ThreadStore();
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
                    new Thread(
                            new Runnable() {
                                public void run() {
                                    final List<Geocache> list = lists.get(coreFinal);
                                    try {
                                        for (int i = start; i < end; i++) {
                                            final Geocache geocache = originalList.get(i);
                                            if ((!inverted && isGood(geocache))
                                                    || (inverted && !isGood(geocache))) {
                                                list.add(geocache);
                                            }
                                        }
                                    } catch (Throwable ethrowable) {
                                        Thread thread = Thread.currentThread();
                                        thread.getUncaughtExceptionHandler()
                                                .uncaughtException(thread, ethrowable);
                                    }
                                }
                            }));
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

    protected abstract boolean isGood(Geocache geocache);
}
