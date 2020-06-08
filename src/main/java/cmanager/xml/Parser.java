package cmanager.xml;

import cmanager.exception.MalFormedException;
import cmanager.util.ThreadStore;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.commons.text.StringEscapeUtils;

public class Parser {

    public static Element parse(String element) throws MalFormedException, IOException {
        return parse(new BufferReadAbstraction(element), null);
    }

    public static Element parse(InputStream inputStream, XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        return parse(new BufferReadAbstraction(inputStream), callback);
    }

    private static Element parse(BufferReadAbstraction element, XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        final Element root = new Element();
        do {
            removeDelimiter(element);
            if (element.substring(0, 5).equals("<?xml")) {
                final int index = element.indexOf("?>");
                element.deleteUntil(index + 2);
            }
            removeDelimiter(element);
            if (element.substring(0, 9).equals("<!DOCTYPE")) {
                final int index = element.indexOf(">");
                element.deleteUntil(index + 1);
            }
            parse(element, root, callback);
            removeDelimiter(element);
        } while (element.available());

        return root;
    }

    private static void parse(
            BufferReadAbstraction element, Element root, XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        removeDelimiter(element);
        if (element.charAt(0) != '<') {
            throw new MalFormedException();
        }
        if (element.charAt(1) == '/') {
            return;
        }

        final Element outputElement = new Element();

        final int nameEnd = endOfName(element);
        final String elementName = element.substring(1, nameEnd);
        element.deleteUntil(nameEnd);
        outputElement.setName(elementName);

        // Parse attributes.
        removeDelimiter(element);
        while (element.charAt(0) != '>') {
            removeDelimiter(element);

            // Catch /> endings.
            if ((element.charAt(0) == '/' && element.charAt(1) == '>')) {
                element.deleteChar();
                element.deleteChar();

                parse(element, root, callback);

                if (callback != null && !callback.elementLocatedCorrectly(outputElement, root)) {
                    throw new MalFormedException();
                }

                if (callback == null || !callback.elementFinished(outputElement)) {
                    root.getChildren().add(outputElement);
                }
                return;
            }

            // Tag is not closed => an attribute is following.
            int index = element.indexOf("=");
            final String attributeName = element.substring(0, index);
            element.deleteUntil(index + 1);

            String attributeValue;
            final char marking = element.charAt(0);
            if (marking == '"' || marking == '\'') {
                element.deleteChar();
                index = element.indexOf(String.valueOf(marking));
                attributeValue = element.substring(0, index);
                element.deleteUntil(index + 1);
            } else {
                throw new MalFormedException();
            }

            final XmlAttribute attribute = new XmlAttribute(attributeName);
            attribute.setValue(StringEscapeUtils.unescapeXml(attributeValue));
            outputElement.getAttributes().add(attribute);
        }
        element.deleteChar();

        while (true) {
            final int startOfName = element.indexOf("<");
            if (startOfName == -1) {
                final StringBuilder elementTemp = element.toStringBuilder();
                trim(elementTemp);
                if (elementTemp.length() == 0) {
                    break;
                } else {
                    throw new MalFormedException();
                }
            }

            final StringBuilder body = new StringBuilder(element.substring(0, startOfName));
            trim(body);
            outputElement.setBody(body.toString());

            element.deleteUntil(startOfName);

            if (element.charAt(1) == '/') {
                element.deleteChar();
                element.deleteChar();

                if (!element.substring(0, elementName.length() + 1).equals(elementName + ">")) {
                    throw new MalFormedException();
                }
                element.deleteUntil(elementName.length() + 1);

                break;
            } else {
                parse(element, outputElement, callback);
            }
        }

        if (callback != null && !callback.elementLocatedCorrectly(outputElement, root)) {
            throw new MalFormedException();
        }

        if (callback == null || !callback.elementFinished(outputElement)) {
            root.getChildren().add(outputElement);
        }
    }

    static void trim(StringBuilder stringBuilder) {
        removeDelimiter(stringBuilder);

        while (stringBuilder.length() > 0
                && isDelimiter(stringBuilder.charAt(stringBuilder.length() - 1))) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }

    static int endOfName(BufferReadAbstraction bufferReadAbstraction) throws IOException {
        int i = 0;
        while (!isDelimiter(bufferReadAbstraction.charAt(i))
                && bufferReadAbstraction.charAt(i) != '>'
                && !(bufferReadAbstraction.charAt(i) == '?'
                        && bufferReadAbstraction.charAt(i + 1) == '>')) {
            i++;
        }
        return i;
    }

    static boolean isDelimiter(char character) {
        return character == ' ' || character == '\n' || character == '\t' || character == '\r';
    }

    static void removeDelimiter(BufferReadAbstraction bufferReadAbstraction) throws IOException {
        while (bufferReadAbstraction.available() && isDelimiter(bufferReadAbstraction.charAt(0))) {
            bufferReadAbstraction.deleteChar();
        }
    }

    static void removeDelimiter(StringBuilder stringBuilder) {
        while (stringBuilder.length() > 0 && isDelimiter(stringBuilder.charAt(0))) {
            stringBuilder.deleteCharAt(0);
        }
    }

    public static void xmlToBuffer(Element root, OutputStream outputStream) throws Throwable {
        shrinkXmlTree(root);

        BufferedWriter bufferedWriter =
                new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        BufferWriteAbstraction bufferWriteAbstraction =
                new BufferedWriterWriteAbstraction(bufferedWriter);

        bufferWriteAbstraction.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n");
        for (final Element child : root.getChildren()) {
            xmlToBuffer(child, bufferWriteAbstraction, 0);
        }

        bufferedWriter.flush();
    }

    private static void shrinkXmlTree(final Element element) throws Throwable {
        if (element.getChildren().size() < 100) {
            for (final Element child : element.getChildren()) {
                shrinkXmlTree(child);
            }
        } else {
            final int listSize = element.getChildren().size();
            final ThreadStore threadStore = new ThreadStore();
            final int cores = threadStore.getCores(listSize);
            final int perProcess = listSize / cores;

            for (int core = 0; core < cores; core++) {
                final int start = perProcess * core;

                int temp = Math.min(perProcess * (core + 1), listSize);
                if (core == cores - 1) {
                    temp = listSize;
                }
                final int end = temp;

                threadStore.addAndRun(new Thread(() -> shrinkXmlElement(element, start, end)));
            }
            threadStore.joinAndThrow();
        }

        element.getChildren()
                .removeIf(
                        child ->
                                child.getUnescapedBody() == null
                                        && child.getAttributes().size() == 0
                                        && child.getChildren().size() == 0);
    }

    private static void shrinkXmlElement(final Element element, int start, int end) {
        try {
            for (int i = start; i < end; i++) {
                shrinkXmlTree(element.getChildren().get(i));
            }
        } catch (Throwable throwable) {
            final Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, throwable);
        }
    }

    private static void xmlToBuffer(
            final Element element,
            final BufferWriteAbstraction bufferWriteAbstraction,
            final int level)
            throws Throwable {
        final String name = element.getName();

        appendSpaces(bufferWriteAbstraction, level);
        bufferWriteAbstraction.append("<").append(name);
        for (final XmlAttribute attribute : element.getAttributes()) {
            if (attribute.getValue() != null) {
                bufferWriteAbstraction.append(" ").append(attribute.getName()).append("=\"");
                bufferWriteAbstraction
                        .append(StringEscapeUtils.escapeXml11(attribute.getValue()))
                        .append("\"");
            }
        }

        if (element.getUnescapedBody() == null && element.getChildren().size() == 0) {
            bufferWriteAbstraction.append(" />\n");
        } else {
            bufferWriteAbstraction.append(">");
            if (element.getChildren().size() != 0) {
                bufferWriteAbstraction.append("\n");
            }
            if (element.getChildren().size() > 200) {
                // Use multiple threads, if there are many children e.g. the children of "gpx".
                final int listSize = element.getChildren().size();
                final ThreadStore threadStore = new ThreadStore();
                final int cores = threadStore.getCores(listSize);
                final int perProcess = listSize / cores;

                for (int core = 0; core < cores; core++) {
                    final int start = perProcess * core;

                    int temp = Math.min(perProcess * (core + 1), listSize);
                    if (core == cores - 1) {
                        temp = listSize;
                    }
                    final int end = temp;

                    threadStore.addAndRun(
                            new Thread(
                                    new Runnable() {
                                        public void run() {
                                            xmlElementToBuffer(
                                                    element,
                                                    start,
                                                    end,
                                                    level,
                                                    bufferWriteAbstraction);
                                        }
                                    }));
                }
                threadStore.joinAndThrow();
            } else {
                for (final Element child : element.getChildren()) {
                    xmlToBuffer(child, bufferWriteAbstraction, level + 1);
                }
            }
            if (element.getUnescapedBody() != null) {
                bufferWriteAbstraction.append(
                        StringEscapeUtils.escapeXml11(element.getUnescapedBody()));
            } else {
                appendSpaces(bufferWriteAbstraction, level);
            }
            bufferWriteAbstraction.append("</").append(name).append(">\n");
        }
    }

    private static void xmlElementToBuffer(
            final Element element,
            int start,
            int end,
            int level,
            final BufferWriteAbstraction bufferWriteAbstraction) {
        try {
            StringBufferWriteAbstraction bufferWriteAbstractionThread =
                    new StringBufferWriteAbstraction(new StringBuilder());
            for (int i = start; i < end; i++) {
                final Element child = element.getChildren().get(i);
                xmlToBuffer(child, bufferWriteAbstractionThread, level + 1);

                // Flush each n elements
                if (i % 100 == 0) {
                    synchronized (bufferWriteAbstraction) {
                        bufferWriteAbstraction.append(bufferWriteAbstraction);
                    }
                    bufferWriteAbstractionThread =
                            new StringBufferWriteAbstraction(new StringBuilder());
                }
            }

            synchronized (bufferWriteAbstraction) {
                bufferWriteAbstraction.append(bufferWriteAbstraction);
            }
        } catch (Throwable throwable) {
            final Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, throwable);
        }
    }

    private static void appendSpaces(BufferWriteAbstraction bufferWriteAbstraction, int factor)
            throws IOException {
        for (int i = 0; i < factor * 2; i++) {
            bufferWriteAbstraction.append(" ");
        }
    }
}
