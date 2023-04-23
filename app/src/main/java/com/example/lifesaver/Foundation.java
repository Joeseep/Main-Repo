package com.example.lifesaver;

public class Foundation {
    private String name;
    private String goal;
    private String logo;
    private String contact;
    private String email;
    private String website;
    private String address;

    public Foundation() {
        // Default constructor required for Firebase
    }

    public Foundation(String name, String goal, String logo, String contact, String email, String website, String address) {
        this.name = name;
        this.goal = goal;
        this.logo = logo;
        this.contact = contact;
        this.email = email;
        this.website = website;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getGoal() {
        return goal;
    }

    public String getLogo() {
        return logo;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }
    public String getWebsite() {
        return website;
    }
    public String getAddress() {
        return address;
    }
}

