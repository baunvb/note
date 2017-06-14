package com.baunvb.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.baunvb.note.model.Note;
import com.baunvb.note.model.Photo;

import java.util.ArrayList;

/**
 * Created by Bau NV on 6/5/2017.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notes.db";
    public static final String TABLE_NOTES = "notes";
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String CONTENT = "content";
    public static final String COLOR = "color";
    public static final String TITLE = "title";
    public static final String ALARM = "alarm";
    public static final String TIME = "time";

    public static final String TABLE_PHOTOS = "photoPaths";
    public static final String PHOTO_ID = "photoId";
    public static final String NOTE_ID = "noteId";
    public static final String PHOTO = "path";
    public static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;


    private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
            + TABLE_NOTES + "( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE + " text, "
            + CONTENT +" text, "
            + DATE + " text, "
            + COLOR + " text, "
            + ALARM + " integer, "
            + TIME + " text);";

    private static final String CREATE_TABLE_PHOTO = "CREATE TABLE "
            + TABLE_PHOTOS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NOTE_ID + " INTEGER, "
            + PHOTO + " text);";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_PHOTO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    public void open(){
        db = getWritableDatabase();
    }

    public void close(){
        db.close();
    }

    public long insertNote(Note note) {
        open();
        ContentValues values = new ContentValues();
        values.put(DATE, note.getDate());
        values.put(CONTENT, note.getContent());
        values.put(COLOR, note.getColor());
        values.put(TITLE, note.getTitle());
        values.put(ALARM, note.getAlarm());
        values.put(TIME, note.getTime());
        long id = db.insert(TABLE_NOTES, null, values);
        close();
        return id;
    }

    public long insertPhoto(Photo photo){
        open();
        ContentValues values = new ContentValues();
        values.put(NOTE_ID, photo.getNoteId());
        values.put(PHOTO, photo.getPath());
        long id = db.insert(TABLE_PHOTOS, null, values);
        close();
        return id;
    }

    public ArrayList<Note> getAllNotes() {
        open();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NOTES
                        + " ORDER BY " + ID + " DESC",
                null);
        if (cursor == null) {
            close();
            return null;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return new ArrayList<>();
        }

        ArrayList<Note> list = new ArrayList<>();
        cursor.moveToFirst();
        int indexId = cursor.getColumnIndex(ID);
        int indexDate = cursor.getColumnIndex(DATE);
        int indexContent = cursor.getColumnIndex(CONTENT);
        int indexColor = cursor.getColumnIndex(COLOR);
        int indexTitle = cursor.getColumnIndex(TITLE);
        int indexIsAlarm = cursor.getColumnIndex(ALARM);
        int indexTime = cursor.getColumnIndex(TIME);
        int indexPhoto = cursor.getColumnIndex(PHOTO);
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(indexId);
            String date = cursor.getString(indexDate);
            String content = cursor.getString(indexContent);
            String color = cursor.getString(indexColor);
            String title = cursor.getString(indexTitle);
            int isAlarm = cursor.getInt(indexIsAlarm);
            String time = cursor.getString(indexTime);
            list.add(new Note(id, title, content, date, time, color, isAlarm));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return list;
    }

    public ArrayList<Photo> getAllPhotos(int noteID){
        open();
        ArrayList<Photo> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PHOTOS + " WHERE " + NOTE_ID + " = " + noteID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(c.getColumnIndex(ID));
            String path = c.getString(c.getColumnIndex(PHOTO));
            list.add(new Photo(id, path));
            c.moveToNext();
        }

        c.close();
        close();
        return list;
    }

    public void deleteNote(int id) {
        open();
        db.delete(
                TABLE_NOTES,
                "id=?",
                new String[]{String.valueOf(id)}
        );
        close();
    }

    public void deletePhoto(int noteID){
        open();
        db.delete(
                TABLE_PHOTOS,
                "noteId=?",
                new String[] {String.valueOf(noteID)}
        );
        close();
    }

    public void updateNote(Note note) {
        open();
        ContentValues values = new ContentValues();
        values.put(DATE, note.getDate());
        values.put(CONTENT, note.getContent());
        values.put(COLOR, note.getColor());
        values.put(TITLE, note.getTitle());
        values.put(ALARM, note.getAlarm());
        values.put(TIME, note.getTime());
        db.update(
                TABLE_NOTES,
                values,
                "id=?",
                new String[]{String.valueOf(note.getId())}
        );
        close();
    }
}
