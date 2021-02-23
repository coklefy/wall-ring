package com.wallring.Model;

import java.util.List;

public class Request extends  SavedImage{
    String user_id;
    private List<SavedImage> savedImages;

    public Request( List<SavedImage> savedImages ) {
        this.savedImages = savedImages;
  }

    public Request() {
    }


    public List<SavedImage> getSavedImages() {
        return savedImages;
    }

    public void setSavedImages(List<SavedImage> savedImages) {
        this.savedImages = savedImages;
    }
}
