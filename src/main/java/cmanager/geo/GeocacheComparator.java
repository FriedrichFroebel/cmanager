package cmanager.geo;

import cmanager.util.DateTimeUtil;
import cmanager.util.LoggingUtil;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

/** Methods for comparing geocaches. */
public class GeocacheComparator {

    /** Logger instance to use for information messages. */
    private static final Logger LOGGER = LoggingUtil.getLogger(GeocacheComparator.class);

    /**
     * Calculate the similarity between the given geocache instances.
     *
     * @param geocache1 The first geocache instance.
     * @param geocache2 The second geocache instance.
     * @return The calculated similarity value.
     */
    public static double calculateSimilarity(final Geocache geocache1, final Geocache geocache2) {
        final String codeGc1 = geocache1.getCodeGc();
        final String codeGc2 = geocache2.getCodeGc();
        final String code1 = geocache1.getCode();
        final String code2 = geocache2.getCode();

        // Check for matching GC codes for duplicate listings.
        // For OC caches, the owner might have decided to link to the corresponding GC cache which
        // we can make use of here.
        if ((codeGc1 != null && codeGc1.toUpperCase().equals(code2))
                || (codeGc2 != null && codeGc2.toUpperCase().equals(code1))) {
            LOGGER.info("Found matching linked GC code.");
            return 1;
        }

        // If a non premium member downloads her/his founds via geotoad, premium caches are
        // mis-located at 0.0/0.0 which falsely matches many OC dummies in the ocean.
        if (geocache1.getCoordinate().equals(new Coordinate(0.0, 0.0))
                && geocache2.getCoordinate().equals(new Coordinate(0.0, 0.0))) {
            LOGGER.info("Found mis-located premium cache.");
            return 0;
        }

        double dividend = 0;
        double divisor = 0;

        // Check whether the names only differ in their padding.
        // Names cannot be null.
        divisor++;
        if (geocache1.getName().trim().equals(geocache2.getName().trim())) {
            LOGGER.info("Names are equal.");
            dividend++;
        } else {
            LOGGER.info("Names are not equal.");
        }

        // Check whether the positions are less than 1 metre apart.
        divisor++;
        if (geocache1.getCoordinate().distanceHaversine(geocache2.getCoordinate()) < 0.001) {
            LOGGER.info("Coordinates are less than 1 metre apart.");
            dividend++;
        } else {
            LOGGER.info("Coordinates are more than 1 metre apart.");
        }

        // Check whether the difficulty rating is the same.
        divisor++;
        if (Double.compare(geocache1.getDifficulty(), geocache2.getDifficulty()) == 0) {
            LOGGER.info("Difficulty rating is the same.");
            dividend++;
        } else {
            LOGGER.info("Difficulty rating differs.");
        }

        // Check whether the terrain rating is the same.
        divisor++;
        if (Double.compare(geocache1.getTerrain(), geocache2.getTerrain()) == 0) {
            LOGGER.info("Terrain rating is the same.");
            dividend++;
        } else {
            LOGGER.info("Terrain rating differs.");
        }

        // Check whether the cache types are the same.
        divisor++;
        if (geocache1.getType().equals(geocache2.getType())) {
            LOGGER.info("Cache types are the same.");
            dividend++;
        } else {
            LOGGER.info("Cache types differ.");
        }

        // Handle event dates. See issue #17.
        if (geocache1.getType().isEventType() && geocache2.getType().isEventType()) {
            // Both geocaches are an event cache, so we should perform this comparison.

            divisor++;

            // Make sure that the event dates only differ in less than 1 day.
            final ZonedDateTime dateHidden1 = geocache1.getDateHidden();
            final ZonedDateTime dateHidden2 = geocache2.getDateHidden();
            final boolean isInRange = DateTimeUtil.isInDayRange(dateHidden1, dateHidden2, 1);

            if (isInRange) {
                LOGGER.info("Event cache with dates less than 1 day apart.");
                dividend++;
            } else {
                LOGGER.info("Event cache with dates more than 1 day apart.");
            }
        }

        // Check whether the owner names match.
        if (geocache1.getOwner() != null && geocache2.getOwner() != null) {
            divisor++;
            final String owner1 = geocache1.getOwner();
            final String owner2 = geocache2.getOwner();
            if (owner1.equals(owner2)) {
                LOGGER.info("Owners are the same.");
                dividend++;
            } else if (owner1.contains(owner2) || owner2.contains(owner1)) {
                LOGGER.info("One owner is a substring of the other.");
                dividend += 2.0 / 3.0;
            } else {
                LOGGER.info("Owners differ completely.");
            }
        } else {
            LOGGER.info("Skipping owner test as at least one owner is null.");
        }

        // Check whether the container sizes match.
        if (geocache1.getContainer() != null && geocache2.getContainer() != null) {
            divisor++;
            if (geocache1.getContainer().equals(geocache2.getContainer())) {
                LOGGER.info("Cache sizes are the same.");
                dividend++;
            } else {
                LOGGER.info(
                        MessageFormat.format(
                                "Cache sizes differ: {0} vs. {1}.",
                                geocache1.getContainer().asGc(), geocache2.getContainer().asGc()));
            }
        } else {
            LOGGER.info("Skipping cache size test as at least one container is null.");
        }

        // Check whether the status matches.
        if (geocache1.isAvailable() != null && geocache1.isArchived() != null) {
            divisor++;
            if (geocache1.getStatusAsString().equals(geocache2.getStatusAsString())) {
                dividend++;
            }
        }
        LOGGER.info(
                MessageFormat.format(
                        "Status comparison: {0} vs. {1}.",
                        geocache1.getStatusAsString(), geocache2.getStatusAsString()));

        // Log result as fraction.
        LOGGER.info(MessageFormat.format("Similarity = {0} / {1}", dividend, divisor));

        // Calculate the fraction.
        return dividend / divisor;
    }

    /**
     * Check whether the given geocache instances are similar, when employing a threshold value of
     * 80 %.
     *
     * @param geocache1 The first geocache instance.
     * @param geocache2 The second geocache instance.
     * @return Whether the two geocache instances share at least 80 % of common data according to
     *     our heuristics.
     */
    public static boolean areSimilar(final Geocache geocache1, final Geocache geocache2) {
        return areSimilar(geocache1, geocache2, 0.8);
    }

    /**
     * Check whether the given geocache instances are similar, when employing the given threshold
     * value.
     *
     * @param geocache1 The first geocache instance.
     * @param geocache2 The second geocache instance.
     * @param threshold The threshold value to use. Set to `0.8` to aim for a similarity of at least
     *     80 %.
     * @return Whether the two geocache instances share at least the given amount of common data
     *     according to our heuristics.
     */
    public static boolean areSimilar(
            final Geocache geocache1, final Geocache geocache2, final double threshold) {
        final double similarity = calculateSimilarity(geocache1, geocache2);
        final boolean comparisonResult = similarity >= threshold;

        LOGGER.info(
                MessageFormat.format(
                        "calculateSimilarity({0}, {1}) = {2} (=> threshold condition is {3} with threshold {4})",
                        geocache1.getCode(),
                        geocache2.getCode(),
                        similarity,
                        comparisonResult,
                        threshold));
        LOGGER.info(MessageFormat.format("Geocache 1: {0}", geocache1.toString()));
        LOGGER.info(MessageFormat.format("Geocache 2: {0}", geocache2.toString()));

        return comparisonResult;
    }
}
