package com.baunvb.note.fragments;

/**
 * Created by Baunvb on 4/17/2017.
 */

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;

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

        ArrayList<String> photos = currentNote.getPhoto();
        bmPhotos.clear();
        if (photos != null){
            for (String photo : photos){
                bmPhotos.add(photo);
            }
        }

        if (currentNote.getAlarm() == 1){
            isAlarm = true;
        } else if(currentNote.getAlarm() == 0){
            isAlarm = false;
        }

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
        currentNote.setPhoto(bmPhotos);
        String datex[] = tvDate.getText().toString().split("/");
        String timex[] = tvTime.getText().toString().split(":");
        if (isAlarm){
            ((MainActivity)getActivity()).alarmService.setAlarmFire(id, Integer.parseInt(datex[0]),
                    Integer.parseInt(datex[1]), Integer.parseInt(datex[2]),Integer.parseInt(timex[0]),
                    Integer.parseInt(timex[1]));
        }

        database.update(currentNote);
        return id;
    }

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String data = "Tiêu đề: " + edtTitle.getText().toString() + "\n" + "Nội dung: "+edtContent.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data );
        startActivity(Intent.createChooser(sharingIntent, "Share with"));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_left:
                updateNote();
                if (position > 0){
                    position--;
                }
                fillData();
                break;
            case R.id.iv_right:
                updateNote();
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
