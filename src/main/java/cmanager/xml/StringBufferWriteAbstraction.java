package cmanager.xml;

class StringBufferWriteAbstraction extends BufferWriteAbstraction {

    private StringBuilder stringBuilder = null;

    @SuppressWarnings("unused")
    private StringBufferWriteAbstraction() {}

    public StringBufferWriteAbstraction(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public BufferWriteAbstraction append(String string) {
        stringBuilder.append(string);
        return this;
    }

    public String toString() {
        return stringBuilder.toString();
    }
}
