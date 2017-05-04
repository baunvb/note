package com.baunvb.note.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class EditNoteFragment extends Fragment implements View.OnClickListener, PickColorDialog.PickerColorDialogListener, InsertPictureDialog.InsertPictureDialogListener {

    private static final int CAMERA_EDIT_REQUEST_CODE = 100;
    private static final int GALLERY_EDIT_REQUEST_CODE = 101;

    private View view;
    private ImageView ivCamera;
    private ImageView ivColor;
    private ImageView ivSave;
    private ImageView ivAlarm;

    private ImageView ivLeft;
    private ImageView ivRight;
    private ImageView ivShare;
    private ImageView ivDelete;

    private TextView tvDate;
    private TextView tvTime;
    private TextView tvAlarm;

    private EditText edtTitle;
    private EditText edtContent;

    private Button btnDatePicker, btnTimePicker;
    private Button btnCloseAlarm;

    private LinearLayout layoutShowDateTime;
    private LinearLayout layoutEditNote;

    private Database database;

    private String color = null;
    private boolean isAlarm;

    private Note currentNote;
    private int position;
    private ArrayList<Bitmap> bmPhotos = new ArrayList<Bitmap>();
    private RecyclerView lvPhoto;
    LinearLayoutManager layoutManager;
    PhotoAdapter photoAdapter;

    private ArrayList<Note> listNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_note, null);
        initViews();
        return view;
    }

    private void initViews() {
        database = new Database(getActivity());

        lvPhoto = (RecyclerView) view.findViewById(R.id.lvPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lvPhoto.setLayoutManager(layoutManager);

        photoAdapter = new PhotoAdapter(bmPhotos);
        lvPhoto.setAdapter(photoAdapter);

        edtContent = (EditText) view.findViewById(R.id.edt_edit_note_content);
        edtTitle = (EditText) view.findViewById(R.id.edt_edit_note_title);

        ivCamera = (ImageView) view.findViewById(R.id.iv_create_note_camera);
        ivCamera.setOnClickListener(this);

        ivColor = (ImageView) view.findViewById(R.id.iv_create_note_color);
        ivColor.setOnClickListener(this);

        ivSave = (ImageView) view.findViewById(R.id.iv_create_note_save);
        ivSave.setOnClickListener(this);

        tvDate = (TextView) view.findViewById(R.id.tv_edit_note_date);
        tvTime = (TextView) view.findViewById(R.id.tv_edit_note_time);
        tvAlarm = (TextView) view.findViewById(R.id.tv_edit_note_alarm);

        ivAlarm = (ImageView) view.findViewById(R.id.iv_edit_note_open_alarm);
        ivAlarm.setOnClickListener(this);
        ivAlarm.setImageLevel(0);

        btnCloseAlarm = (Button) view.findViewById(R.id.btn_edit_note_close_alarm);
        btnCloseAlarm.setOnClickListener(this);

        btnDatePicker = (Button) view.findViewById(R.id.btn_edit_note_date_picker);
        btnDatePicker.setOnClickListener(this);

        btnTimePicker = (Button) view.findViewById(R.id.btn_edit_note_time_picker);
        btnTimePicker.setOnClickListener(this);

        layoutShowDateTime = (LinearLayout) view.findViewById(R.id.layout_edit_note_show_date_time);
        layoutShowDateTime.setVisibility(View.VISIBLE);

        layoutEditNote = (LinearLayout) view.findViewById(R.id.layout_edit_note);
        ivLeft = (ImageView) view.findViewById(R.id.iv_left);
        ivRight = (ImageView) view.findViewById(R.id.iv_right);
        ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        ivShare = (ImageView) view.findViewById(R.id.iv_share);

        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        position = ((MainActivity)getActivity()).getPosition();
        photoAdapter.notifyDataSetChanged();
        fillData();
    }

    public void fillData(){
        photoAdapter.notifyDataSetChanged();
        listNote = ((MainActivity)getActivity()).getDatabase().getAll();
        ivLeft.setFocusable(true);
        ivLeft.setImageLevel(1);

        ivRight.setFocusable(true);
        ivRight.setImageLevel(1);

        if ((position == listNote.size() - 1) ){
            ivRight.setImageLevel(0);
            ivAlarm.setFocusable(false);
        }
        if (position == 0){
            ivLeft.setImageLevel(0);
            ivLeft.setFocusable(false);
        }

        currentNote = listNote.get(position);
        edtTitle.setText(currentNote.getTitle());
        edtContent.setText(currentNote.getContent());
        this.color = currentNote.getColor();
        layoutEditNote.setBackgroundColor(Color.parseColor(currentNote.getColor()));
        ivAlarm.setImageLevel(currentNote.getAlarm());

        ArrayList<byte[]> photos = currentNote.getPhoto();
        bmPhotos.clear();
        if (photos != null){
            for (byte[] photo : photos){
                Bitmap bmPhoto =  BitmapFactory.decodeByteArray(photo, 0, photo.length);
                bmPhotos.add(bmPhoto);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == Activity.RESULT_OK){
           if(requestCode == CAMERA_EDIT_REQUEST_CODE){
               Bitmap bmPhoto = (Bitmap) data.getExtras().get("data");
                bmPhotos.add(bmPhoto);
               photoAdapter.notifyDataSetChanged();
           }
           if (requestCode == GALLERY_EDIT_REQUEST_CODE){
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

    public ArrayList<byte[]> savePhoto(ArrayList<Bitmap> bitmaps) {
        ArrayList<byte[]> bytes = new ArrayList<byte[]>();
        for(Bitmap bitmap:bitmaps){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 0, outputStream);
            bytes.add(outputStream.toByteArray());
        }
        return bytes;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_create_note_camera:
                updateNote();
                InsertPictureDialog pictureDialog = new InsertPictureDialog(this.getActivity());
                pictureDialog.setListener(this);
                pictureDialog.show();

                break;
            case R.id.iv_create_note_color:
                PickColorDialog dialog = new PickColorDialog(this.getActivity());
                dialog.setListener(this);
                dialog.show();
                break;
            case R.id.iv_create_note_save:
                updateNote();
                ((MainActivity)getActivity()).showListNoteFragment();
                break;
            case R.id.iv_edit_note_open_alarm:
                layoutShowDateTime.setVisibility(View.VISIBLE);
                tvAlarm.setVisibility(View.GONE);
                if(!isAlarm){
                    ivAlarm.setImageLevel(1);
                    isAlarm = true;
                } else {
                    ivAlarm.setImageLevel(0);
                    isAlarm = false;
                }
                break;

            case R.id.btn_edit_note_close_alarm:
                layoutShowDateTime.setVisibility(View.INVISIBLE);
                tvAlarm.setVisibility(View.VISIBLE);
                isAlarm = false;
                ivAlarm.setImageLevel(0);
                break;
            case R.id.btn_edit_note_date_picker:
                new DatePickerDialog(getActivity(),
                        date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.btn_edit_note_time_picker:
                new TimePickerDialog(getActivity(),
                        time,
                        myCalendar.get(Calendar.HOUR),
                        myCalendar.get(Calendar.MINUTE),
                        true)
                        .show();
                break;
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

    private void shareNote() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String data = "Tiêu đề: " + edtTitle.getText().toString() + "\n" + "Nội dung: "+edtContent.getText().toString();
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data );
        startActivity(Intent.createChooser(sharingIntent, "Share with"));
    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDate();
        }

    };

    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            myCalendar.set(Calendar.HOUR, hour);
            myCalendar.set(Calendar.MINUTE, minute);
            setTime();
        }
    };

    private void setDate() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);

        tvDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void setTime(){
        String myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
        tvTime.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onSelectColorListener(String color) {
        layoutEditNote.setBackgroundColor(Color.parseColor(color));
        this.color = color;
    }


    public int getAlarm(){
        if (!isAlarm){
            return 0;
        }
        else
            return 1;
    }

    public void deleteNote(final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this?")
                .setCancelable(false)
                .setTitle("Confirm Delete")
                .setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity mainActivity = (MainActivity)getActivity();
                        mainActivity.getDatabase().delete(id);
                        ((MainActivity)getActivity()).showListNoteFragment();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public int updateNote(){
        int id = currentNote.getId();
        currentNote.setAlarm(getAlarm());
        currentNote.setTitle(edtTitle.getText().toString());
        currentNote.setContent(edtContent.getText().toString());
        currentNote.setColor(color);
        currentNote.setDate(tvDate.getText().toString());
        currentNote.setTime(tvTime.getText().toString());

        ArrayList<byte[]> photos = new ArrayList<byte[]>();
        if (bmPhotos.size()>0 ) {
            photos = savePhoto(bmPhotos);
        }
        currentNote.setPhoto(photos);
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

    @Override
    public void onInsertListener(int typeInsert) {
        switch (typeInsert){
            case CreateNoteFragment.TAKE_PHOTO:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                super.startActivityForResult(intentCamera,  CAMERA_EDIT_REQUEST_CODE);
                break;
            case CreateNoteFragment.CHOOSE_PHOTO:
                Intent intentGallery = new Intent();
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(Intent.createChooser(intentGallery, "Chose image"), GALLERY_EDIT_REQUEST_CODE);
                break;
            default:
                break;
        }
    }
}
