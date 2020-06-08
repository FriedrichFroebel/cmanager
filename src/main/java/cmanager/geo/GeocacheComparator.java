package cmanager.geo;

import cmanager.util.LoggingUtil;
import java.text.MessageFormat;
import java.util.logging.Logger;

public class GeocacheComparator {

    public static double calculateSimilarity(Geocache geocache1, Geocache geocache2) {
        final String codeGc1 = geocache1.getCodeGc();
        final String codeGc2 = geocache2.getCodeGc();
        final String code1 = geocache1.getCode();
        final String code2 = geocache2.getCode();

        final Logger logger = LoggingUtil.getLogger(GeocacheComparator.class);

        if ((codeGc1 != null && codeGc1.toUpperCase().equals(code2))
                || (codeGc2 != null && codeGc2.toUpperCase().equals(code1))) {
            logger.info("Found matching linked GC code.");
            return 1;
        }

        // If a non premium member downloads her/his founds via geotoad, premium caches are
        // mis-located at 0.0/0.0 which falsely matches many OC dummies in the ocean.
        if (geocache1.getCoordinate().equals(new Coordinate(0.0, 0.0))
                && geocache2.getCoordinate().equals(new Coordinate(0.0, 0.0))) {
            logger.info("Found mis-located premium cache.");
            return 0;
        }

        double dividend = 0;
        double divisor = 0;

        divisor++;
        // Name cannot be null.
        if (geocache1.getName().trim().equals(geocache2.getName().trim())) {
            logger.info("Names are equal.");
            dividend++;
        } else {
            logger.info("Names are not equal.");
        }

        divisor++;
        if (geocache1.getCoordinate().distanceHaversine(geocache2.getCoordinate()) < 0.001) {
            logger.info("Coordinates are less than 1 metre apart.");
            dividend++;
        } else {
            logger.info("Coordinates are more than 1 metre apart.");
        }

        divisor++;
        if (Double.compare(geocache1.getDifficulty(), geocache2.getDifficulty()) == 0) {
            logger.info("Difficulty rating is the same.");
            dividend++;
        } else {
            logger.info("Difficulty rating differs.");
        }

        divisor++;
        if (Double.compare(geocache1.getTerrain(), geocache2.getTerrain()) == 0) {
            logger.info("Terrain rating is the same.");
            dividend++;
        } else {
            logger.info("Terrain rating differs.");
        }

        divisor++;
        if (geocache1.getType().equals(geocache2.getType())) {
            logger.info("Cache types are the same.");
            dividend++;
        } else {
            logger.info("Cache types differ.");
        }

        if (geocache1.getOwner() != null) {
            divisor++;
            final String owner1 = geocache1.getOwner();
            final String owner2 = geocache2.getOwner();
            if (owner1.equals(owner2)) {
                logger.info("Owners are the same.");
                dividend++;
            } else if (owner1.contains(owner2) || owner2.contains(owner1)) {
                logger.info("One owner is a substring of the other.");
                dividend += 2.0 / 3.0;
            } else {
                logger.info("Owners differ completely.");
            }
        } else {
            logger.info("Skipping owner test as the first owner is null.");
        }

        if (geocache1.getContainer() != null) {
            divisor++;
            if (geocache1.getContainer().equals(geocache2.getContainer())) {
                logger.info("Cache sizes are the same.");
                dividend++;
            } else {
                logger.info(
                        MessageFormat.format(
                                "Cache sizes differ: {0} vs. {1}.",
                                geocache1.getContainer().asGc(), geocache2.getContainer().asGc()));
            }
        } else {
            logger.info("Skipping cache size test as the first container is null.");
        }

        if (geocache1.isAvailable() != null && geocache1.isArchived() != null) {
            divisor++;
            if (geocache1.getStatusAsString().equals(geocache2.getStatusAsString())) {
                dividend++;
            }
        }
        logger.info(
                MessageFormat.format(
                        "Status comparison: {0} vs. {1}.",
                        geocache1.getStatusAsString(), geocache2.getStatusAsString()));

        logger.info(MessageFormat.format("Similarity = {0} / {1}", dividend, divisor));

        return dividend / divisor;
    }

    public static boolean areSimilar(Geocache geocache1, Geocache geocache2) {
        return areSimilar(geocache1, geocache2, 0.8);
    }

    public static boolean areSimilar(Geocache geocache1, Geocache geocache2, double threshold) {
        final double similarity = calculateSimilarity(geocache1, geocache2);
        final boolean comparisonResult = similarity >= threshold;

        final Logger logger = LoggingUtil.getLogger(GeocacheComparator.class);
        logger.info(
                MessageFormat.format(
                        "calculateSimilarity({0}, {1}) = {2} (=> threshold condition is {3} with threshold {4})",
                        geocache1.getCode(),
                        geocache2.getCode(),
                        similarity,
                        comparisonResult,
                        threshold));
        logger.info(MessageFormat.format("Geocache 1: {0}", geocache1.toString()));
        logger.info(MessageFormat.format("Geocache 2: {0}", geocache2.toString()));

        return comparisonResult;
    }
}
