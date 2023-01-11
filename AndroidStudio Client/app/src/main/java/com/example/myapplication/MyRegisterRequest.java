package com.example.myapplication;

public class MyRegisterRequest {
    private final String email;
    private final String age;
    private final String fullName;
    private final String password;

    public MyRegisterRequest(String email, String age, String fullName, String password) {
        this.email = email;
        this.age = age;
        this.fullName = fullName;
        this.password = password;
    }
}
