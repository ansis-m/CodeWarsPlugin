package com.example.codewarsplugin.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KataRecord {

    String id;
    String name;
    String slug;
    String url;
    String category;
    String[] languages;
}
