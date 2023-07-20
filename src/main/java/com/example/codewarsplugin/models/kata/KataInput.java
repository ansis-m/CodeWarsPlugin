package com.example.codewarsplugin.models.kata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KataInput implements JsonSource{

    private boolean success;
    private String languageName;
    private String label;
    private String solutionId;
    private String icon;
    private String setup;
    private String exampleFixture;
    private String workingCode;
    private String workingFixture;
    private String recentlyAttempted;
    private String activeVersion;
    private List<LanguageVersion> languageVersions;
    private String testFramework;
    private String path;

    @JsonProperty("package")
    private String _package;

    private String fixture;


    @Override
    public String toString() {
        return "KataInput{" +
                "success=" + success +
                ", languageName='" + languageName + '\'' +
                ", label='" + label + '\'' +
                ", solutionId='" + solutionId + '\'' +
                ", icon='" + icon + '\'' +
                ", setup='" + setup + '\'' +
                ", exampleFixture='" + exampleFixture + '\'' +
                ", workingCode='" + workingCode + '\'' +
                ", workingFixture='" + workingFixture + '\'' +
                ", recentlyAttempted='" + recentlyAttempted + '\'' +
                ", activeVersion='" + activeVersion + '\'' +
                ", languageVersions=" + languageVersions +
                ", testFramework='" + testFramework + '\'' +
                ", path='" + path + '\'' +
                ", _package='" + _package + '\'' +
                ", fixture='" + fixture + '\'' +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getExampleFixture() {
        return exampleFixture;
    }

    public void setExampleFixture(String exampleFixture) {
        this.exampleFixture = exampleFixture;
    }

    public String getWorkingCode() {
        return workingCode;
    }

    public void setWorkingCode(String workingCode) {
        this.workingCode = workingCode;
    }

    public String getWorkingFixture() {
        return workingFixture;
    }

    public void setWorkingFixture(String workingFixture) {
        this.workingFixture = workingFixture;
    }

    public String getRecentlyAttempted() {
        return recentlyAttempted;
    }

    public void setRecentlyAttempted(String recentlyAttempted) {
        this.recentlyAttempted = recentlyAttempted;
    }

    public String getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(String activeVersion) {
        this.activeVersion = activeVersion;
    }

    public List<LanguageVersion> getLanguageVersions() {
        return languageVersions;
    }

    public void setLanguageVersions(List<LanguageVersion> languageVersions) {
        this.languageVersions = languageVersions;
    }

    public String getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }

    public String get_package() {
        return _package;
    }

    public void set_package(String _package) {
        this._package = _package;
    }

    public String getFixture() {
        return fixture;
    }

    public void setFixture(String fixture) {
        this.fixture = fixture;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
