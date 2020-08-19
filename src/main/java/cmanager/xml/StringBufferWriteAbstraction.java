package cmanager.xml;

/** Abstraction for writing to a string buffer. */
class StringBufferWriteAbstraction extends BufferWriteAbstraction {

    /** The string buffer. */
    private StringBuilder stringBuilder = null;

    @SuppressWarnings("unused")
    private StringBufferWriteAbstraction() {}

    /**
     * Create a new instance with the given builder.
     *
     * @param stringBuilder The builder to use.
     */
    public StringBufferWriteAbstraction(final StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    /**
     * Append the given string to the buffer.
     *
     * @param string The string to append.
     * @return The current abstraction instance.
     */
    @Override
    public BufferWriteAbstraction append(final String string) {
        stringBuilder.append(string);
        return this;
    }

    /**
     * Convert the buffer to a string.
     *
     * @return The buffer content as a string.
     */
    public String toString() {
        return stringBuilder.toString();
    }
}
