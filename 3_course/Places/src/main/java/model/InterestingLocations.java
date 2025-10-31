package model;

public class InterestingLocations {
    private String id;
    private String name;
    private String type;

    public InterestingLocations(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + type;
    }
}

