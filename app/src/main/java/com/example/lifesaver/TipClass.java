package com.example.lifesaver;

public class TipClass {
    private String title, description, image, videoTIP;

    public String getTitle() {
        return title;
    }

    public String getVideoTIP() {
        return videoTIP;
    }

    public void setVideoTIP(String videoTIP) {
        this.videoTIP = videoTIP;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public TipClass(String title, String description, String image, String videoTIP) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.videoTIP = videoTIP;
    }
    public TipClass(){

    }
}
