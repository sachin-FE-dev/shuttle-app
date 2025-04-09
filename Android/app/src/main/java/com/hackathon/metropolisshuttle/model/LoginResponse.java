package com.hackathon.metropolisshuttle.model;

/**
 * Created by Kavya Shravan on 03/04/25.
 */
public class LoginResponse {
    private String token;
    private String role;

    private String routeId;
    private String routeColor;

    public LoginResponse(String token, String role, String routeId, String routeColor) {
        this.token = token;
        this.role = role;
        this.routeId = routeId;
        this.routeColor = routeColor;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", role='" + role + '\'' +
                ", routeId='" + routeId + '\'' +
                ", routeColor='" + routeColor + '\'' +
                '}';
    }
}

