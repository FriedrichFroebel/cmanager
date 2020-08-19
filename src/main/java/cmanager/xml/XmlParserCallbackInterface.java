package cmanager.xml;

/** Callback interface for parsing XML data. */
public interface XmlParserCallbackInterface {

    /**
     * Check whether the given XML element is located correctly.
     *
     * @param element The element to check the location for.
     * @param parent The parent element.
     * @return Whether the given XML element is located correctly.
     */
    boolean elementLocatedCorrectly(Element element, Element parent);

    /**
     * Indicate whether the given XML element is finished.
     *
     * @param element The element to handle.
     * @return Whether handling the element has been finished.
     */
    boolean elementFinished(Element element);
}
