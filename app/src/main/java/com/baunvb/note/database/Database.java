package com.baunvb.note.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.baunvb.note.item.Note;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Database {


    private final String DATABASE_PATH = Environment
            .getDataDirectory().getPath()
            + "/data/com.baunvb.note/";
    private static final String DATABASE_NAME = "NodeDB.sqlite";

    private static final String TABLE_NOTE_LIST = "Note";


    private static final String COLUMN_LIST_NOTE_ID = "id";
    private static final String COLUMN_LIST_NOTE_DATE = "date";
    private static final String COLUMN_LIST_NOTE_CONTENT = "content";
    private static final String COLUMN_LIST_NOTE_COLOR = "color";
    private static final String COLUMN_LIST_NOTE_TITLE = "title";
    private static final String COLUMN_LIST_NOTE_IS_ALARM = "isAlarm";
    private static final String COLUMN_LIST_NOTE_TIME = "time";
    private static final String COLUMN_LIST_NOTE_PHOTO = "photo";

    private SQLiteDatabase sqLiteDatabase;

    public Database(Context context) {

        copyDatabase(context);
    }

    public void copyDatabase(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(DATABASE_NAME);

            String path = DATABASE_PATH + DATABASE_NAME;
            File file = new File(path);
            if (file.exists()) {
                return;
            }

            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }

            inputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDatabase() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(
                    DATABASE_PATH + DATABASE_NAME,
                    null,
                    SQLiteDatabase.OPEN_READWRITE
            );
        }
    }

    public void closeDatabase() {
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public long insert(Note note) {
        openDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NOTE_DATE, note.getDate());
        values.put(COLUMN_LIST_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_LIST_NOTE_COLOR, note.getColor());
        values.put(COLUMN_LIST_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_LIST_NOTE_IS_ALARM, note.getAlarm());
        values.put(COLUMN_LIST_NOTE_TIME, note.getTime());
        values.put(COLUMN_LIST_NOTE_PHOTO, gson.toJson(note.getPhoto(), new TypeToken<ArrayList<byte[]>>() {
        }.getType()));

        long id = sqLiteDatabase.insert(TABLE_NOTE_LIST, null, values);
        closeDatabase();
        return id;
    }

    public void update(Note note) {
        openDatabase();
        Gson gson = new Gson();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LIST_NOTE_DATE, note.getDate());
        values.put(COLUMN_LIST_NOTE_CONTENT, note.getContent());
        values.put(COLUMN_LIST_NOTE_COLOR, note.getColor());
        values.put(COLUMN_LIST_NOTE_TITLE, note.getTitle());
        values.put(COLUMN_LIST_NOTE_IS_ALARM, note.getAlarm());
        values.put(COLUMN_LIST_NOTE_TIME, note.getTime());
        values.put(COLUMN_LIST_NOTE_PHOTO, gson.toJson(note.getPhoto(), new TypeToken<ArrayList<byte[]>>() {
        }.getType()));

        sqLiteDatabase.update(
                TABLE_NOTE_LIST,
                values,
                "id=?",
                new String[]{String.valueOf(note.getId())}
        );
        closeDatabase();
    }

    public void delete(int id) {
        openDatabase();
        sqLiteDatabase.delete(
                TABLE_NOTE_LIST,
                "id=?",
                new String[]{String.valueOf(id)}
        );
        closeDatabase();
    }

    public ArrayList<Note> getAll() {
        openDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM " + TABLE_NOTE_LIST
                        + " ORDER BY " + COLUMN_LIST_NOTE_ID + " DESC",
                null);
        if (cursor == null) {
            closeDatabase();
            return null;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            closeDatabase();
            return new ArrayList<>();
        }

        ArrayList<Note> list = new ArrayList<>();
        cursor.moveToFirst();
        int indexId = cursor.getColumnIndex(COLUMN_LIST_NOTE_ID);
        int indexDate = cursor.getColumnIndex(COLUMN_LIST_NOTE_DATE);
        int indexContent = cursor.getColumnIndex(COLUMN_LIST_NOTE_CONTENT);
        int indexColor = cursor.getColumnIndex(COLUMN_LIST_NOTE_COLOR);
        int indexTitle = cursor.getColumnIndex(COLUMN_LIST_NOTE_TITLE);
        int indexIsAlarm = cursor.getColumnIndex(COLUMN_LIST_NOTE_IS_ALARM);
        int indexTime = cursor.getColumnIndex(COLUMN_LIST_NOTE_TIME);
        int indexPhoto = cursor.getColumnIndex(COLUMN_LIST_NOTE_PHOTO);
        Gson gson = new Gson();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(indexId);
            String date = cursor.getString(indexDate);
            String content = cursor.getString(indexContent);
            String color = cursor.getString(indexColor);
            String title = cursor.getString(indexTitle);
            int isAlarm = cursor.getInt(indexIsAlarm);
            String time = cursor.getString(indexTime);
            ArrayList<byte[]> photo = gson.fromJson(cursor.getString(indexPhoto), new TypeToken<ArrayList<byte[]>>() {
            }.getType());
            list.add(new Note(id, title, content, date, time, color, isAlarm, photo));
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase();
        return list;
    }

}