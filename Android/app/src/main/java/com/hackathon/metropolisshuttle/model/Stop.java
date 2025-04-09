package com.hackathon.metropolisshuttle.model;

import java.util.List;

public class Stop {

    private String name;
    private List<Double> coords;

    public Stop(String name, List<Double> coords) {
        this.name = name;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getCoords() {
        return coords;
    }

    public void setCoords(List<Double> coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "name='" + name + '\'' +
                ", coords=" + coords +
                '}';
    }
}
