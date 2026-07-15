package com.khalil.DRACS.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorite_sections WHERE pageId = :pageId ORDER BY timestamp DESC")
    LiveData<List<FavoriteSection>> getFavoritesForPage(String pageId);

    @Query("SELECT * FROM favorite_sections ORDER BY timestamp DESC")
    LiveData<List<FavoriteSection>> getAllFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteSection favorite);

    @Query("DELETE FROM favorite_sections WHERE pageId = :pageId AND sectionId = :sectionId")
    void deleteByPageAndSection(String pageId, String sectionId);

    @Query("SELECT COUNT(*) > 0 FROM favorite_sections WHERE pageId = :pageId AND sectionId = :sectionId")
    boolean isFavorite(String pageId, String sectionId);
}
