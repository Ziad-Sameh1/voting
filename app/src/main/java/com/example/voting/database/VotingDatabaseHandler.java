package com.example.voting.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class VotingDatabaseHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "VotingDB";
    private SQLiteDatabase db;

    // tables
    private String POLL_TABLE = "CREATE TABLE polls (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, desc TEXT)";

    private String VOTES_TABLE = "CREATE TABLE votes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, pollId INTEGER FOREIGN KEY REFERENCES polls (id), body TEXT NOT NULL, votes INTEGER )";

    public VotingDatabaseHandler(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL(POLL_TABLE);
        db.execSQL(VOTES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS polls, votes");
        onCreate(db);
    }

    // create poll
    public void newPoll(String title, String desc, String[] optionsP) {

        db = this.getWritableDatabase();

        ContentValues pollValue = new ContentValues();
        ContentValues optionsValue = new ContentValues();

        pollValue.put("title", title);
        pollValue.put("desc", desc);
        long pollId = db.insert("polls", null, pollValue);

        for (int i = 0; i < optionsP.length; i++) {
            optionsValue.put("pollId", pollId);
            optionsValue.put("body", optionsP[i]);
            optionsValue.put("votes", 0);
            db.insert("votes", null, optionsValue);
        }

        db.close();
    }

    public Cursor getPolls() {
        db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM polls LEFT JOIN votes ON votes.pollId = polls.id", null);
        db.close();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor;
            }
        }
        return null;
    }


    public Cursor searchPolls(String title) {
        db = this.getReadableDatabase();

        final Cursor cursor = db.rawQuery("SELECT * FROM polls LEFT JOIN votes ON votes.pollId = polls.id WHERE title LIKE ?", new String[]{title});
        db.close();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor;
            }
        }
        return null;
    }


    public void addVote(int optionId) {
        db = this.getWritableDatabase();
        db.execSQL("UPDATE FROM votes where id = '" + optionId + "' SET votes = votes + 1", null);
        db.close();
    }

    public void deletePoll(int pollId) {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM polls Where id = '" + pollId + "'", null);
        db.close();

    }


}
