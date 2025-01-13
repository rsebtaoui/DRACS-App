package com.khalil.DRACS;

import android.view.View;

import java.util.List;

public class Item {
    private String title;
    private String intro;
    private String lists;
    private String conclu;
    private List<ClickableWord> clickableWords;
    private List<Coloredlines> coloredlines;

    public Item(String title, String intro, String lists, String conclu, List<ClickableWord> clickableWords, List<Coloredlines> coloredlines) {
        this.title = title;
        this.intro = intro;
        this.lists = lists;
        this.conclu = conclu;
        this.clickableWords = clickableWords;
        this.coloredlines = coloredlines;
    }

    public String getTitle() {
        return title;
    }

    public String getintro() {
        return intro;
    }

    public String getlists() {
        return lists;
    }

    public String getconclu() {
        return conclu;
    }

    public List<ClickableWord> getClickableWords() {
        return clickableWords;
    }

    public List<Coloredlines> getColoredlines() {
        return coloredlines;
    }

    public static class ClickableWord {
        private final String word;
        private final View.OnClickListener onClickListener;

        public ClickableWord(String word, View.OnClickListener onClickListener) {
            this.word = word;
            this.onClickListener = onClickListener;
        }

        public String getWord() {
            return word;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }
    }

    public static class Coloredlines {
        private String line;


        public Coloredlines(String line) {
            this.line = line;
        }

        public String getLine() {
            return line;
        }
    }
}
