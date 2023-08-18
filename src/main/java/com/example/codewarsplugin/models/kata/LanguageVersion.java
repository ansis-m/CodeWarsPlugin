package com.example.codewarsplugin.models.kata;

public class LanguageVersion {
    private String id;
    private String label;
    private boolean supported;
    public LanguageVersion(String id, String label, boolean supported) {
        this.id = id;
        this.label = label;
        this.supported = supported;
    }

    public LanguageVersion() {
    }
    @Override
    public String toString() {
        return "LanguageVersion{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", supported=" + supported +
                '}';
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public boolean isSupported() {
        return supported;
    }
    public void setSupported(boolean supported) {
        this.supported = supported;
    }
}
