package cmanager.xml;

/** Container for a XML attribute. */
public class XmlAttribute {

    /** The name of the attribute. */
    private final String name;

    /** The value of the attribute. */
    private String value = null;

    /**
     * Create a new attribute without a value.
     *
     * @param name The attribute name.
     */
    public XmlAttribute(final String name) {
        this.name = name;
    }

    /**
     * Create a new attribute with a string value.
     *
     * @param name The attribute name.
     * @param value The attribute value as a string.
     */
    public XmlAttribute(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Create a new attribute with a double value.
     *
     * @param name The attribute name.
     * @param value The attribute value as a double value.
     */
    public XmlAttribute(final String name, final Double value) {
        this.name = name;
        this.value = value != null ? value.toString() : null;
    }

    /**
     * Create a new attribute with a boolean value.
     *
     * @param name The attribute name.
     * @param value The attribute value as a boolean value.
     */
    public XmlAttribute(final String name, final Boolean value) {
        this.name = name;
        this.value = value == null ? null : value ? "True" : "False";
    }

    /**
     * Create a new attribute with an integer value.
     *
     * @param name The attribute name.
     * @param value The attribute value as an integer value.
     */
    public XmlAttribute(final String name, final Integer value) {
        this.name = name;
        this.value = value != null ? value.toString() : null;
    }

    /**
     * Get the name of the attribute.
     *
     * @return The attribute name.
     */
    public String getName() {
        return name;
    }

    /**
     * Check whether the attribute has the given name.
     *
     * @param name The name to check for.
     * @return Whether the attribute has the given name.
     */
    public boolean is(final String name) {
        return this.name.equals(name);
    }

    /**
     * Check whether the attribute has the given name and value.
     *
     * @param name The name to check for.
     * @param value The value to check for.
     * @return Whether the attribute has the given name and value.
     */
    public boolean is(final String name, final String value) {
        return this.name.equals(name) && this.value.equals(value);
    }

    /**
     * Set the attribute value.
     *
     * @param value The value to set.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Get the attribute value.
     *
     * @return The attribute value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the value of the attribute as a double value.
     *
     * @return The attribute value as a double value.
     */
    public Double getValueDouble() {
        return Double.valueOf(value);
    }

    /**
     * Get the value of the attribute as an integer value.
     *
     * @return The attribute value as an integer value.
     */
    public Integer getValueInteger() {
        return Integer.valueOf(value);
    }
}
