package com.khalil.DRACS.Models;

public class SearchResult {
    private final String title;
    private final String snippet;
    private final Class<?> targetFragment;
    private String targetSection;
    private int sectionPosition;

    public SearchResult(String title, String snippet, Class<?> targetFragment) {
        this.title = title;
        this.snippet = snippet;
        this.targetFragment = targetFragment;
        this.sectionPosition = -1;
    }

    public void setSectionPosition(int position) {
        this.sectionPosition = position;
    }

    public int getSectionPosition() {
        return sectionPosition;
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
