package model;

public class Storemodel {
    private String name;
    private String roundname;
    private String description;
    private String distance;
    private int image;

    public Storemodel() {
    }

    public Storemodel(String roundname) {
        this.roundname = roundname;
    }

    public String getRoundname() {
        return roundname;
    }

    public void setRoundname(String roundname) {
        this.roundname = roundname;
    }

    public Storemodel(String name, String description, String distance, int image) {
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
