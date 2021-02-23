package com.wallring.Model;

public class Image {
    private String Name;
    private String Image;
    private String Description;
    private String CategoryID;


    public Image(String name, String image, String description, String price, String discount, String categoryID) {
        Name = name;
        Image = image;
        Description = description;
        CategoryID = categoryID;
    }

    public Image() {}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategoryID() { return CategoryID;}

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }
}
