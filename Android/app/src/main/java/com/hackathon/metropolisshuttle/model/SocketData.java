package com.hackathon.metropolisshuttle.model;

import com.hackathon.metropolisshuttle.Utils;

/**
 * Created by Kavya Shravan on 03/04/25.
 */
public class SocketData {
    private String shuttle_id;
    private double lat;
    private double lng;
    private String timestamp;

    public SocketData(double lat, double lng) {
        this.shuttle_id = "bus_101";
        this.lat = lat;
        this.lng = lng;
        this.timestamp = Utils.getUtcTimestamp();
    }

    public String getShuttle_id() {
        return shuttle_id;
    }

    public void setShuttle_id(String shuttle_id) {
        this.shuttle_id = shuttle_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SocketData{" +
                "shuttle_id='" + shuttle_id + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
