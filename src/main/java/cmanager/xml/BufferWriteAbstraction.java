package cmanager.xml;

import java.io.IOException;

abstract class BufferWriteAbstraction {

    public abstract BufferWriteAbstraction append(String string) throws IOException;

    public abstract String toString();

    public BufferWriteAbstraction append(BufferWriteAbstraction bufferWriteAbstraction)
            throws IOException {
        return append(bufferWriteAbstraction.toString());
    }
}
