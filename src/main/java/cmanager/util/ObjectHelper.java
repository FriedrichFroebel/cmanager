package cmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** Utility class for working with plain objects. */
public class ObjectHelper {

    /**
     * Create a copy of the given object.
     *
     * <p>This will serialize the given object and deserialize it into a new instance afterwards.
     *
     * @param o The object to create a copy of.
     * @return The created copy.
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(T o) {
        T result = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            final byte[] byteData = byteArrayOutputStream.toByteArray();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
            result = (T) new ObjectInputStream(byteArrayInputStream).readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return result;
    }

    /**
     * Check whether the given objects are equal.
     *
     * @param object1 The first object.
     * @param object2 The second object.
     * @return Whether the two objects are equal.
     */
    static boolean areEqual(Object object1, Object object2) {
        return !(object1 == null || !object1.equals(object2));
    }

    /**
     * Decide on the best of the given objects.
     *
     * <p>This will choose the first object if the two objects are equal, otherwise the second
     * object will be chosen.
     *
     * @param object1 The first object.
     * @param object2 The second object.
     * @return The best of the given objects.
     */
    public static <T> T getBest(T object1, T object2) {
        return areEqual(object1, object2) ? object1 : object2;
    }
}
