package com.example.myapplication.Models;

/**
 * User class getting for upload/download data that given from the firebase database.
 */
public class User {

    public String phoneNumber;
    private String fullName, age, email, uid, url;
    private boolean isAdmin;

/*
 *Different constructors for a different scenarios.
 */
    public User() {
        this.isAdmin = false;
    }
    public User(String fullName, String age, String email, String uid) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.uid = uid;
    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.isAdmin = false;
    }

    /*
     * Getters/Setters for User, used the most in profile class.
     */
    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public String getFullName() {return fullName;}

    public void setFullName(String fullName) {this.fullName = fullName;}

    public String getAge() {return age;}

    public void setAge(String age) {this.age = age;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", age='" + age + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }
}

