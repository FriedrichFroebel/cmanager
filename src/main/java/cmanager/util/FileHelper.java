package cmanager;

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

public class FileHelper {

    public static <T extends Serializable> T deserialize(InputStream inputStream)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        @SuppressWarnings("unchecked")
        T object = (T) objectInputStream.readObject();
        objectInputStream.close();
        return object;
    }

    public static <T extends Serializable> T deserializeFromFile(String path)
            throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(path);
        T object = deserialize(fileInputStream);
        fileInputStream.close();
        return object;
    }

    public static void serialize(Serializable serializable, OutputStream outputStream)
            throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();
    }

    public static void serializeToFile(Serializable serializable, String path) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        serialize(serializable, fileOutputStream);
        fileOutputStream.close();
    }

    public static void processFiles(String path, FileHelper.InputAction inputAction)
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

    private static void processGZipFile(String path, FileHelper.InputAction inputAction)
            throws Throwable {
        processGZipFile(new FileInputStream(path), inputAction);
    }

    private static void processGZipFile(InputStream inputStream, FileHelper.InputAction inputAction)
            throws Throwable {
        // Get the gzip file content.
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        inputAction.process(gzipInputStream);
        gzipInputStream.close();
    }

    private static void processZipFile(String path, FileHelper.InputAction inputAction)
            throws Throwable {
        processZipFile(new FileInputStream(path), inputAction);
    }

    private static void processZipFile(InputStream inputStream, FileHelper.InputAction inputAction)
            throws Throwable {
        // Get the zip file content.
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
        // zis.close();	// Crashes on recursion.
    }

    private static void processFile(String path, FileHelper.InputAction inputAction)
            throws Throwable {
        inputAction.process(new FileInputStream(path));
    }

    public static OutputStream openFileWrite(String path) throws IOException {
        return new FileOutputStream(path);
    }

    public static void closeFileWrite(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        final int lastPeriodPosition = fileName.lastIndexOf('.');
        if (lastPeriodPosition > 0) {
            extension = fileName.substring(lastPeriodPosition + 1);
        }
        return extension.toLowerCase();
    }

    public abstract static class InputAction {
        public abstract void process(InputStream inputStream) throws Throwable;
    }
}
