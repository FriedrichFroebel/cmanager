package cmanager.xml;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

/** Container for a XML element. */
public class Element {

    /** The name of the element. */
    private String elementName = null;

    /** The element body. */
    private String body = null;

    /** The child elements of the element. */
    private final List<Element> children = new ArrayList<>();

    /** The attributes of the element. */
    private final List<XmlAttribute> attributes = new ArrayList<>();

    /** Create an unitialized element. */
    public Element() {}

    /**
     * Create an element with the given name.
     *
     * @param name The element name.
     */
    public Element(final String name) {
        setName(name);
    }

    /**
     * Create a named element with a string body.
     *
     * @param name The element name.
     * @param body The element body.
     */
    public Element(final String name, final String body) {
        setName(name);
        setBody(body);
    }

    /**
     * Create a named element with a double body.
     *
     * @param name The element name.
     * @param body The element body.
     */
    public Element(final String name, final Double body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    /**
     * Create a named element with an integer body.
     *
     * @param name The element name.
     * @param body The element body.
     */
    public Element(final String name, final Integer body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    /**
     * Create a named element with a boolean body.
     *
     * @param name The element name.
     * @param body The element body.
     */
    public Element(final String name, final Boolean body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    /**
     * Set the name of the element.
     *
     * @param name The name to set.
     */
    public void setName(final String name) {
        elementName = name;
    }

    /**
     * Get the name of the element.
     *
     * @return The element name.
     */
    public String getName() {
        return elementName;
    }

    /**
     * Check whether the element has the given name.
     *
     * @param name The element name/tag to check for.
     * @return Whether the element has the given name.
     */
    public boolean is(final String name) {
        return this.elementName.equals(name);
    }

    /**
     * Check whether the element has an attribute with the given value.
     *
     * @param attr The attribute name to search for.
     * @param is The attribute value to check.
     * @return Whether the element has an attribute with the given value.
     */
    public boolean attrIs(final String attr, final String is) {
        for (final XmlAttribute attribute : attributes) {
            if (attribute.is(attr, is)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add the given element as a child.
     *
     * @param child The element to add.
     */
    public void add(final Element child) {
        children.add(child);
    }

    /**
     * Get all child elements.
     *
     * @return The children.
     */
    public List<Element> getChildren() {
        return children;
    }

    /**
     * Get the (first) child element with the given name/tag.
     *
     * @param name The name to search for.
     * @return The requested element or <code>null</code> if it could not be found.
     */
    public Element getChild(final String name) {
        for (final Element element : children) {
            if (element.is(name)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Get all children with the given name.
     *
     * @param name To tag name to filter for.
     * @return The children matching the given tag name.
     */
    public List<Element> getChildren(final String name) {
        final List<Element> matching = new ArrayList<>();
        for (final Element element : children) {
            if (element.is(name)) {
                matching.add(element);
            }
        }
        return matching;
    }

    /**
     * Add the given attribute.
     *
     * @param attribute The attribute to add.
     */
    public void add(final XmlAttribute attribute) {
        attributes.add(attribute);
    }

    /**
     * Get all attributes.
     *
     * @return All attributes.
     */
    public List<XmlAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Set the body of the element.
     *
     * @param body The body to set.
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * Get the element body with all XML being unescaped.
     *
     * @return The element body with all XML being unescaped.
     */
    public String getUnescapedBody() {
        return body == null ? null : StringEscapeUtils.unescapeXml(body);
    }

    /**
     * Get the element body as a double value.
     *
     * @return The element body as a double value.
     */
    public Double getBodyDouble() {
        return Double.valueOf(body);
    }

    /**
     * Get the element body as an integer value.
     *
     * @return The element body as an integer value.
     */
    public Integer getBodyInteger() {
        return Integer.valueOf(body);
    }

    /**
     * Get the element body as a boolean value.
     *
     * @return The element body as a boolean value.
     */
    public Boolean getBodyBoolean() {
        return Boolean.valueOf(body);
    }
}
