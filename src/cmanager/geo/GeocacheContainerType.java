package cmanager.geo;

public class GeocacheContainerType
{
    public static final TMap CONTAINER = new TMap();
    static
    {
        CONTAINER.add("None");
        CONTAINER.add("Nano");
        CONTAINER.add("Micro");
        CONTAINER.add("Small");
        CONTAINER.add("Regular");
        CONTAINER.add("Large");
        CONTAINER.add("Xlarge");
        CONTAINER.add("Other");
        CONTAINER.add("Virtual");
        CONTAINER.add("Not chosen", "not_chosen");
    }


    private Integer container;

    public GeocacheContainerType(String type)
    {
        set(type);
    }

    public void set(String container)
    {
        if (container == null)
            return;
        container = container.toLowerCase();
        this.container = CONTAINER.getLC(container);
    }

    public String asGC()
    {
        if (container == null)
            return null;
        String s = CONTAINER.get(container, 0);
        return s;
    }

    public boolean equals(GeocacheContainerType other)
    {
        return container == other.container;
    }
}
