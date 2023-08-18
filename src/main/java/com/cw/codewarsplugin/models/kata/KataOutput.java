package com.cw.codewarsplugin.models.kata;

public class KataOutput {

    private String language = "";
    private String code = "";
    private String fixture = "";
    private String setup = "";
    private String testFramework = "";
    private String languageVersion = "";
    private String relayId = "";
    private String[] ciphered = {"setup", "fixture"};
    private String channel = "runner:62b4df10-360b-89b9-387c-d6deb2278971";
    private String successMode;

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
    public KataOutput setFixture(String fixture) {
        this.fixture = fixture;
        return this;
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
    public KataOutput setCiphered(String[] ciphered) {
        this.ciphered = ciphered;
        return this;
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
