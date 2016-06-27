package com.booba.weatherzipsearch.model;

/**
 * Created by donna on 6/23/2016.
 */


import java.io.Serializable;

public class WeatherData implements Serializable {


    private Response response;

    private CurrentObservation current_observation;

    public Response getResponse() {     return response;   }

    public CurrentObservation getCurrentObservation() {   return current_observation;    }

}
