package com.example.flashcardapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {com.example.flashcardapp.Flashcard.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract com.example.flashcardapp.FlashcardDao flashcardDao();
}
