package com.baunvb.note.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;
import com.baunvb.note.adapter.PhotoAdapter;
import com.baunvb.note.database.Database;
import com.baunvb.note.dialog.InsertPictureDialog;
import com.baunvb.note.dialog.PickColorDialog;
import com.baunvb.note.item.Note;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class CreateNoteFragment extends Fragment implements View.OnClickListener, PickColorDialog.PickerColorDialogListener, InsertPictureDialog.InsertPictureDialogListener {

    public static final String YELLOW = "#FFEB3B";
    public static final String ORANGE = "#FF9800";
    public static final String PINK = "#FCE4EC";
    public static final String BLUE = "#90CAF9";

    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;

    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int GALLERY_REQUEST_CODE = 3;

    private View view;
    private ImageView ivCamera;
    private ImageView ivColor;
    private ImageView ivSave;
    private ImageView ivAlarm;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvAlarm;
    private RecyclerView lvPhoto;

    private EditText edtTitle;
    private EditText edtContent;

    private Button btnDatePicker, btnTimePicker;
    private Button btnCloseAlarm;

    private LinearLayout layoutShowDateTime;
    private LinearLayout layoutCreateNote;


    private Database database;

    private int typeInsert;
    private String color = PINK;
    private boolean isAlarm;
    private ArrayList<Bitmap> bmPhotos = new ArrayList<Bitmap>();
    LinearLayoutManager layoutManager;
    PhotoAdapter photoAdapter;

    private Calendar myCalendar;

    public int id;

    public int getIdNote() {
        return id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_note, null);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        isAlarm = false;
        myCalendar = Calendar.getInstance();
        database = new Database(getActivity());

        lvPhoto = (RecyclerView) view.findViewById(R.id.lvPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lvPhoto.setLayoutManager(layoutManager);
        bmPhotos.clear();
        photoAdapter = new PhotoAdapter(bmPhotos);
        lvPhoto.setAdapter(photoAdapter);

        edtContent = (EditText) view.findViewById(R.id.edt_create_note_content);
        edtContent.setText("");
        edtTitle = (EditText) view.findViewById(R.id.edt_create_note_title);
        edtTitle.setText("");

        ivCamera = (ImageView) view.findViewById(R.id.iv_create_note_camera);
        ivCamera.setOnClickListener(this);

        ivColor = (ImageView) view.findViewById(R.id.iv_create_note_color);
        ivColor.setOnClickListener(this);

        ivSave = (ImageView) view.findViewById(R.id.iv_create_note_save);
        ivSave.setOnClickListener(this);

        tvDate = (TextView) view.findViewById(R.id.tv_create_note_date);
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        tvTime = (TextView) view.findViewById(R.id.tv_create_note_time);
        tvTime.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        tvAlarm = (TextView) view.findViewById(R.id.tv_create_note_alarm);

        ivAlarm = (ImageView) view.findViewById(R.id.iv_create_note_open_alarm);
        ivAlarm.setOnClickListener(this);
        ivAlarm.setImageLevel(0);

        btnCloseAlarm = (Button) view.findViewById(R.id.btn_create_note_close_alarm);
        btnCloseAlarm.setOnClickListener(this);

        btnDatePicker = (Button) view.findViewById(R.id.btn_create_note_date_picker);
        btnDatePicker.setOnClickListener(this);

        btnTimePicker = (Button) view.findViewById(R.id.btn_create_note_time_picker);
        btnTimePicker.setOnClickListener(this);

        layoutShowDateTime = (LinearLayout) view.findViewById(R.id.layout_create_note_show_date_time);
        layoutShowDateTime.setVisibility(View.INVISIBLE);

        layoutCreateNote = (LinearLayout) view.findViewById(R.id.layout_create_note);
        layoutCreateNote.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_create_note_camera:
                InsertPictureDialog pictureDialog = new InsertPictureDialog(this.getActivity());
                pictureDialog.setListener(this);
                pictureDialog.show();
                break;
            case R.id.iv_create_note_color:
                PickColorDialog colorDialog = new PickColorDialog(this.getActivity());
                colorDialog.setListener(this);
                colorDialog.show();
                break;
            case R.id.iv_create_note_save:
                saveNote();
                ((MainActivity) getActivity()).showListNoteFragment();
                break;
            case R.id.iv_create_note_open_alarm:
                openAlarm();
                break;

            case R.id.btn_create_note_close_alarm:
                closeAlarm();
                break;

            case R.id.btn_create_note_date_picker:
                new DatePickerDialog(getActivity(),
                        dateListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.btn_create_note_time_picker:
                new TimePickerDialog(getActivity(),
                        timeListener,
                        myCalendar.get(Calendar.HOUR),
                        myCalendar.get(Calendar.MINUTE),
                        true)
                        .show();
                break;
            default:
                break;
        }

    }

    private void closeAlarm() {
        layoutShowDateTime.setVisibility(View.INVISIBLE);
        isAlarm = false;
        ivAlarm.setImageLevel(0);
        tvAlarm.setVisibility(View.VISIBLE);
    }

    private void openAlarm() {
        layoutShowDateTime.setVisibility(View.VISIBLE);
        tvAlarm.setVisibility(View.GONE);
        if (!isAlarm) {
            ivAlarm.setImageLevel(1);
            isAlarm = true;
        } else {
            ivAlarm.setImageLevel(0);
            isAlarm = false;
        }
    }

    public ArrayList<byte[]> savePhoto(ArrayList<Bitmap> bitmaps) {
        ArrayList<byte[]> bytes = new ArrayList<byte[]>();
        for(Bitmap bitmap:bitmaps){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 0, outputStream);
            bytes.add(outputStream.toByteArray());
        }
        return bytes;
    }

    private void saveNote() {
        String title = edtTitle.getText().toString();
        String content = edtContent.getText().toString();
        String date = tvDate.getText().toString();
        String time = tvTime.getText().toString();
        int alarm = getAlarm();

        ArrayList<byte[]> photos = null;
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
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int PhotoOfYear,
                              int dayOfPhoto) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, PhotoOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfPhoto);
            setDate();
        }

    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            setTime();
        }
    };

    private void setDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void setTime() {
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tvTime.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onSelectColorListener(String color) {
        layoutCreateNote.setBackgroundColor(Color.parseColor(color));
        this.color = color;
    }

    public int getAlarm() {
        if (!isAlarm) {
            return 0;
        } else
            return 1;
    }


    @Override
    public void onInsertListener(int typeInsert) {
        switch (typeInsert) {
            case CreateNoteFragment.TAKE_PHOTO:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                super.startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
                break;
            case CreateNoteFragment.CHOOSE_PHOTO:
                Intent intentGallery = new Intent();
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                Intent chooserIntent = Intent.createChooser(intentGallery, "Choose image");
                startActivityForResult(chooserIntent, GALLERY_REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bmPhoto = (Bitmap) data.getExtras().get("data");
                bmPhotos.add(bmPhoto);
                photoAdapter.notifyDataSetChanged();
            }

            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bmPhoto = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    bmPhotos.add(bmPhoto);
                    photoAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
