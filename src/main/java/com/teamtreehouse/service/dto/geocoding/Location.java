package com.teamtreehouse.service.dto.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Location {
    @JsonProperty("lat") //Parses JSON response to/from this variable
    private double latitude;

    @JsonProperty("lng") //Parses JSON response to/from this variable
    private double longitude;

    public Location(){}

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
