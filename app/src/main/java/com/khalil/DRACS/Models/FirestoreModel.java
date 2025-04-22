package com.khalil.DRACS.Models;

import java.util.List;
import java.util.Map;

public class FirestoreModel {
    private Map<String, Section> sections;

    public static class Section {
        private String introduction;
        private String body;
        private List<String> dashes;
        private List<ClickableWord> clickable_words;
        private List<ColoredLine> colored_lines;
        private String conclusion;

        // Getters and setters
        public String getIntroduction() { return introduction; }
        public String getBody() { return body; }
        public List<String> getDashes() { return dashes; }
        public List<ClickableWord> getClickableWords() { return clickable_words; }
        public List<ColoredLine> getColoredLines() { return colored_lines; }
        public String getConclusion() { return conclusion; }
    }

    public static class ClickableWord {
        private String text;
        private String color;
        private String action_type;
        private String action_value;

        // Getters
        public String getText() { return text; }
        public String getColor() { return color; }
        public String getActionType() { return action_type; }
        public String getActionValue() { return action_value; }
    }

    public static class ColoredLine {
        private String text;
        private String color;

        // Getters
        public String getText() { return text; }
        public String getColor() { return color; }
    }

    public Map<String, Section> getSections() {
        return sections;
    }
}
