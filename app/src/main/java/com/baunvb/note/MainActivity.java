package com.baunvb.note;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baunvb.note.database.Database;
import com.baunvb.note.fragments.CreateNoteFragment;
import com.baunvb.note.fragments.EditNoteFragment;
import com.baunvb.note.fragments.ListNoteFragment;
import com.baunvb.note.item.Note;
import com.baunvb.note.service.AlarmService;

public class MainActivity extends AppCompatActivity {
    private ListNoteFragment listNoteFragment;
    private CreateNoteFragment createNoteFragment;
    private EditNoteFragment editNoteFragment;
    private Database database;
    private Note note;
    private int position;



    private ServiceConnection serviceConnection;
    public AlarmService alarmService;

    public static boolean isConnected;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this);
        showListNoteFragment();
        connectService();
    }

    public void showListNoteFragment() {
        if (listNoteFragment == null) {
            listNoteFragment = new ListNoteFragment();
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, listNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    public void showCreateNoteFragment() {
        createNoteFragment = new CreateNoteFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, createNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    public void showEditNoteFragment(){
        editNoteFragment = new EditNoteFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, editNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public Database getDatabase() {
        return database;
    }

    private void connectService() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                alarmService = ((AlarmService.ServiceBinder) service).getService();
                isConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
            }
        };

        Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

}
