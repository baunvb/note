package com.baunvb.note.fragments;

/**
 * Created by Baunvb on 4/17/2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;
import com.baunvb.note.item.Note;

import java.util.ArrayList;

public class CreateNoteFragment extends FormNoteFragment{
    @Override
    protected int getLayout() {
        return R.layout.fragment_create_note;
    }

    @Override
    protected void fillData(){
    }

    @Override
    protected int saveNote() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();
        int alarm = getAlarm();

        ArrayList<String> photos = null;
        if (bmPhotos.size()>0 ) {
            photos = savePhoto(bmPhotos);
        }
        id = (int) database.insert(new Note(title, content, date, time, color, alarm, photos));
        String datex[] = date.split("/");
        String timex[] = time.split(":");
        if (isAlarm) {
            ((MainActivity) getActivity()).alarmService.setAlarmFire(id, Integer.parseInt(datex[0]),
                    Integer.parseInt(datex[1]), Integer.parseInt(datex[2]), Integer.parseInt(timex[0]),
                    Integer.parseInt(timex[1]));
        }
        return id;
    }

    @Override
    public void onClick(View v) {

    }
}
