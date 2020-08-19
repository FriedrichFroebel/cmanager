package cmanager.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Utility methods for handling files. */
public class FileHelper {

    /**
     * Deserialize the given data.
     *
     * @param inputStream The data to deserialize.
     * @return The deserialized data.
     * @throws ClassNotFoundException The corresponding class to deserialize the data could not be
     *     found.
     * @throws IOException Something went wrong with the deserialization.
     */
    public static <T extends Serializable> T deserialize(InputStream inputStream)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        @SuppressWarnings("unchecked")
        T object = (T) objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    /**
     * Deserialize the data from the given file.
     *
     * @param path The file to deserialize.
     * @return The deserialized data.
     * @throws ClassNotFoundException The corresponding class to deserialize the data could not be
     *     found.
     * @throws IOException Something went wrong with the deserialization.
     */
    public static <T extends Serializable> T deserializeFromFile(final String path)
            throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(path);
        T object = deserialize(fileInputStream);
        fileInputStream.close();
        return object;
    }

    /**
     * Serialize the given data.
     *
     * @param serializable The data to serialize.
     * @param outputStream The stream to serialize to.
     * @throws IOException Something went wrong with the serialization.
     */
    public static void serialize(final Serializable serializable, OutputStream outputStream)
            throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();
    }

    /**
     * Serialize the given data to the given file.
     *
     * @param serializable The data to serialize.
     * @param path The file to serialize to.
     * @throws IOException Something went wrong with the serialization.
     */
    public static void serializeToFile(final Serializable serializable, final String path)
            throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        serialize(serializable, fileOutputStream);
        fileOutputStream.close();
    }

    /**
     * Process the given file, depending on the extension.
     *
     * @param path The file to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    public static void processFiles(final String path, FileHelper.InputAction inputAction)
            throws Throwable {
        final String pathLowerCase = path.toLowerCase();
        if (pathLowerCase.endsWith(".zip")) {
            processZipFile(path, inputAction);
        } else if (pathLowerCase.endsWith(".gz")) {
            processGZipFile(path, inputAction);
        } else {
            processFile(path, inputAction);
        }
    }

    /**
     * Process the given GZIP file.
     *
     * @param path The file to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    private static void processGZipFile(final String path, FileHelper.InputAction inputAction)
            throws Throwable {
        processGZipFile(new FileInputStream(path), inputAction);
    }

    /**
     * Process the given GZIP data.
     *
     * @param inputStream The data to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    private static void processGZipFile(InputStream inputStream, FileHelper.InputAction inputAction)
            throws Throwable {
        // Get the GZIP file content.
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);

        inputAction.process(gzipInputStream);
        gzipInputStream.close();
    }

    /**
     * Process the given ZIP file.
     *
     * @param path The file to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    private static void processZipFile(final String path, FileHelper.InputAction inputAction)
            throws Throwable {
        processZipFile(new FileInputStream(path), inputAction);
    }

    /**
     * Process the given ZIP data.
     *
     * @param path The data to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    private static void processZipFile(InputStream inputStream, FileHelper.InputAction inputAction)
            throws Throwable {
        // Get the ZIP file content.
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // Get the zipped file list entry.
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            final String fileName = zipEntry.getName();
            if (fileName.toLowerCase().endsWith(".zip")) {
                processZipFile(zipInputStream, inputAction);
            } else {
                inputAction.process(zipInputStream);
            }

            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.closeEntry();

        // Crashes on recursion.
        // zis.close();
    }

    /**
     * Process the given (plain) file.
     *
     * @param path The file to process.
     * @param inputAction The actual data processor which takes care of saving the processed data as
     *     well.
     */
    private static void processFile(final String path, FileHelper.InputAction inputAction)
            throws Throwable {
        inputAction.process(new FileInputStream(path));
    }

    /**
     * Open the given file for writing.
     *
     * @param path The file to open.
     * @return The opened output stream.
     */
    public static OutputStream openFileWrite(final String path) throws IOException {
        return new FileOutputStream(path);
    }

    /**
     * Flush and close the given writer.
     *
     * @param bufferedWriter The writer to close.
     */
    public static void closeFileWrite(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    /**
     * Determine the file extension for the given file.
     *
     * @param fileName The file to determine the extension for.
     * @return The file extension.
     */
    public static String getFileExtension(final String fileName) {
        String extension = "";

        final int lastPeriodPosition = fileName.lastIndexOf('.');
        if (lastPeriodPosition > 0) {
            extension = fileName.substring(lastPeriodPosition + 1);
        }
        return extension.toLowerCase();
    }

    /** Data processing action. */
    public abstract static class InputAction {

        /**
         * Process the given data and take care of saving the processed data.
         *
         * @param inputStream The data to process.
         * @throws Throwable Something went wrong with processing the data.
         */
        public abstract void process(InputStream inputStream) throws Throwable;
    }
}
