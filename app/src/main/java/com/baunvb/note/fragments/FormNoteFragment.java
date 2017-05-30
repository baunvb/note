package com.baunvb.note.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;
import com.baunvb.note.adapter.NoteAdapter;
import com.baunvb.note.adapter.PhotoAdapter;
import com.baunvb.note.database.Database;
import com.baunvb.note.dialog.InsertPictureDialog;
import com.baunvb.note.dialog.PickColorDialog;
import com.baunvb.note.item.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Baunvb on 4/17/2017.
 */

public abstract class FormNoteFragment extends Fragment implements View.OnClickListener,
        PickColorDialog.PickerColorDialogListener,
        InsertPictureDialog.InsertPictureDialogListener {

    public static final String YELLOW = "#FFEB3B";
    public static final String ORANGE = "#FF9800";
    public static final String PINK = "#FCE4EC";
    public static final String BLUE = "#90CAF9";

    public static final int TAKE_PHOTO = 0;
    public static final int CHOOSE_PHOTO = 1;

    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int GALLERY_REQUEST_CODE = 3;

    protected View view;
    protected LinearLayout llBack;
    protected ImageView ivMore;
    protected ImageView ivCamera;
    protected ImageView ivColor;
    protected ImageView ivSave;
    protected ImageView ivAlarm;
    protected TextView tvDate;
    protected TextView tvTime;
    protected TextView tvAlarm;
    protected RecyclerView lvPhoto;

    protected EditText edtTitle;
    protected EditText edtContent;
    protected TextView tvItemNote;

    protected Button btnDatePicker, btnTimePicker;
    protected Button btnCloseAlarm;

    protected LinearLayout layoutShowDateTime;
    protected LinearLayout layoutCreateNote;

    protected int position;
    protected ArrayList<Note> listNote;
    protected Note currentNote;
    protected Database database;

    protected String color = PINK;
    protected boolean isAlarm;
    protected ArrayList<String> photos = new ArrayList<String>();
    protected LinearLayoutManager layoutManager;
    protected PhotoAdapter photoAdapter;

    protected Calendar myCalendar;

    protected int id;
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
    protected View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_back:
                    ((MainActivity) getActivity()).showListNoteFragment();
                    break;
                case R.id.iv_create_note_camera:
                    InsertPictureDialog pictureDialog = new InsertPictureDialog(getActivity());
                    pictureDialog.setListener(FormNoteFragment.this);
                    pictureDialog.show();
                    break;
                case R.id.iv_create_note_color:
                    PickColorDialog colorDialog = new PickColorDialog(getActivity());
                    colorDialog.setListener(FormNoteFragment.this);
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

    };

    public int getIdNote() {
        return id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected abstract int getLayout();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(getLayout(), null);
        initViews();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected abstract void fillData();

    public void initViews() {
        isAlarm = false;
        myCalendar = Calendar.getInstance();
        database = new Database(getActivity());

        lvPhoto = (RecyclerView) view.findViewById(R.id.lvPhoto);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lvPhoto.setLayoutManager(layoutManager);
        photos.clear();
        photoAdapter = new PhotoAdapter(getActivity(), photos);
        lvPhoto.setAdapter(photoAdapter);

        edtContent = (EditText) view.findViewById(R.id.edt_create_note_content);
        edtContent.setText("");
        edtTitle = (EditText) view.findViewById(R.id.edt_create_note_title);
        edtTitle.setText("");
        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvItemNote.setText(s);
                if (s.length() == 0) {
                    tvItemNote.setText(getString(R.string.app_name));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvItemNote = (TextView) view.findViewById(R.id.title_item_note);

        llBack = (LinearLayout) view.findViewById(R.id.ll_back);
        llBack.setOnClickListener(listener);

        ivCamera = (ImageView) view.findViewById(R.id.iv_create_note_camera);
        ivCamera.setOnClickListener(listener);

        ivColor = (ImageView) view.findViewById(R.id.iv_create_note_color);
        ivColor.setOnClickListener(listener);

        ivSave = (ImageView) view.findViewById(R.id.iv_create_note_save);
        ivSave.setOnClickListener(listener);

        ivMore = (ImageView) view.findViewById(R.id.iv_create_note_more);
        ivMore.setVisibility(View.GONE);

        tvDate = (TextView) view.findViewById(R.id.tv_create_note_date);
        tvDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        tvTime = (TextView) view.findViewById(R.id.tv_create_note_time);
        tvTime.setText(new SimpleDateFormat("HH:mm").format(new Date()));
        tvAlarm = (TextView) view.findViewById(R.id.tv_create_note_alarm);

        ivAlarm = (ImageView) view.findViewById(R.id.iv_create_note_open_alarm);
        ivAlarm.setOnClickListener(listener);
        ivAlarm.setImageLevel(0);

        btnCloseAlarm = (Button) view.findViewById(R.id.btn_create_note_close_alarm);
        btnCloseAlarm.setOnClickListener(listener);

        btnDatePicker = (Button) view.findViewById(R.id.btn_create_note_date_picker);
        btnDatePicker.setOnClickListener(listener);

        btnTimePicker = (Button) view.findViewById(R.id.btn_create_note_time_picker);
        btnTimePicker.setOnClickListener(listener);

        layoutShowDateTime = (LinearLayout) view.findViewById(R.id.layout_create_note_show_date_time);
        layoutShowDateTime.setVisibility(View.INVISIBLE);

        layoutCreateNote = (LinearLayout) view.findViewById(R.id.layout_create_note);
        layoutCreateNote.setBackgroundColor(Color.parseColor(color));

        position = ((MainActivity) getActivity()).getPosition();
        photoAdapter.notifyDataSetChanged();
        fillData();
    }

    public void deleteNote(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.notification_delete))
                .setCancelable(false)
                .setTitle(getString(R.string.delete_label))
                .setPositiveButton(getString(R.string.yes_label),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.getDatabase().delete(id);
                                ((MainActivity) getActivity()).showListNoteFragment();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.no_label),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
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

    public ArrayList<String> savePhoto(ArrayList<String> paths) {
        ArrayList<String> listPaths = new ArrayList<String>();
        for (String path : paths) {
            listPaths.add(path);
        }
        return listPaths;
    }

    protected abstract int saveNote();

    private void setDate() {
        String myFormat = "dd/MM/yyyy";
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
            case FormNoteFragment.TAKE_PHOTO:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                super.startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
                break;
            case FormNoteFragment.CHOOSE_PHOTO:
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                String newPath = saveToInternalStorage(selectedImageUri);
                photos.add(newPath);
                photoAdapter.notifyDataSetChanged();
            }

            if (requestCode == GALLERY_REQUEST_CODE){
                try {
                    Uri selectedImageUri = data.getData();
                    String newPath = saveToInternalStorage(selectedImageUri);
                    //saveToInternalStorage(selectedImageUri);
                    photos.add(newPath);
                    photoAdapter.notifyDataSetChanged();
                } catch (Exception e){

                }
            }
        }
    }

    private String saveToInternalStorage(Uri uri) {
        String path = getRealPathFromURI(uri);
        File fileSrc = new File(path);
        String fileName = path.substring(path.lastIndexOf("/")+1);
        String pathImage = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + Environment.DIRECTORY_PICTURES
                + "/" + fileName;
        File fileDes = new File(pathImage);
        if (fileDes.exists()) {
            fileDes.delete();
        }
        try {
            fileDes.createNewFile();
            copyFile(fileSrc, fileDes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathImage;
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

}
