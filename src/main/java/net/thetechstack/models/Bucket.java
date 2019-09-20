package net.thetechstack.models;


import javafx.beans.property.SimpleStringProperty;

public class Bucket {
    private final SimpleStringProperty name;
    private final SimpleStringProperty creationDate;

    public Bucket(String name, String creationDate) {
        this.name = new SimpleStringProperty(name);
        this.creationDate = new SimpleStringProperty(creationDate);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCreationDate() {
        return creationDate.get();
    }

    public void setCreationDate(String creationDate) {
        this.creationDate.set(creationDate);
    }
}