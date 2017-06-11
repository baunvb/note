package com.baunvb.note.activity.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.baunvb.note.R;
import com.baunvb.note.db.DatabaseManager;
import com.baunvb.note.activity.fragments.CreateNoteFragment;
import com.baunvb.note.activity.fragments.EditNoteFragment;
import com.baunvb.note.activity.fragments.ListNoteFragment;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 1;
    private ListNoteFragment mListNoteFragment;
    private CreateNoteFragment mCreateNoteFragment;
    private EditNoteFragment mEditNoteFragment;
    private com.baunvb.note.model.Note note;
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setNote(com.baunvb.note.model.Note note) {
        this.note = note;
    }

    public com.baunvb.note.model.Note getNote() {
        return note;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        if (null == savedInstanceState){
            showListNoteFragment();
        }
    }

    public void showListNoteFragment() {
        if (mListNoteFragment == null) {
            mListNoteFragment = new ListNoteFragment();
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, mListNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        mCreateNoteFragment = null;
        mEditNoteFragment = null;
    }

    public void showCreateNoteFragment() {
        if (mCreateNoteFragment == null) {
            mCreateNoteFragment = new CreateNoteFragment();
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, mCreateNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public void showEditNoteFragment(){
        if (mEditNoteFragment == null){
            mEditNoteFragment = new EditNoteFragment();
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, mEditNoteFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE_PERMISSION);
            }

        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            }
        } else{
            Toast.makeText(this, getString(R.string.request_permission_label), Toast.LENGTH_LONG).show();
        }
    }

}
