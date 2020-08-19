package cmanager.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Abstraction for buffered reading. */
class BufferReadAbstraction {

    /** The maximum size of the buffer. */
    private final int LIMIT = 1024 * 1024 * 10;

    /** Internal character buffer used during reading data from the abstracted buffer. */
    private final char[] characterBuffer = new char[LIMIT];

    /** The internal buffered reader. */
    private final BufferedReader bufferedReader;

    /**
     * Create a new instance to read the given stream in a buffered way.
     *
     * @param inputStream The stream to work on.
     */
    public BufferReadAbstraction(InputStream inputStream) {
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8), LIMIT);
    }

    /**
     * Create a new instance to read the given string in a buffered way.
     *
     * @param string The string to work on.
     */
    public BufferReadAbstraction(final String string) {
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)),
                                StandardCharsets.UTF_8));
    }

    /**
     * Get the character at the given position.
     *
     * <p>This is the same as reading the first `index` characters from the buffer and only
     * retrieving the last character of it afterwards.
     *
     * @param index The index of the character to get.
     * @return The requested character.
     * @throws IOException Retrieving the character is not possible.
     */
    public char charAt(final int index) throws IOException {
        bufferedReader.mark(index + 1);
        bufferedReader.read(characterBuffer, 0, index + 1);
        bufferedReader.reset();

        return characterBuffer[index];
    }

    /**
     * Check whether the buffer is ready to be read.
     *
     * @return Whether the buffer is ready to be read.
     * @throws IOException Accessing the buffer is not possible.
     */
    public boolean available() throws IOException {
        return bufferedReader.ready();
    }

    /**
     * Skip the next character.
     *
     * @throws IOException Skipping the character is not possible.
     */
    public void deleteChar() throws IOException {
        bufferedReader.skip(1);
    }

    /**
     * Skip the given amount of characters.
     *
     * @param end The number of characters to skip.
     * @throws IOException Skipping the characters has not been completely successful.
     */
    public void deleteUntil(final int end) throws IOException {
        bufferedReader.skip(end);
    }

    /**
     * Get the requested substring. The buffer will start with the next character after the end
     * position afterwards.
     *
     * @param start The index to start at.
     * @param end The index to end with.
     * @return The requested substring.
     * @throws IOException Retrieving the substring is not possible.
     */
    public String substring(final int start, final int end) throws IOException {
        bufferedReader.mark(end + 1);
        bufferedReader.read(characterBuffer, 0, end + 1);
        bufferedReader.reset();

        return new String(characterBuffer, start, end - start);
    }

    /**
     * Determine the index of the given string. The buffer will start at the limit afterwards.
     *
     * @param str The string to search for.
     * @return The requested index or <code>-1</code> if there has not been any match.
     * @throws IOException Determining the index has not been possible.
     */
    public int indexOf(final String str) throws IOException {
        // Mark the end of the buffer.
        bufferedReader.mark(LIMIT);

        int offset = 0;
        int size = 200;

        while (true) {
            // We have reached the end of the buffer.
            if (offset + size > LIMIT) {
                bufferedReader.reset();
                return -1;
            }

            // Read the specified number of characters.
            final int read = bufferedReader.read(characterBuffer, offset, size);
            offset += read;
            size = size * 2;

            // Check for a match.
            final int len = str.length();
            for (int j = 0; j < offset; j++) {
                if (characterBuffer[j] == str.charAt(0)) {
                    boolean match = true;
                    for (int i = 1; i < len && j + i < offset; i++) {
                        if (characterBuffer[j + i] != str.charAt(i)) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        bufferedReader.reset();
                        return j;
                    }
                }
            }
        }
    }

    /**
     * Convert the buffer to a string builder.
     *
     * @return The string builder for the current buffer.
     * @throws IOException Converting the buffer failed.
     */
    public StringBuilder toStringBuilder() throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        final char[] buffer = new char[1024 * 1024];
        int readChars;
        while ((readChars = bufferedReader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, readChars);
        }
        return stringBuilder;
    }

    /**
     * Get up to <code>max</code> characters from the start of the buffer.
     *
     * @param max The maximum number of characters to return.
     * @return The head of the current buffer, with not more than <code>max</code> characters.
     * @throws IOException Retrieving the head failed.
     */
    public String getHead(int max) throws IOException {
        max = Math.min(max, LIMIT - 1);

        bufferedReader.mark(max);
        max = bufferedReader.read(characterBuffer, 0, max);
        bufferedReader.reset();

        return new String(characterBuffer, 0, max);
    }
}
