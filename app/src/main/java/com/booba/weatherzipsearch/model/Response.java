package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Response implements Serializable{

    @SerializedName("version")    private String version;
    @SerializedName("termsofService")    private String termsofService;
    @SerializedName("features")    private Features features;
    @SerializedName("error")    private Error error;

    public String getVersion() {
        return version;
    }

    public Error getError() {        return error;    }

    public String getTermsofService() {
        return termsofService;
    }

    public Features getFeatures() {
        return features;
    }


}