package com.example.smester_project_lms;
import javafx.scene.image.ImageView;

public class booksTable {
    private ImageView image;
    private String name;
    private String author;
    private String issuance;
    private String dateField;

    public booksTable(ImageView image, String name, String author, String issuance, String dateCreated) {
        this.image = image;
        this.name = name;
        this.author = author;
        this.issuance = issuance;
        this.dateField = dateCreated;
    }

    public ImageView getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getIssuance() {
        return issuance;
    }

    public String getDateField() {
        return dateField;
    }
}
