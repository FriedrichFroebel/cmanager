package cmanager.xml;

import java.io.BufferedWriter;
import java.io.IOException;

/** Abstraction for writing to a buffered writer. */
class BufferedWriterWriteAbstraction extends BufferWriteAbstraction {

    /** The buffered writer. */
    private BufferedWriter bufferedWriter = null;

    @SuppressWarnings("unused")
    private BufferedWriterWriteAbstraction() {}

    /**
     * Create a new instance with the given writer.
     *
     * @param bufferedWriter The writer to use.
     */
    public BufferedWriterWriteAbstraction(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    /**
     * Append the given string to the buffer.
     *
     * @param string The string to append.
     * @return The current abstraction instance.
     * @throws IOException The string could not be appended.
     */
    @Override
    public BufferWriteAbstraction append(final String string) throws IOException {
        bufferedWriter.write(string);
        return this;
    }

    /** Converting this abstraction layer to a string is not supported. */
    @Override
    public String toString() {
        throw new IllegalAccessError();
    }

    /**
     * Get the underlying buffered writer.
     *
     * @return The buffered writer.
     */
    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
