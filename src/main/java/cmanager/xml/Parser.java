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

/** Parser for XML files. */
public class Parser {

    /**
     * Parse the given XML element string.
     *
     * @param element The element string to parse.
     * @return The parsed element.
     * @throws MalFormedException The given element is malformed.
     * @throws IOException Something went wrong when reading/writing data.
     */
    public static Element parse(final String element) throws MalFormedException, IOException {
        return parse(new BufferReadAbstraction(element), null);
    }

    /**
     * Parse the given XML data using the given callback.
     *
     * @param inputStream The stream to get the data from.
     * @param callback The callback to use.
     * @return The parsed element.
     * @throws MalFormedException The given element is malformed.
     * @throws IOException Something went wrong when reading/writing data.
     */
    public static Element parse(InputStream inputStream, XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        return parse(new BufferReadAbstraction(inputStream), callback);
    }

    /**
     * Parse the given XML data using the given callback.
     *
     * @param element The buffer to get the data from.
     * @param callback The callback to use.
     * @return The parsed element.
     * @throws MalFormedException The given element is malformed.
     * @throws IOException Something went wrong when reading/writing data.
     */
    private static Element parse(
            final BufferReadAbstraction element, XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        final Element root = new Element();

        do {
            removeDelimiter(element);

            // <?xml version="1.0" encoding="utf-8"?>
            if (element.substring(0, 5).equals("<?xml")) {
                final int index = element.indexOf("?>");
                element.deleteUntil(index + 2);
            }

            removeDelimiter(element);

            // <!DOCTYPE ... >
            if (element.substring(0, 9).equals("<!DOCTYPE")) {
                final int index = element.indexOf(">");
                element.deleteUntil(index + 1);
            }

            // Parse the children.
            parse(element, root, callback);

            removeDelimiter(element);
        } while (element.available());

        return root;
    }

    /**
     * Parse the given XML data using the given callback and the specified root element.
     *
     * @param element The buffer to get the data from.
     * @param root The root element to add the parsed data to.
     * @param callback The callback to use.
     * @throws MalFormedException The given element is malformed.
     * @throws IOException Something went wrong when reading/writing data.
     */
    private static void parse(
            final BufferReadAbstraction element,
            final Element root,
            XmlParserCallbackInterface callback)
            throws MalFormedException, IOException {
        removeDelimiter(element);

        // Each tag has to start with a `<`.
        if (element.charAt(0) != '<') {
            throw new MalFormedException();
        }

        // This is a closing tag, starting with `</`.
        if (element.charAt(1) == '/') {
            return;
        }

        final Element outputElement = new Element();

        // Retrieve the element name/tag.
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

                // Parse the next elements.
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

            // Add the attribute.
            final XmlAttribute attribute = new XmlAttribute(attributeName);
            attribute.setValue(StringEscapeUtils.unescapeXml(attributeValue));
            outputElement.getAttributes().add(attribute);
        }
        element.deleteChar();

        // Parse the body.
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

            // Check if we have children or not.
            if (element.charAt(1) == '/') {
                // We do not have children, so we can exit the loop afterwards.
                element.deleteChar();
                element.deleteChar();

                if (!element.substring(0, elementName.length() + 1).equals(elementName + ">")) {
                    throw new MalFormedException();
                }
                element.deleteUntil(elementName.length() + 1);

                break;
            } else {
                // There are some children, as the current tag is not closed.
                parse(element, outputElement, callback);
            }
        }

        if (callback != null && !callback.elementLocatedCorrectly(outputElement, root)) {
            throw new MalFormedException();
        }

        // Add the new element as a child of the root.
        if (callback == null || !callback.elementFinished(outputElement)) {
            root.getChildren().add(outputElement);
        }
    }

    /**
     * Trim the given string by removing delimiters.
     *
     * @param stringBuilder The string builder to work on.
     */
    static void trim(final StringBuilder stringBuilder) {
        removeDelimiter(stringBuilder);

        while (stringBuilder.length() > 0
                && isDelimiter(stringBuilder.charAt(stringBuilder.length() - 1))) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }

    /**
     * Find the end of the current tag name.
     *
     * @param bufferReadAbstraction The buffer to search inside.
     * @return The index of the end of the current tag name.
     * @throws IOException Something went wrong when reading the data.
     */
    static int endOfName(final BufferReadAbstraction bufferReadAbstraction) throws IOException {
        int i = 0;

        // The end has not been reached when there is no delimiter, closing `>` or `?>`.
        while (!isDelimiter(bufferReadAbstraction.charAt(i))
                && bufferReadAbstraction.charAt(i) != '>'
                && !(bufferReadAbstraction.charAt(i) == '?'
                        && bufferReadAbstraction.charAt(i + 1) == '>')) {
            i++;
        }

        return i;
    }

    /**
     * Check whether the given character is a delimiter.
     *
     * @param character The character to check for.
     * @return Whether the given character is a delimiter, id est a whitespace, newline or tab
     *     character.
     */
    static boolean isDelimiter(final char character) {
        return character == ' ' || character == '\n' || character == '\t' || character == '\r';
    }

    /**
     * Remove the leading delimiters from the given buffer.
     *
     * @param bufferReadAbstraction The buffer to work on.
     * @throws IOException Something went wrong when reading/writing data.
     */
    static void removeDelimiter(final BufferReadAbstraction bufferReadAbstraction)
            throws IOException {
        while (bufferReadAbstraction.available() && isDelimiter(bufferReadAbstraction.charAt(0))) {
            bufferReadAbstraction.deleteChar();
        }
    }

    /**
     * Remove the leading delimiters from the given string.
     *
     * @param stringBuilder The string builder to work on.
     */
    static void removeDelimiter(final StringBuilder stringBuilder) {
        while (stringBuilder.length() > 0 && isDelimiter(stringBuilder.charAt(0))) {
            stringBuilder.deleteCharAt(0);
        }
    }

    /**
     * Write the given XML to the buffer.
     *
     * <p>This is the top-level method adding the `&lt;?xml ... ?&gt;` line.
     *
     * @param root The root of the XML tree.
     * @param outputStream The stream to write to.
     * @throws Throwable Something went wrong with the conversion.
     */
    public static void xmlToBuffer(final Element root, OutputStream outputStream) throws Throwable {
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

    /**
     * Shrink the given XML tree by removing elements without a body, attributes and children.
     *
     * @param element The tree root to work on.
     * @throws Throwable Something went wrong with shrinking the tree.
     */
    private static void shrinkXmlTree(final Element element) throws Throwable {
        if (element.getChildren().size() < 100) {
            // If the tree is small enough, use one thread only.
            for (final Element child : element.getChildren()) {
                shrinkXmlTree(child);
            }
        } else {
            // If the tree is too large, use multiple threads.
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

        // Remove unused children.
        element.getChildren()
                .removeIf(
                        child ->
                                child.getUnescapedBody() == null
                                        && child.getAttributes().size() == 0
                                        && child.getChildren().size() == 0);
    }

    /**
     * Shrink the given XML tree element by removing elements without a body, attributes and
     * children.
     *
     * @param element The tree element to work on.
     * @throws Throwable Something went wrong with shrinking the element.
     */
    private static void shrinkXmlElement(final Element element, final int start, final int end) {
        try {
            for (int i = start; i < end; i++) {
                shrinkXmlTree(element.getChildren().get(i));
            }
        } catch (Throwable throwable) {
            final Thread thread = Thread.currentThread();
            thread.getUncaughtExceptionHandler().uncaughtException(thread, throwable);
        }
    }

    /**
     * Write the given XML to the buffer.
     *
     * <p>This is the method for the actual XML tree.
     *
     * @param element The tree element to write.
     * @param bufferWriteAbstraction The buffer to write to.
     * @param level The current level inside the tree.
     * @throws Throwable Something went wrong with the conversion.
     */
    private static void xmlToBuffer(
            final Element element,
            final BufferWriteAbstraction bufferWriteAbstraction,
            final int level)
            throws Throwable {
        final String name = element.getName();

        // Add the tag name including the attributes.
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

        // Close the tag and add the body.
        if (element.getUnescapedBody() == null && element.getChildren().size() == 0) {
            // This element has no children and body, so close the element directly.
            bufferWriteAbstraction.append(" />\n");
        } else {
            // This element has children and/or a body, so handle them.

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

    /**
     * Write the given XML element to The buffer.
     *
     * <p>This will write the given child elements to the buffer.
     *
     * @param element The tree element to write.
     * @param start The first child element to write from the current element.
     * @param end The last child element to write from the current element.
     * @param level The current level inside the tree.
     * @param bufferWriteAbstraction The buffer to write to.
     * @throws Throwable Something went wrong with the conversion.
     */
    private static void xmlElementToBuffer(
            final Element element,
            final int start,
            final int end,
            final int level,
            final BufferWriteAbstraction bufferWriteAbstraction) {
        try {
            StringBufferWriteAbstraction bufferWriteAbstractionThread =
                    new StringBufferWriteAbstraction(new StringBuilder());
            for (int i = start; i < end; i++) {
                final Element child = element.getChildren().get(i);
                xmlToBuffer(child, bufferWriteAbstractionThread, level + 1);

                // Flush each 100 elements.
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

    /**
     * Append <code>factor * 2</code> spaces to the buffer.
     *
     * <p>This is mainly used in conjunction with the XML tree writer to indent the levels.
     *
     * @param bufferWriteAbstraction The buffer to write to.
     * @param factor The half of the number of spaces to write.
     * @throws IOException Something went wrong with writing the spaces.
     */
    private static void appendSpaces(
            final BufferWriteAbstraction bufferWriteAbstraction, final int factor)
            throws IOException {
        for (int i = 0; i < factor * 2; i++) {
            bufferWriteAbstraction.append(" ");
        }
    }
}
