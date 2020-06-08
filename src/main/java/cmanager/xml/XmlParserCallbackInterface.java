package cmanager.xml;

public interface XmlParserCallbackInterface {

    boolean elementLocatedCorrectly(Element element, Element parent);

    boolean elementFinished(Element element);
}
