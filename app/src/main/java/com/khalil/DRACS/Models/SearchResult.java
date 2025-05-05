package com.khalil.DRACS.Models;

public class SearchResult {
    private String title;
    private String snippet;
    private Class<?> targetFragment;
    private String targetSection;

    public SearchResult(String title, String snippet, Class<?> targetFragment) {
        this.title = title;
        this.snippet = snippet;
        this.targetFragment = targetFragment;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public Class<?> getTargetFragment() {
        return targetFragment;
    }

    public String getTargetSection() {
        return targetSection;
    }

    public void setTargetSection(String targetSection) {
        this.targetSection = targetSection;
    }
}
