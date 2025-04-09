package com.hackathon.metropolisshuttle.model;

/**
 * Created by Kavya Shravan on 03/04/25.
 */
public class Register {

    private String name;
    private String password;
    private String email;
    private String role;

    public Register(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = "Driver";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Register{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
