package com.sahabatpnj.pemandu.model;


public class User {

    private String userId;
    private String name;
    private String role;
    private String location;
    private String about;
    private String image;
    private String email;
    private String password;

    public User(String userId, String name, String role, String location, String about, String image, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.location = location;
        this.about = about;
        this.image = image;
        this.email = email;
        this.password = password;
    }

    public User() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
