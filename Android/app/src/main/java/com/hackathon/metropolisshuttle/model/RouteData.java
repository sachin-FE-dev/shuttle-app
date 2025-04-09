package com.hackathon.metropolisshuttle.model;

import java.util.List;

/**
 * Created by Kavya Shravan on 04/04/25.
 */
public class RouteData {

    private boolean success;
    private List<Data> data;

    public RouteData(boolean success, List<Data> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RouteData{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}

