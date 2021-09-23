package com.shoppr.shoper.util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class ImageArr {
    @SerializedName("image")
    @Expose
    private File image;
    @SerializedName("id")
    @Expose
    private String id;

    public ImageArr() {
    }
    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
