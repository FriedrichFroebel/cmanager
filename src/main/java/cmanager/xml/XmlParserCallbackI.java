package cmanager.xml;

public interface XmlParserCallbackI {

    boolean elementLocatedCorrectly(Element element, Element parent);

    boolean elementFinished(Element element);
}
