package com.example.codewarsplugin.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String username;
    private String email;
    private int honor;
    private int rank;
    private String current_language;
    private String country_name;
    private String id;
    private String[] starred_code_challenge_ids;
    private String avatar_tag;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", honor=" + honor +
                ", rank=" + rank +
                ", current_language='" + current_language + '\'' +
                ", country_name='" + country_name + '\'' +
                ", id='" + id + '\'' +
                ", starred_code_challenge_ids=" + Arrays.toString(starred_code_challenge_ids) +
                ", avatar_tag='" + avatar_tag + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHonor() {
        return honor;
    }

    public void setHonor(int honor) {
        this.honor = honor;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCurrent_language() {
        return current_language;
    }

    public void setCurrent_language(String current_language) {
        this.current_language = current_language;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getStarred_code_challenge_ids() {
        return starred_code_challenge_ids;
    }

    public void setStarred_code_challenge_ids(String[] starred_code_challenge_ids) {
        this.starred_code_challenge_ids = starred_code_challenge_ids;
    }

    public String getAvatar_tag() {
        return avatar_tag;
    }

    public void setAvatar_tag(String avatar_tag) {
        this.avatar_tag = avatar_tag;
    }
}
