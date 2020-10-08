package cmanager.oc;

import cmanager.geo.Geocache;
import cmanager.global.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Shadow list lookup handler.
 *
 * <p>The shadow list has been a (currently defunct) undocumented API specifically created for the
 * cmanager application. When calling it, one received a reverse mapping GC -> OC with verified
 * entries. Additionally users of cmanager would post their own validated duplicates to this API
 * endpoint to allow faster duplicate checks for the next users.
 */
public class ShadowList {

    /** The directory where the local shadow list copy is stored. */
    private static final String SHADOWLIST_FOLDER = Constants.CACHE_FOLDER + "OC.shadowlist";

    /** The path to the actual shadow list file. */
    private static final String SHADOWLIST_PATH = SHADOWLIST_FOLDER + "/gc2oc.gz";

    /**
     * The directory indicating which geocaches have already been posted to the shadow list by this
     * user.
     */
    private static final String SHADOWLIST_POSTED_FOLDER =
            Constants.CACHE_FOLDER + "OC.shadowlist.posted";

    /**
     * Update the local shadow list copy.
     *
     * <p>This will be done once a month.
     *
     * @throws IOException Something went wrong with the update.
     */
    public static void updateShadowList() throws IOException {
        // TODO: Enable once the API is working again.

        /*
        // Delete the list if it is older than 1 month.
        final File file = new File(SHADOWLIST_PATH);
        if (file.exists()) {
            DateTime fileTime = new DateTime(file.lastModified());
            final DateTime now = new DateTime();
            fileTime = fileTime.plusMonths(1);
            if (fileTime.isAfter(now)) {
                return;
            }

            if (!file.delete()) {
                System.out.println("Error deleting file " + SHADOWLIST_PATH + ".");
            }
        }

        // Create the shadow list directory.
        final boolean success = new File(SHADOWLIST_FOLDER).mkdirs();
        if (!success) {
            System.out.println("Error creating directory " + SHADOWLIST_FOLDER + ".");
        }

        // Download the list.
        final URL url = new URL("https://www.opencaching.de/api/gc2oc.php");
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(SHADOWLIST_PATH);
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        fileOutputStream.close();*/
    }

    /**
     * Load the mapping from the local shadow list copy.
     *
     * @throws Throwable Something went wrong when loading the data.
     */
    public static ShadowList loadShadowList() throws Throwable {
        final HashMap<String, String> shadowList = new HashMap<>();

        System.out.println(
                "The shadow list retrieval has been disabled temporarily as the API endpoint is gone. "
                        + "You can find additional information in issue #5 (https://github.com/FriedrichFroebel/cmanager/issues/5).");
        // TODO: Enable after the GZip archive is valid again.

        /*
        // Unpack the GZIP compressed file and read its content.
        FileHelper.processFiles(SHADOWLIST_PATH, new FileHelper.InputAction() {
            @Override
            public void process(InputStream inputStream) throws Throwable {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    final String[] token = line.split(",");
                    // Column 2 == "1" means verified by a human
                    if (token[2].equals("1")) {
                        // <GC, OC>
                        shadowList.put(token[0], token[1]);
                    }
                }
            }
        });*/

        return new ShadowList(shadowList);
    }

    /** The actual mapping data. */
    private final Map<String, String> shadowList;

    /**
     * Create a new shadow list instance.
     *
     * @param shadowList The shadow list data to set.
     */
    private ShadowList(final Map<String, String> shadowList) {
        this.shadowList = shadowList;
    }

    /**
     * Get the OC code for the corresponding GC code.
     *
     * @param gcCode The GC code to search for.
     * @return The matching OC code. This will be `null` for unknown GC codes.
     */
    public String getMatchingOcCode(final String gcCode) {
        return shadowList.get(gcCode);
    }

    /**
     * Check whether the shadow list contains the given GC code.
     *
     * @param gcCode The GC code to check for.
     * @return Whether the shadow list contains the given GC code.
     */
    public boolean contains(final String gcCode) {
        return shadowList.get(gcCode) != null;
    }

    /**
     * Post the given match to the shadow list.
     *
     * @param gc The geocache instance of the match.
     * @param oc The corresponding opencache instance.
     * @throws Exception Something went wrong when posting the data.
     */
    public void postToShadowList(final Geocache gc, final Geocache oc) throws Exception {
        // TODO: Enable once the API is working again.

        /*
        // Do not repost items which are already upstream.
        if (contains(gc.getCode())) {
            return;
        }

        // Do not repost local findings.
        final File file = new File(SHADOWLIST_POSTED_FOLDER + "/" + gc.getCode());
        if (file.exists()) {
            return;
        }

        final String url =
                "https://www.opencaching.de/api/gc2oc.php"
                        + "?report=1"
                        + "&ocwp="
                        + oc.getCode()
                        + "&gcwp="
                        + gc.getCode()
                        + "&source="
                        + Constants.APP_NAME
                        + "+"
                        + Version.VERSION;

        // Post.
        HTTP.get(url);

        // Remember our post.
        new File(SHADOWLIST_POSTED_FOLDER).mkdirs();
        file.createNewFile();*/
    }
}
