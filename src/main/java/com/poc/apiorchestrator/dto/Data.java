package com.poc.apiorchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    @JsonProperty("idDocument")
    private String idDocument;

    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    @Override
    public String toString() {
        return "Data{" +
                "idDocument='" + idDocument + '\'' +
                '}';
    }
}
