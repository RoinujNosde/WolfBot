package me.roinujnosde.wolfbot.models.spiget;

public class Author {

    private final long id;
    private final String name;
    private final Icon icon;

    public Author(long id, String name, Icon icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }
}
