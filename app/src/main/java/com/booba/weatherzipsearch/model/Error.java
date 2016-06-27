package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Error implements Serializable{

    @SerializedName("type")

    private String type;
    @SerializedName("description")

    private String description;

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }


}
