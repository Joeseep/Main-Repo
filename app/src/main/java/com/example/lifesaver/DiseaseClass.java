package com.example.lifesaver;

public class DiseaseClass {
    private String name, description, imageURL, cause, symptoms, firstaid, prevention, videoURL;

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getPrevention() {
        return prevention;
    }

    public void setPrevention(String prevention) {
        this.prevention = prevention;
    }

    public String getFirstaid() {
        return firstaid;
    }

    public void setFirstaid(String firstaid) {
        this.firstaid = firstaid;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public DiseaseClass(String name, String description, String imageURL, String cause, String symptoms, String firstaid, String prevention, String videoURL) {
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.cause = cause;
        this.symptoms = symptoms;
        this.firstaid = firstaid;
        this.prevention = prevention;
        this.videoURL = videoURL;
    }
    public DiseaseClass(){

    }
}
