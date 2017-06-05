package com.baunvb.note.fragments;

/**
 * Created by Baunvb on 4/17/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;

import java.io.File;
import java.util.ArrayList;

public class EditNoteFragment extends FormNoteFragment{

    private ImageView ivPrevious;
    private ImageView ivNext;
    private ImageView ivDelete;
    private ImageView ivShare;

    @Override
    protected int getLayout() {
        return R.layout.fragment_edit_note;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    public void initViews() {
        ivPrevious = (ImageView) view.findViewById(R.id.iv_left);
        ivPrevious.setOnClickListener(this);
        ivNext = (ImageView) view.findViewById(R.id.iv_right);
        ivNext.setOnClickListener(this);
        ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(this);
        ivShare = (ImageView) view.findViewById(R.id.iv_share);
        ivShare.setOnClickListener(this);
        super.initViews();
        ivMore.setVisibility(View.VISIBLE);
        ivMore.setOnClickListener(this);
    }

    @Override
    protected void fillData() {
        photoAdapter.notifyDataSetChanged();
        listNote = ((MainActivity)getActivity()).getDatabase().getAll();
        ivPrevious.setFocusable(true);
        ivPrevious.setImageLevel(1);
        ivNext.setFocusable(true);
        ivNext.setImageLevel(1);

        if ((position == listNote.size() - 1) ){
            ivNext.setImageLevel(0);
            ivAlarm.setFocusable(false);
        }
        if (position == 0){
            ivPrevious.setImageLevel(0);
            ivPrevious.setFocusable(false);
        }

        currentNote = listNote.get(position);
        edtTitle.setText(currentNote.getTitle());
        edtContent.setText(currentNote.getContent());
        this.color = currentNote.getColor();
        layoutCreateNote.setBackgroundColor(Color.parseColor(currentNote.getColor()));
        ivAlarm.setImageLevel(currentNote.getAlarm());

        ArrayList<String> currentPhotos = currentNote.getPhoto();
        if (currentPhotos != null){
            for (int i = 0; i < currentPhotos.size() -1; i++){
                File imgFile = new File(currentPhotos.get(position));
                if (!imgFile.exists()){
                    currentPhotos.remove(i);
                }
            }
        }
        this.photos.clear();
        if (currentPhotos != null){
            for (String photo : currentPhotos){
                this.photos.add(photo);
            }
        }

        if (currentNote.getAlarm() == 1){
            isAlarm = true;
        } else if(currentNote.getAlarm() == 0){
            isAlarm = false;
        }

        layoutShowDateTime.setVisibility(View.VISIBLE);
        tvDate.setText(currentNote.getDate());
        tvTime.setText(currentNote.getTime());
    }

    @Override
    protected int saveNote() {
        int id = currentNote.getId();
        currentNote.setAlarm(getAlarm());
        currentNote.setTitle(edtTitle.getText().toString());
        currentNote.setContent(edtContent.getText().toString());
        currentNote.setColor(color);
        currentNote.setDate(tvDate.getText().toString());
        currentNote.setTime(tvTime.getText().toString());
        currentNote.setPhoto(photos);
        String datex[] = tvDate.getText().toString().split("/");
        String timex[] = tvTime.getText().toString().split(":");
        if (isAlarm){
            alarmService.setAlarmFire(id, Integer.parseInt(datex[0]),
                    Integer.parseInt(datex[1]), Integer.parseInt(datex[2]),Integer.parseInt(timex[0]),
                    Integer.parseInt(timex[1]));
        }
        database.update(currentNote);
        return id;
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String data = getString(R.string.title)+ ": " + edtTitle.getText().toString() + "\n" +getString(R.string.content)+": "+edtContent.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data );
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_label)));
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.add, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add:
                        ((MainActivity) getActivity()).showCreateNoteFragment();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_create_note_more:
                showPopup(ivMore);
                break;
            case R.id.iv_left:
                saveNote();
                if (position > 0){
                    position--;
                }
                fillData();
                break;
            case R.id.iv_right:
                saveNote();
                if (position < (listNote.size()-1)){
                    position++;
                }
                fillData();
                break;
            case R.id.iv_delete:
                deleteNote(currentNote.getId());
                break;
            case R.id.iv_share:
                shareNote();
                break;
            default:
                break;

        }
    }
}
