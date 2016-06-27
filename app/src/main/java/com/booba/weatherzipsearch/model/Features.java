package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Features implements Serializable {

    @SerializedName("conditions")
    private int conditions;

    public int getConditions() {
        return conditions;
    }


}