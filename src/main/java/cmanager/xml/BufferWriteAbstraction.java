package cmanager.xml;

import java.io.IOException;

/** Abstraction for buffered writing. */
abstract class BufferWriteAbstraction {

    /**
     * Append the given string to the buffer.
     *
     * @param string The string to append.
     * @return The current abstraction instance.
     * @throws IOException The string could not be appended.
     */
    public abstract BufferWriteAbstraction append(String string) throws IOException;

    /**
     * Convert the buffer to a string.
     *
     * @return The buffer content as a string.
     */
    public abstract String toString();

    /**
     * Append the given buffer to the current buffer.
     *
     * @param bufferWriteAbstraction The buffer to append.
     * @return The current abstraction instance.
     * @throws IOException The buffer could not be appended.
     */
    public BufferWriteAbstraction append(BufferWriteAbstraction bufferWriteAbstraction)
            throws IOException {
        return append(bufferWriteAbstraction.toString());
    }
}
