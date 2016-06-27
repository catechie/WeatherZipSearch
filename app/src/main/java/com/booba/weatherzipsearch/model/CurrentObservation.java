package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurrentObservation implements Serializable{


    @SerializedName("weather")  private String weather;

    @SerializedName("temp_f")
     private String tempF;

    @SerializedName("temp_c")  private String tempC;
    @SerializedName("relative_humidity")  private String relativeHumidity;
    @SerializedName("feelslike_f")   private String feelslikeF;
    @SerializedName("icon_url")     private String iconUrl;
    @SerializedName("display_location") private DisplayLocation displayLocation;
    @SerializedName("dewpoint_f") private String dewpointF;


    public DisplayLocation getDisplayLocation() {        return displayLocation;    }


    public String getWeather() {        return weather;    }


    public String getTempF() {        return tempF;    }



    public String getRelativeHumidity() {        return relativeHumidity;    }



    public String getDewpointF() {        return dewpointF;    }



    public String getFeelslikeF() {        return feelslikeF;    }



    public String getIconUrl() {        return iconUrl;    }


}