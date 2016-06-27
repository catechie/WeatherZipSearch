package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DisplayLocation implements Serializable{


    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;

    @SerializedName("zip")
    private String zip;




    public String getCity() {
        return city;
    }


    public String getState() {
        return state;
    }




    public String getZip() {
        return zip;
    }




}