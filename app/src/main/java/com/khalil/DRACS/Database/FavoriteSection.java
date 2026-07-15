package com.khalil.DRACS.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "favorite_sections", primaryKeys = {"pageId", "sectionId"})
public class FavoriteSection {

    @NonNull
    public String pageId;

    @NonNull
    public String sectionId;

    @NonNull
    public String title;

    public long timestamp;

    public FavoriteSection(@NonNull String pageId, @NonNull String sectionId,
                           @NonNull String title, long timestamp) {
        this.pageId = pageId;
        this.sectionId = sectionId;
        this.title = title;
        this.timestamp = timestamp;
    }
}
