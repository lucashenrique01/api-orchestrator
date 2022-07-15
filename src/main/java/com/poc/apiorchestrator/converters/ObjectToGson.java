package com.poc.apiorchestrator.converters;

import com.google.gson.Gson;
import com.poc.apiorchestrator.dto.Event;

public class ObjectToGson {

    public String eventToJson(Event event){
        Gson gson = new Gson();
        String jsonString = gson.toJson(event);
        return jsonString;
    }
}
