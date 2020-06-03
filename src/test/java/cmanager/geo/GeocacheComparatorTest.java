package cmanager.geo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import cmanager.exception.CoordinateUnparsableException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for the geocache comparator. */
public class GeocacheComparatorTest {

    /** A list of geocaches which produce matches. */
    private final List<Geocache[]> matching = new ArrayList<>();

    /** A list of geocaches which produce no matches. */
    private final List<Geocache[]> notMatching = new ArrayList<>();

    /**
     * Add a pair of caches with a match to the list of matching caches.
     *
     * @param code1 The code of the first cache.
     * @param name1 The name of the first cache.
     * @param coordinates1 The coordinates of the first cache.
     * @param difficulty1 The difficulty of the first cache.
     * @param terrain1 The terrain of the first cache.
     * @param type1 The type of the first cache.
     * @param owner1 The owner of the first cache.
     * @param container1 The container size of the first cache.
     * @param archived1 Whether the first cache is archived.
     * @param available1 Whether the first cache is available.
     * @param code2 The code of the second cache.
     * @param name2 The name of the second cache.
     * @param coordinates2 The coordinates of the second cache.
     * @param difficulty2 The difficulty of the second cache.
     * @param terrain2 The terrain of the second cache.
     * @param type2 The type of the second cache.
     * @param owner2 The owner of the second cache.
     * @param container2 The container size of the second cache.
     * @param archived2 Whether the second cache is archived.
     * @param available2 Whether the second cache is available.
     */
    private void addGood(
            String code1,
            String name1,
            String coordinates1,
            Double difficulty1,
            Double terrain1,
            String type1,
            String owner1,
            String container1,
            Boolean archived1,
            Boolean available1,
            String code2,
            String name2,
            String coordinates2,
            Double difficulty2,
            Double terrain2,
            String type2,
            String owner2,
            String container2,
            Boolean archived2,
            Boolean available2) {
        add(
                matching,
                code1,
                name1,
                coordinates1,
                difficulty1,
                terrain1,
                type1,
                owner1,
                container1,
                archived1,
                available1,
                code2,
                name2,
                coordinates2,
                difficulty2,
                terrain2,
                type2,
                owner2,
                container2,
                archived2,
                available2,
                null);
    }

    /**
     * Add a pair of caches with a match to the list of matching caches.
     *
     * @param code1 The code of the first cache.
     * @param name1 The name of the first cache.
     * @param coordinates1 The coordinates of the first cache.
     * @param difficulty1 The difficulty of the first cache.
     * @param terrain1 The terrain of the first cache.
     * @param type1 The type of the first cache.
     * @param owner1 The owner of the first cache.
     * @param container1 The container size of the first cache.
     * @param archived1 Whether the first cache is archived.
     * @param available1 Whether the first cache is available.
     * @param code2 The code of the second cache.
     * @param name2 The name of the second cache.
     * @param coordinates2 The coordinates of the second cache.
     * @param difficulty2 The difficulty of the second cache.
     * @param terrain2 The terrain of the second cache.
     * @param type2 The type of the second cache.
     * @param owner2 The owner of the second cache.
     * @param container2 The container size of the second cache.
     * @param archived2 Whether the second cache is archived.
     * @param available2 Whether the second cache is available.
     * @param codeGc The GC code of the cache.
     */
    private void addGood(
            String code1,
            String name1,
            String coordinates1,
            Double difficulty1,
            Double terrain1,
            String type1,
            String owner1,
            String container1,
            Boolean archived1,
            Boolean available1,
            String code2,
            String name2,
            String coordinates2,
            Double difficulty2,
            Double terrain2,
            String type2,
            String owner2,
            String container2,
            Boolean archived2,
            Boolean available2,
            String codeGc) {
        add(
                matching,
                code1,
                name1,
                coordinates1,
                difficulty1,
                terrain1,
                type1,
                owner1,
                container1,
                archived1,
                available1,
                code2,
                name2,
                coordinates2,
                difficulty2,
                terrain2,
                type2,
                owner2,
                container2,
                archived2,
                available2,
                codeGc);
    }

    /**
     * Add a pair of caches without a match to the list of not matching caches.
     *
     * @param code1 The code of the first cache.
     * @param name1 The name of the first cache.
     * @param coordinates1 The coordinates of the first cache.
     * @param difficulty1 The difficulty of the first cache.
     * @param terrain1 The terrain of the first cache.
     * @param type1 The type of the first cache.
     * @param owner1 The owner of the first cache.
     * @param container1 The container size of the first cache.
     * @param archived1 Whether the first cache is archived.
     * @param available1 Whether the first cache is available.
     * @param code2 The code of the second cache.
     * @param name2 The name of the second cache.
     * @param coordinates2 The coordinates of the second cache.
     * @param difficulty2 The difficulty of the second cache.
     * @param terrain2 The terrain of the second cache.
     * @param type2 The type of the second cache.
     * @param owner2 The owner of the second cache.
     * @param container2 The container size of the second cache.
     * @param archived2 Whether the second cache is archived.
     * @param available2 Whether the second cache is available.
     */
    private void addBad(
            String code1,
            String name1,
            String coordinates1,
            Double difficulty1,
            Double terrain1,
            String type1,
            String owner1,
            String container1,
            Boolean archived1,
            Boolean available1,
            String code2,
            String name2,
            String coordinates2,
            Double difficulty2,
            Double terrain2,
            String type2,
            String owner2,
            String container2,
            Boolean archived2,
            Boolean available2) {
        add(
                notMatching,
                code1,
                name1,
                coordinates1,
                difficulty1,
                terrain1,
                type1,
                owner1,
                container1,
                archived1,
                available1,
                code2,
                name2,
                coordinates2,
                difficulty2,
                terrain2,
                type2,
                owner2,
                container2,
                archived2,
                available2,
                null);
    }

    /**
     * Add a pair of caches without a match to the list of not matching caches.
     *
     * @param code1 The code of the first cache.
     * @param name1 The name of the first cache.
     * @param coordinates1 The coordinates of the first cache.
     * @param difficulty1 The difficulty of the first cache.
     * @param terrain1 The terrain of the first cache.
     * @param type1 The type of the first cache.
     * @param owner1 The owner of the first cache.
     * @param container1 The container size of the first cache.
     * @param archived1 Whether the first cache is archived.
     * @param available1 Whether the first cache is available.
     * @param code2 The code of the second cache.
     * @param name2 The name of the second cache.
     * @param coordinates2 The coordinates of the second cache.
     * @param difficulty2 The difficulty of the second cache.
     * @param terrain2 The terrain of the second cache.
     * @param type2 The type of the second cache.
     * @param owner2 The owner of the second cache.
     * @param container2 The container size of the second cache.
     * @param archived2 Whether the second cache is archived.
     * @param available2 Whether the second cache is available.
     * @param codeGc The GC code of the cache.
     */
    private void addBad(
            String code1,
            String name1,
            String coordinates1,
            Double difficulty1,
            Double terrain1,
            String type1,
            String owner1,
            String container1,
            Boolean archived1,
            Boolean available1,
            String code2,
            String name2,
            String coordinates2,
            Double difficulty2,
            Double terrain2,
            String type2,
            String owner2,
            String container2,
            Boolean archived2,
            Boolean available2,
            String codeGc) {
        add(
                notMatching,
                code1,
                name1,
                coordinates1,
                difficulty1,
                terrain1,
                type1,
                owner1,
                container1,
                archived1,
                available1,
                code2,
                name2,
                coordinates2,
                difficulty2,
                terrain2,
                type2,
                owner2,
                container2,
                archived2,
                available2,
                codeGc);
    }

    /**
     * Add a pair of caches to the specified list.
     *
     * @param list The list to add the caches to.
     * @param code1 The code of the first cache.
     * @param name1 The name of the first cache.
     * @param coordinates1 The coordinates of the first cache.
     * @param difficulty1 The difficulty of the first cache.
     * @param terrain1 The terrain of the first cache.
     * @param type1 The type of the first cache.
     * @param owner1 The owner of the first cache.
     * @param container1 The container size of the first cache.
     * @param archived1 Whether the first cache is archived.
     * @param available1 Whether the first cache is available.
     * @param code2 The code of the second cache.
     * @param name2 The name of the second cache.
     * @param coordinates2 The coordinates of the second cache.
     * @param difficulty2 The difficulty of the second cache.
     * @param terrain2 The terrain of the second cache.
     * @param type2 The type of the second cache.
     * @param owner2 The owner of the second cache.
     * @param container2 The container size of the second cache.
     * @param archived2 Whether the second cache is archived.
     * @param available2 Whether the second cache is available.
     * @param codeGc The GC code of the cache.
     */
    private void add(
            List<Geocache[]> list,
            String code1,
            String name1,
            String coordinates1,
            Double difficulty1,
            Double terrain1,
            String type1,
            String owner1,
            String container1,
            Boolean archived1,
            Boolean available1,
            String code2,
            String name2,
            String coordinates2,
            Double difficulty2,
            Double terrain2,
            String type2,
            String owner2,
            String container2,
            Boolean archived2,
            Boolean available2,
            String codeGc) {
        try {
            final Geocache geocache1 =
                    new Geocache(
                            code1,
                            name1,
                            new Coordinate(coordinates1),
                            difficulty1,
                            terrain1,
                            type1);
            geocache1.setOwner(owner1);
            geocache1.setContainer(container1);
            geocache1.setArchived(archived1);
            geocache1.setAvailable(available1);

            final Geocache geocache2 =
                    new Geocache(
                            code2,
                            name2,
                            new Coordinate(coordinates2),
                            difficulty2,
                            terrain2,
                            type2);
            geocache2.setOwner(owner2);
            geocache2.setContainer(container2);
            geocache2.setArchived(archived2);
            geocache2.setAvailable(available2);
            geocache2.setCodeGc(codeGc);

            list.add(new Geocache[] {geocache1, geocache2});
        } catch (NullPointerException | CoordinateUnparsableException exception) {
            exception.printStackTrace();
            fail("Unable to initialize list.");
        }
    }

    /** Test with matching caches. */
    @Test
    @DisplayName("Test matching cache pairs")
    public void testMatching() {
        // Real life samples.

        addGood(
                // Cache 1.
                "GCC681",
                "Moorleiche",
                "N 53° 06.438' E 008° 07.767'",
                2.0,
                3.0,
                "Multi",
                "digitali",
                "regular",
                false,
                true,
                // Cache 2.
                "OC0BEF",
                "Moorleiche",
                "N 53° 06.438' E 008° 07.767'",
                2.0,
                3.0,
                "Multi",
                "digitali",
                "Regular",
                false,
                true);

        addGood(
                // Cache 1.
                "GC1F9JP",
                "TB-Hotel Nr. 333",
                "N 53° 08.245' E 008° 16.700'",
                1.0,
                2.0,
                "Tradi",
                "TravelMad",
                "regular",
                false,
                true,
                // Cache 2.
                "OC6544",
                "TB-Hotel Nr. 333",
                "N 53° 08.245' E 008° 16.700'",
                1.0,
                1.5,
                "Tradi",
                "TravelMad",
                "Regular",
                false,
                true);

        addGood(
                // Cache 1.
                "GC3314B",
                "Zeche Gottessegen - III - Stollen",
                "N 51° 26.334 E 007° 27.874",
                2.0,
                2.0,
                "Tradi",
                "Wir_4",
                "micro",
                true,
                false,
                // Cache 2.
                "OCD346",
                "Zeche Gottessegen - III - Stollen",
                "N 51° 26.334' E 007° 27.874'",
                2.0,
                2.0,
                "Tradi",
                "Wir_4",
                "Micro",
                true,
                false);

        addGood(
                // Cache 1.
                "GC3314V",
                "Zeche Gottessegen - IV - Förderturm",
                "N 51° 26.334 E 007° 28.077",
                2.5,
                2.0,
                "Tradi",
                "Wir_4",
                "small",
                true,
                false,
                // Cache 2.
                "OCD347",
                "Zeche Gottessegen - IV - Förderturm",
                "N 51° 26.334' E 007° 28.077'",
                2.0,
                2.0,
                "Tradi",
                "Wir_4",
                "Small",
                true,
                false);

        addGood(
                // Cache 1.
                "GC4675C",
                "Klaus Autowerkstatt (Kinder-Cache)",
                "N 51° 18.767' E 007° 26.629'",
                1.5,
                2.0,
                "Tradi",
                "Atomaffe",
                "small",
                true,
                false,
                // Cache 2.
                "OCF83C",
                "Klaus Autowerkstatt (Kinder-Cache)",
                "N 51° 18.767' E 007° 26.629'",
                1.5,
                2.0,
                "Tradi",
                "Atomaffe",
                "Regular",
                true,
                false);

        addGood(
                // Cache 1.
                "GC39105",
                "Ein “Schatz” aus der Antike",
                "N 50° 56.410' E 006° 49.730'",
                2.0,
                1.0,
                "Unknown Cache",
                "Rheingeister",
                "micro",
                false,
                true,
                // Cache 2.
                "OCD85A",
                "Ein “Schatz” aus der Antike",
                "N 50° 56.410' E 006° 49.710'",
                2.0,
                1.0,
                "Unknown Cache",
                "Rheingeister",
                "Micro",
                false,
                true);

        addGood(
                // Cache 1.
                "GC58YWX",
                "Ein Nano an der Kreuzung klebt...",
                "N 51° 24.661 E 007° 50.056",
                3.0,
                1.5,
                "Tradi",
                "Keks579",
                "micro",
                true,
                false,
                // Cache 2.
                "OC1120F",
                "Ein Nano an der Kreuzung klebt...",
                "N 51° 24.684' E 007° 50.035'",
                3.0,
                1.5,
                "Tradi",
                "Keks579_Unidos",
                "Micro",
                true,
                false);

        addGood(
                // Cache 1.
                "GC53AX3",
                "Piep Piep Piep",
                "N 51° 22.067 E 007° 29.565",
                1.5,
                1.5,
                "Tradi",
                "geyerwally",
                "micro",
                false,
                true,
                // Cache 2.
                "OC111B6",
                "Piep Piep Piep",
                "N 51° 22.067' E 007° 29.565'",
                1.5,
                1.5,
                "Tradi",
                "geyerwally",
                "Micro",
                false,
                true);

        // Volatile start!

        addGood(
                // Cache 1.
                "GC1P7V2",
                "Donald´s Badewanne",
                "N 51° 20.593 E 007° 31.486",
                3.0,
                1.5,
                "Unknown Cache",
                "Jerry_the_Dog",
                "small",
                true,
                false,
                // Cache 2.
                "OC8F33",
                "Donald´s Badewanne",
                "N 51° 20.907' E 007° 31.788'",
                4.0,
                1.5,
                "Unknown Cache",
                "Jerry_the_Dog",
                "Small",
                true,
                false,
                // GC code.
                "gc1p7v2");

        // Interesting tuples:
        //     OC1158E / GC4VRCT Händel oder Bruckner
        //     OC110D9 / GC58CJT Tu was Gutes...
        //     OC11577 / GC598KJ SPY
        //     OC948B / GC1ZRDV Reiglersbachsee
        //     OC174D / GCTBWH Crailsheim per Auto
        //     OC1467 / GCQJZP Helgoland Catamaran Quicky
        //     OC4551 / GC16Q2H Motte Keyenberg

        // Unmatched edge cases.

        /*addGood(
        // Cache 1.
        "GCJWEN",
        "Die Bärenhöhle",
        "N 51° 47.700' E 006 06.914'",
        3.0,
        4.0,
        "Tradi",
        "geoBONE",
        "micro",
        false,
        true,
        // Cache 2.
        "OC001B",
        "Die Baerenhoehle",
        "N 51° 47.700' E 006° 06.914'",
        3.0,
        4.5,
        "Tradi",
        "geoBONE",
        "Micro",
        true,
        false);*/

        /*addGood(
        // Cache 1.
        "GC5N4RW",
        "Schützenplatz ? !",
        "N 52° 30.020 E 009° 51.363",
        3.0,
        1.5,
        "Tradi",
        "TommyKFB",
        "micro",
        false,
        true,
        // Cache 2.
        "OC11BB9",
        "Schützenplatz ?!",
        "N 52° 30.020' E 009° 51.365'",
        1.0,
        1.0,
        "Tradi",
        "TommyKFB",
        "Micro",
        true,
        false);*/

        /*addGood(
        // Cache 1.
        "GC33W4R",
        "Kleine Prinzessin in der Stemke",
        "N 51° 21.900' E 007° 22.650'",
        1.5,
        3.0,
        "Unknown Cache",
        "Quickcreek",
        "regular",
        true,
        false,
        // Cache 2.
        "OCE5D1",
        "Kleine Prinzessin in der Stemke",
        "N 51° 21.841' E 007° 22.601'",
        1.0,
        2.5,
        "Unknown Cache",
        "Quickcreek",
        "Regular",
        false,
        true);*/

        for (final Geocache[] tuple : matching) {
            final Geocache gc = tuple[0];
            final Geocache oc = tuple[1];
            if (!GeocacheComparator.areSimilar(gc, oc)) {
                fail("No match: " + gc.toString() + " " + oc.toString());
            }
        }

        for (final Geocache[] tuple1 : matching) {
            for (final Geocache[] tuple2 : matching) {
                if (tuple1 == tuple2) {
                    continue;
                }

                final Geocache gc = tuple1[0];
                final Geocache oc = tuple2[1];
                final boolean similar = GeocacheComparator.areSimilar(gc, oc);
                assertFalse(similar, "Unexpected match: " + gc.toString() + " " + oc.toString());
            }
        }
    }

    /** Test with not matching caches. */
    @Test
    @DisplayName("Test not matching cache pairs")
    public void testNotMatching() {
        // Real life samples.

        addBad(
                // Cache 1.
                "GC6321G",
                "Konkurrenz belebt das Geschäft -GC-",
                "N 53° 35.565 E 009° 55.200",
                1.5,
                3.5,
                "Multi",
                "rbx270",
                "regular",
                false,
                true,
                // Cache 2.
                "OC12599",
                "Konkurrenz belebt das Geschäft -OC-",
                "N 53° 35.569' E 009° 55.207'",
                1.5,
                3.5,
                "Multi",
                "rbx270",
                "Regular",
                false,
                true);

        // Generics.

        addBad(
                // Cache 1.
                "GC",
                "cache",
                "N 1° 11.111 E 2° 22.222",
                1.0,
                3.0,
                "Multi",
                "author",
                "regular",
                false,
                true,
                // Cache 2.
                "OC",
                "cache",
                "N 1° 11.111 E 2° 22.222",
                2.0,
                4.0,
                "Multi",
                "author",
                "Regular",
                false,
                true);

        addBad(
                // Cache 1.
                "GC",
                "cache",
                "N 1° 22.222 E 2° 22.222",
                1.0,
                1.0,
                "Tradi",
                "author",
                "regular",
                false,
                true,
                // Cache 2.
                "OC",
                "cache",
                "N 1° 11.111 E 2° 22.222",
                1.0,
                1.0,
                "Multi",
                "author",
                "Regular",
                false,
                true);

        for (final Geocache[] tuple : notMatching) {
            final Geocache gc = tuple[0];
            final Geocache oc = tuple[1];
            final boolean similar = GeocacheComparator.areSimilar(gc, oc);
            assertFalse(similar, "Match: " + gc.toString() + " " + oc.toString());
        }
    }
}
