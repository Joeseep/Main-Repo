package com.example.lifesaver;

public class User {
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;

    public User(String fullName, String email, String address, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
