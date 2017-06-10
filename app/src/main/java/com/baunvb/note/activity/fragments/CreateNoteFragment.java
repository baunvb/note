package com.baunvb.note.activity.fragments;

/**
 * Created by Baunvb on 4/17/2017.
 */

import android.util.Log;
import android.view.View;

import com.baunvb.note.R;
import com.baunvb.note.model.Note;
import com.baunvb.note.model.Photo;

public class CreateNoteFragment extends BaseFragment {

    @Override
    protected int getLayout() {
        return R.layout.fragment_create_note;
    }

    @Override
    protected void fillData(){
    }

    @Override
    public int saveNote() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();
        int alarm = getAlarm();

        id = (int) database.insertNote(new Note(title, content, date, time, color, alarm));
        for (int i = 0; i < this.photoPaths.size(); i++) {
            long id = database.insertPhoto(new Photo(this.id, photoPaths.get(i)));
        }

        String datex[] = date.split("/");
        String timex[] = time.split(":");
        if (isAlarm) {
            setAlarmFire(id, Integer.parseInt(datex[0]),
                    Integer.parseInt(datex[1]), Integer.parseInt(datex[2]), Integer.parseInt(timex[0]),
                    Integer.parseInt(timex[1]));
        }
        return id;
    }

    @Override
    public void onClick(View v) {

    }
}
