package cmanager.gpx;

import cmanager.geo.Geocache;
import cmanager.geo.Waypoint;
import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.util.FileHelper;
import cmanager.xml.Element;
import cmanager.xml.Parser;
import cmanager.xml.XmlAttribute;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Collection of methods for handling GPX files. */
public class Gpx {

    /** The number of geocaches to write into each GPX file. */
    private static final int CACHES_PER_GPX = 1000;

    /**
     * Load the GPX file data from the given stream input the given lists.
     *
     * @param inputStream The stream with the GPX input data.
     * @param geocaches The list of geocaches to add the read geocaches to.
     * @param waypoints The list of waypoints to add the read waypoints to.
     * @throws IllegalArgumentException Something went wrong when determining the number of threads
     *     to use.
     * @throws MalFormedException The given GPX file is not a valid XML file.
     * @throws IOException Something went wrong while reading the data.
     */
    public static void loadFromStream(
            InputStream inputStream, final List<Geocache> geocaches, final List<Waypoint> waypoints)
            throws Throwable {
        final ExecutorService service =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        Parser.parse(inputStream, new GpxParserCallback(service, geocaches, waypoints) {});

        service.shutdown();

        // Incredible high delay but still ugly.
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    /**
     * Save the given geocache list to the given GPX file.
     *
     * @param list The list of geocaches to write to the file.
     * @param listName The name of the geocache list.
     * @param pathToGpx The path to the GPX file to write to.
     * @throws Throwable Something went wrong while writing the data.
     */
    public static void saveToFile(
            final List<Geocache> list, String listName, final String pathToGpx) throws Throwable {
        // The output will be a ZIP file containing multiple GPX files.
        OutputStream outputStream = FileHelper.openFileWrite(pathToGpx);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setLevel(7);

        // Strip an optional `.zip` extension from the list name.
        if (FileHelper.getFileExtension(listName).equals("zip")) {
            listName = listName.substring(0, listName.length() - 4);
        }

        int subListNumber = 0;
        int baseIndex = 0;
        final boolean useSingleFile = list.size() <= CACHES_PER_GPX;
        do {
            final List<Geocache> subList = new ArrayList<>(CACHES_PER_GPX);

            // Create the sub-list with not more than `CACHES_PER_GPX` geocaches.
            for (int index = 0;
                    index < CACHES_PER_GPX && index + baseIndex < list.size();
                    index++) {
                subList.add(list.get(index + baseIndex));
            }
            baseIndex += CACHES_PER_GPX;
            subListNumber += 1;

            // Determine the name of the sub-list file, then add this file to the ZIP file.
            final String subListFileName =
                    useSingleFile ? listName : listName + "-" + subListNumber + ".gpx";
            zipOutputStream.putNextEntry(new ZipEntry(subListFileName));

            final Element root = cacheListToXml(subList, listName);
            Parser.xmlToBuffer(root, zipOutputStream);

            zipOutputStream.closeEntry();
        } while (baseIndex < list.size());

        zipOutputStream.close();
        outputStream.close();
    }

    /**
     * Serialize the given geocache list to a GPX/XML structure.
     *
     * @param list The list of geocaches to serialize.
     * @param name The name of the geocache list.
     */
    private static Element cacheListToXml(final List<Geocache> list, final String name) {
        final Element root = new Element();

        // Basic structure, schemes and namespaces.
        final Element gpx = new Element("gpx");
        gpx.add(new XmlAttribute("version", "1.0"));
        gpx.add(new XmlAttribute("creator", Constants.APP_NAME));
        gpx.add(
                new XmlAttribute(
                        "xsi:schemaLocation",
                        "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd http://www.gsak.net/xmlv1/6 http://www.gsak.net/xmlv1/6/gsak.xsd"));
        gpx.add(new XmlAttribute("xmlns", "http://www.topografix.com/GPX/1/0"));
        gpx.add(new XmlAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
        gpx.add(new XmlAttribute("xmlns:groundspeak", "http://www.groundspeak.com/cache/1/0/1"));
        gpx.add(new XmlAttribute("xmlns:gsak", "http://www.gsak.net/xmlv1/6"));
        gpx.add(new XmlAttribute("xmlns:cgeo", "http://www.cgeo.org/wptext/1/0"));
        root.add(gpx);

        // Add some metadata.
        gpx.add(new Element("name", name));
        gpx.add(
                new Element(
                        "desc",
                        "Geocache file generated by "
                                + Constants.APP_NAME
                                + " "
                                + Version.VERSION));
        gpx.add(new Element("author", Constants.APP_NAME));

        // Add the current timestamp.
        final ZonedDateTime dateTime = ZonedDateTime.now();
        final String dateString = dateTime.format(DateTimeFormatter.ISO_INSTANT);
        gpx.add(new Element("time", dateString));

        // Add the geocaches.
        for (final Geocache geocache : list) {
            gpx.add(GpxElementUtil.cacheToXml(geocache));
            for (final Waypoint waypoint : geocache.getWaypoints()) {
                gpx.add(GpxElementUtil.waypointToXml(waypoint));
            }
        }

        return root;
    }
}
