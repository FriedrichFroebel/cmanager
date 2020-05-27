package cmanager.xml;

import java.io.BufferedWriter;
import java.io.IOException;

class BufferedWriterWriteAbstraction extends BufferWriteAbstraction {

    private BufferedWriter bufferedWriter = null;

    @SuppressWarnings("unused")
    private BufferedWriterWriteAbstraction() {}

    public BufferedWriterWriteAbstraction(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    public BufferWriteAbstraction append(String string) throws IOException {
        bufferedWriter.write(string);
        return this;
    }

    @Override
    public String toString() {
        throw new IllegalAccessError();
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }
}
