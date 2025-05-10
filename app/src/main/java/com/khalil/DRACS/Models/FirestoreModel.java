package com.khalil.DRACS.Models;

import android.view.View;
import com.google.firebase.firestore.PropertyName;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class FirestoreModel {
    private Map<String, Section> sections;

    // Default constructor for Firestore serialization
    public FirestoreModel() {
        this.sections = new HashMap<>();
    }

    public static class Section {
        private String title;
        private String introduction;
        private List<String> dashes;

        @PropertyName("clickable_words")
        private List<ClickableWord> clickableWords;

        @PropertyName("colored_lines")
        private List<ColoredLine> coloredLines;

        private String conclusion;

        @PropertyName("order")
        private int order = 0;

        // Default constructor for Firestore serialization
        public Section() {
            this.dashes = new ArrayList<>();
            this.clickableWords = new ArrayList<>();
            this.coloredLines = new ArrayList<>();
        }

        @PropertyName("order")
        public int getOrder() { return order; }

        // Getters and setters with null checks
        public String getTitle() { return title != null ? title : ""; }
        public String getIntroduction() { return introduction != null ? introduction : ""; }
        public List<String> getDashes() { return dashes != null ? dashes : new ArrayList<>(); }

        @PropertyName("clickable_words")
        public List<ClickableWord> getClickableWords() { return clickableWords != null ? clickableWords : new ArrayList<>(); }

        @PropertyName("colored_lines")
        public List<ColoredLine> getColoredLines() { return coloredLines != null ? coloredLines : new ArrayList<>(); }

        public String getConclusion() { return conclusion != null ? conclusion : ""; }
    }

    public static class ClickableWord {
        @PropertyName("text")
        private String text;

        @PropertyName("color")
        private String color;

        @PropertyName("action_type")
        private String actionType;

        @PropertyName("action_value")
        private String actionValue;

        private View.OnClickListener onClickListener;

        // Default constructor for Firestore serialization
        public ClickableWord() {
            this.text = "";
            this.color = "";
            this.actionType = "";
            this.actionValue = "";
        }

        // Getters with null checks
        @PropertyName("text")
        public String getText() { return text != null ? text : ""; }

        @PropertyName("color")
        public String getColor() { return color != null ? color : ""; }

        @PropertyName("action_type")
        public String getActionType() { return actionType != null ? actionType : ""; }

        @PropertyName("action_value")
        public String getActionValue() { return actionValue != null ? actionValue : ""; }

        public View.OnClickListener getOnClickListener() { return onClickListener; }

        // Setter for onClickListener
        public void setOnClickListener(View.OnClickListener listener) {
            this.onClickListener = listener;
        }
    }

    public static class ColoredLine {
        @PropertyName("text")
        private String text;

        @PropertyName("color")
        private String color;

        // Default constructor for Firestore serialization
        public ColoredLine() {
            this.text = "";
            this.color = "";
        }

        // Getters with null checks
        @PropertyName("text")
        public String getText() { return text != null ? text : ""; }

        @PropertyName("color")
        public String getColor() { return color != null ? color : ""; }
    }

    public Map<String, Section> getSections() {
        return sections != null ? sections : new HashMap<>();
    }
}
