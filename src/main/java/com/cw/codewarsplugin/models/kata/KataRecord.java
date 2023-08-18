package com.cw.codewarsplugin.models.kata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KataRecord implements JsonSource{

    private String id;
    private String name;
    private String slug;
    private String url;
    private String category;
    private String[] languages;
    private String path;
    private String selectedLanguage;
    private String description;
    private Rank rank;
    private String[] tags;
    private String workUrl;
    private boolean completed = false;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KataRecord that = (KataRecord) o;
        return id.equals(that.id) && name.equals(that.name) && slug.equals(that.slug) && selectedLanguage.equals(that.selectedLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, slug, selectedLanguage);
    }

    @Override
    public String toString() {
        return "KataRecord{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", url='" + url + '\'' +
                ", category='" + category + '\'' +
                ", languages=" + Arrays.toString(languages) +
                ", path='" + path + '\'' +
                ", selectedLanguage='" + selectedLanguage + '\'' +
                ", description='" + description + '\'' +
                ", rank=" + rank +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    public Rank getRank() {
        return rank;
    }
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    public String[] getTags() {
        return tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSelectedLanguage() {
        return selectedLanguage;
    }
    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String[] getLanguages() {
        return languages;
    }
    public void setLanguages(String[] languages) {
        this.languages = languages;
    }
    public void setWorkUrl(String url) {
        this.workUrl = url;
    }
    public String getWorkUrl() {
        return workUrl;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public static class Rank {
        int id;
        String name;
        String color;
        @Override
        public String toString() {
            return "Rank{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", color='" + color + '\'' +
                    '}';
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getColor() {
            return color;
        }
        public void setColor(String color) {
            this.color = color;
        }
    }
}
