package com.baunvb.note.model;

/**
 * Created by Bau NV on 6/6/2017.
 */

public class Photo {
    private int id;
    private int noteId;
    private String path;

    public Photo(int id, int noteId, String path){
        this.id = id;
        this.noteId = noteId;
        this.path = path;
    }

    public Photo(int noteId, String path){
        this.noteId = noteId;
        this.path  = path;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }
}
