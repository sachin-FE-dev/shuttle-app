package com.hackathon.metropolisshuttle.model;

import java.util.List;

public class Data {
    private int id;
    private String name;
    private String color;
    private List<Stop> stops;

    public Data(int id, String name, String color, List<Stop> stops) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stops = stops;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stops=" + stops +
                '}';
    }
}
