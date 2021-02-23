package com.wallring.Model;

import java.util.List;

public class SavedImage {

    private String image_URL;


    public SavedImage(String image_URL) {
        this.image_URL = image_URL;
    }

    public SavedImage(){}



    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }
}
