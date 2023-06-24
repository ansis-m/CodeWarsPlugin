package com.example.codewarsplugin.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KataRecord(
  String id,
  String name,
  String slug,
  String url,
  String category,
  String[] languages) {
}
