package cmanager.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class BufferReadAbstraction {

    private final int LIMIT = 1024 * 1024 * 10;
    private final char[] characterBuffer = new char[LIMIT];
    private final BufferedReader bufferedReader;

    public BufferReadAbstraction(InputStream inputStream) {
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8), LIMIT);
    }

    public BufferReadAbstraction(String string) {
        bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)),
                                StandardCharsets.UTF_8));
    }

    public char charAt(int index) throws IOException {
        bufferedReader.mark(index + 1);
        bufferedReader.read(characterBuffer, 0, index + 1);
        bufferedReader.reset();

        return characterBuffer[index];
    }

    public boolean available() throws IOException {
        return bufferedReader.ready();
    }

    public void deleteChar() throws IOException {
        bufferedReader.skip(1);
    }

    public void deleteUntil(int end) throws IOException {
        bufferedReader.skip(end);
    }

    public String substring(int start, int end) throws IOException {
        bufferedReader.mark(end + 1);
        bufferedReader.read(characterBuffer, 0, end + 1);
        bufferedReader.reset();

        return new String(characterBuffer, start, end - start);
    }

    public int indexOf(String str) throws IOException {
        bufferedReader.mark(LIMIT);
        int offset = 0;
        int size = 200;

        while (true) {
            if (offset + size > LIMIT) {
                bufferedReader.reset();
                return -1;
            }
            final int read = bufferedReader.read(characterBuffer, offset, size);
            offset += read;
            size = size * 2;

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

    public StringBuilder toStringBuilder() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final char[] buffer = new char[1024 * 1024];
        int readChars;
        while ((readChars = bufferedReader.read(buffer)) > 0) {
            sb.append(buffer, 0, readChars);
        }
        return sb;
    }

    public String getHead(int max) throws IOException {
        max = Math.min(max, LIMIT - 1);

        bufferedReader.mark(max);
        max = bufferedReader.read(characterBuffer, 0, max);
        bufferedReader.reset();

        return new String(characterBuffer, 0, max);
    }
}
