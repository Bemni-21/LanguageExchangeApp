package com.example.languageexchange;
public class UserM {
    private String name;
    private String username;
    private String profilePictureUrl;
    private String email;


    public UserM() {

    }

    public UserM(String name, String username, String profilePictureUrl, String email) {
        this.name = name;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
