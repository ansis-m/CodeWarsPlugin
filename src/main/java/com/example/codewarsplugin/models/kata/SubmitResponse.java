package com.example.codewarsplugin.models.kata;

public class SubmitResponse {
    private String type;
    private String stdout;
    private String stderr;
    private Integer exitCode;
    private Integer wallTime;
    private String message;
    private String token;

    @Override
    public String toString() {
        return "SubmitResponse{" +
                "type='" + type + '\'' +
                ", \nstdout='" + stdout + '\'' +
                ", \nstderr='" + stderr + '\'' +
                ", \nexitCode=" + exitCode +
                ", \nwallTime=" + wallTime +
                ", \nmessage='" + message + '\'' +
                ", \ntoken='" + token + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public Integer getWallTime() {
        return wallTime;
    }

    public void setWallTime(Integer wallTime) {
        this.wallTime = wallTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
