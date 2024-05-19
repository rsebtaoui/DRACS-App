package com.example.DRACS;

import android.view.View;

import java.util.List;

public class Item {
    private String title;
    private String intro;
    private String lists;
    private String conclu;
    private List<ClickableWord> clickableWords;

    public Item(String title, String intro, String lists, String conclu, List<ClickableWord> clickableWords) {
        this.title = title;
        this.intro = intro;
        this.lists = lists;
        this.conclu = conclu;
        this.clickableWords = clickableWords;
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

    public static class ClickableWord {
        private String word;
        private View.OnClickListener onClickListener;

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
}
