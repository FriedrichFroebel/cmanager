package cmanager.xml;

public class XmlAttribute {

    private final String name;
    private String value = null;

    public XmlAttribute(String name) {
        this.name = name;
    }

    public XmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public XmlAttribute(String name, Double value) {
        this.name = name;
        this.value = value != null ? value.toString() : null;
    }

    public XmlAttribute(String name, Boolean value) {
        this.name = name;
        this.value = value == null ? null : value ? "True" : "False";
    }

    public XmlAttribute(String name, Integer value) {
        this.name = name;
        this.value = value != null ? value.toString() : null;
    }

    public String getName() {
        return name;
    }

    public boolean is(String name) {
        return this.name.equals(name);
    }

    public boolean is(String name, String value) {
        return this.name.equals(name) && this.value.equals(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Double getValueDouble() {
        return Double.valueOf(value);
    }

    public Integer getValueInteger() {
        return Integer.valueOf(value);
    }
}
