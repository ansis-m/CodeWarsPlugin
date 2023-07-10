package com.example.codewarsplugin.models.kata;

public class KataOutput {


    private String language = ""; //languageName
    private String code = ""; //setup
    private String fixture = ""; //exampleFixture
    private String setup = ""; //empty
    private String testFramework = ""; //testFramework
    private String languageVersion = ""; //languageVersions.id
    private String relayId = ""; //solution Id
    private String[] ciphered = {"setup", "fixture"};
    private String channel = "runner:62b4df10-360b-89b9-387c-d6deb2278971";
    private String successMode; //null

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFixture() {
        return fixture;
    }

    public void setFixture(String fixture) {
        this.fixture = fixture;
    }

    public String getSetup() {
        return setup;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public String getTestFramework() {
        return testFramework;
    }

    public void setTestFramework(String testFramework) {
        this.testFramework = testFramework;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(String languageVersion) {
        this.languageVersion = languageVersion;
    }

    public String getRelayId() {
        return relayId;
    }

    public void setRelayId(String relayId) {
        this.relayId = relayId;
    }

    public String[] getCiphered() {
        return ciphered;
    }

    public void setCiphered(String[] ciphered) {
        this.ciphered = ciphered;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSuccessMode() {
        return successMode;
    }

    public void setSuccessMode(String successMode) {
        this.successMode = successMode;
    }

}
