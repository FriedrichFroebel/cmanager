package cmanager.geo;

public class GeocacheComparator {

    public static double calculateSimilarity(Geocache geocache1, Geocache geocache2) {
        final String codeGc1 = geocache1.getCodeGC();
        final String codeGc2 = geocache2.getCodeGC();
        final String code1 = geocache1.getCode();
        final String code2 = geocache2.getCode();

        if ((codeGc1 != null && codeGc1.toUpperCase().equals(code2))
                || (codeGc2 != null && codeGc2.toUpperCase().equals(code1))) {
            return 1;
        }

        // If a non premium member downloads her/his founds via geotoad, premium caches are
        // mis-located at 0.0/0.0 which falsely matches many OC dummies in the ocean.
        if (geocache1.getCoordinate().equals(new Coordinate(0.0, 0.0))
                && geocache2.getCoordinate().equals(new Coordinate(0.0, 0.0))) {
            return 0;
        }

        double dividend = 0;
        double divisor = 0;

        divisor++;
        if (geocache1.getName().equals(geocache2.getName())) {
            dividend++;
        }

        divisor++;
        if (geocache1.getCoordinate().distanceHaversine(geocache2.getCoordinate()) < 0.001) {
            dividend++;
        }

        divisor++;
        if (Double.compare(geocache1.getDifficulty(), geocache2.getDifficulty()) == 0) {
            dividend++;
        }

        divisor++;
        if (Double.compare(geocache1.getTerrain(), geocache2.getTerrain()) == 0) {
            dividend++;
        }

        divisor++;
        if (geocache1.getType().equals(geocache2.getType())) {
            dividend++;
        }

        if (geocache1.getOwner() != null) {
            divisor++;
            final String owner1 = geocache1.getOwner();
            final String owner2 = geocache2.getOwner();
            if (owner1.equals(owner2)) {
                dividend++;
            } else if (owner1.contains(owner2) || owner2.contains(owner1)) {
                dividend += 2.0 / 3.0;
            }
        }

        if (geocache1.getContainer() != null) {
            divisor++;
            if (geocache1.getContainer().equals(geocache2.getContainer())) {
                dividend++;
            }
        }

        if (geocache1.isAvailable() != null && geocache1.isArchived() != null) {
            divisor++;
            if (geocache1.getStatusAsString().equals(geocache2.getStatusAsString())) {
                dividend++;
            }
        }

        return dividend / divisor;
    }

    public static boolean areSimilar(Geocache geocache1, Geocache geocache2) {
        return areSimilar(geocache1, geocache2, 0.8);
    }

    public static boolean areSimilar(Geocache geocache1, Geocache geocache2, double threshold) {
        return calculateSimilarity(geocache1, geocache2) >= threshold;
    }
}
