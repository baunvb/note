package com.baunvb.note.activity.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.baunvb.note.R;
import com.baunvb.note.activity.MainActivity;
import com.baunvb.note.custom.adapter.PhotoAdapter;
import com.baunvb.note.custom.dialog.InsertPictureDialog;
import com.baunvb.note.custom.dialog.PickColorDialog;
import com.baunvb.note.db.DatabaseManager;
import com.baunvb.note.service.AlarmReceiver;
import com.baunvb.note.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Baunvb on 4/17/2017.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener,
        PickColorDialog.PickerColorDialogListener,
        InsertPictureDialog.InsertPictureDialogListener {

    public static final int TAKE_PHOTO_CODE = 0;
    public static final int CHOOSE_PHOTO_CODE = 1;

    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int GALLERY_REQUEST_CODE = 3;
    private static final String PHOTO_PATHS = "photoPaths";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String COLOR = "color";
    private static final String ALARM = "alarm";
    private static final String ROOT_DIRECTORY = "Notes" ;
    private static final String TYPE_IMAGE = "image/*";
    private static final String CHOOSER_IMAGE = "Choose image";

    protected View view;
    protected LinearLayout llBack;
    protected ImageView ivSetting;
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
    protected ArrayList<com.baunvb.note.model.Note> listNote;
    protected com.baunvb.note.model.Note currentNote;
    protected DatabaseManager database;

    protected String color = Constant.PINK;
    protected boolean isAlarm;
    protected ArrayList<String> photoPaths = new ArrayList<String>();
    protected LinearLayoutManager layoutManager;
    protected PhotoAdapter photoAdapter;

    protected Calendar myCalendar;

    protected int id;

    private AlarmManager alarmmanager;
    private PendingIntent pendingIntent;
    private Intent launchIntent;

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfPhoto) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
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
                case R.id.iv_camera:
                    InsertPictureDialog pictureDialog = new InsertPictureDialog(getActivity());
                    pictureDialog.setListener(BaseFragment.this);
                    pictureDialog.show();
                    break;
                case R.id.iv_color:
                    PickColorDialog colorDialog = new PickColorDialog(getActivity());
                    colorDialog.setmListener(BaseFragment.this);
                    colorDialog.show();
                    break;
                case R.id.iv_save:
                    saveNote();
                    ((MainActivity) getActivity()).showListNoteFragment();
                    break;
                case R.id.iv_create_note_open_alarm:
                    openAlarm();
                    break;

                case R.id.btn_close_alarm:
                    closeAlarm();
                    break;

                case R.id.btn_date_picker:
                    new DatePickerDialog(getActivity(),
                            dateListener,
                            myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH))
                            .show();
                    break;
                case R.id.btn__time_picker:
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
        alarmmanager = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));
        launchIntent = new Intent(getActivity(), AlarmReceiver.class);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            photoPaths = savedInstanceState.getStringArrayList(PHOTO_PATHS);
            photoAdapter = new PhotoAdapter(getActivity(), photoPaths);
            lvPhoto.setAdapter(photoAdapter);
            edtTitle.setText(savedInstanceState.getString(TITLE));
            edtContent.setText(savedInstanceState.getString(CONTENT));
            isAlarm = savedInstanceState.getBoolean(ALARM);
            color = savedInstanceState.getString(COLOR);
            layoutCreateNote.setBackgroundColor(Color.parseColor(color));

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(PHOTO_PATHS, photoPaths);
        outState.putString(TITLE, edtTitle.getText().toString());
        outState.putString(CONTENT, edtContent.getText().toString());
        outState.putBoolean(ALARM, isAlarm);
        outState.putString(COLOR, color);
    }

    protected abstract void fillData();

    public void initViews() {
        myCalendar = Calendar.getInstance();
        database = new DatabaseManager(getActivity());

        lvPhoto = (RecyclerView) view.findViewById(R.id.lv_photo);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        lvPhoto.setLayoutManager(layoutManager);
        photoPaths.clear();
        photoAdapter = new PhotoAdapter(getActivity(), photoPaths);
        lvPhoto.setAdapter(photoAdapter);

        edtContent = (EditText) view.findViewById(R.id.et_content);
        edtContent.setText(null);
        edtTitle = (EditText) view.findViewById(R.id.et_title);
        edtTitle.setText(null);
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

        ivCamera = (ImageView) view.findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(listener);

        ivColor = (ImageView) view.findViewById(R.id.iv_color);
        ivColor.setOnClickListener(listener);

        ivSave = (ImageView) view.findViewById(R.id.iv_save);
        ivSave.setOnClickListener(listener);

        ivSetting = (ImageView) view.findViewById(R.id.iv_setting);
        ivSetting.setVisibility(View.GONE);

        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvDate.setText((myCalendar.get(Calendar.DAY_OF_MONTH) + 1) + "/"
                       +(myCalendar.get(Calendar.MONTH) + 1) + "/"
                       +myCalendar.get(Calendar.YEAR));
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTime.setText(getString(R.string.default_time));

        tvAlarm = (TextView) view.findViewById(R.id.tv_alarm);
        ivAlarm = (ImageView) view.findViewById(R.id.iv_create_note_open_alarm);
        ivAlarm.setOnClickListener(listener);
        ivAlarm.setImageLevel(0);

        btnCloseAlarm = (Button) view.findViewById(R.id.btn_close_alarm);
        btnCloseAlarm.setOnClickListener(listener);

        btnDatePicker = (Button) view.findViewById(R.id.btn_date_picker);
        btnDatePicker.setOnClickListener(listener);

        btnTimePicker = (Button) view.findViewById(R.id.btn__time_picker);
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
                                database.deleteNote(id);
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
        Toast.makeText(getActivity(), getString(R.string.turn_off_alarm), Toast.LENGTH_SHORT).show();
    }

    private void openAlarm() {
        layoutShowDateTime.setVisibility(View.VISIBLE);
        tvAlarm.setVisibility(View.GONE);
        if (!isAlarm) {
            ivAlarm.setImageLevel(1);
            isAlarm = true;
            Toast.makeText(getActivity(), getString(R.string.turn_on_alarm), Toast.LENGTH_SHORT).show();
        } else {
            ivAlarm.setImageLevel(0);
            isAlarm = false;
            Toast.makeText(getActivity(), getString(R.string.turn_off_alarm), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
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
            case BaseFragment.TAKE_PHOTO_CODE:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                super.startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
                break;
            case BaseFragment.CHOOSE_PHOTO_CODE:
                Intent getIntent = new Intent();
                getIntent.setAction(Intent.ACTION_GET_CONTENT);
                getIntent.setType(TYPE_IMAGE);
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType(TYPE_IMAGE);
                Intent chooserIntent = Intent.createChooser(getIntent, CHOOSER_IMAGE);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
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
                //Uri selectedImageUri = data.getData();
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getActivity(), photo);
                String newPath = saveToInternalStorage(tempUri);
                photoPaths.add(newPath);
                photoAdapter.notifyItemInserted(photoPaths.size()-1);
            }

            if (requestCode == GALLERY_REQUEST_CODE) {
                try {
                    Uri selectedImageUri = data.getData();
                    String newPath = saveToInternalStorage(selectedImageUri);
                    photoPaths.add(newPath);
                    photoAdapter.notifyItemInserted(photoPaths.size()-1);
                } catch (Exception e) {

                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String saveToInternalStorage(Uri uri) {
        File directory = new File(Environment.getExternalStorageDirectory()+File.separator + ROOT_DIRECTORY);
        if (!directory.exists()){
            directory.mkdirs();
        }
        String pathSrc = getRealPathFromURI(uri);
        File fileSrc = new File(pathSrc);
        String fileName = pathSrc.substring(pathSrc.lastIndexOf("/") + 1);

        String pathImage = Environment.getExternalStorageDirectory().getAbsoluteFile()+ "/" + ROOT_DIRECTORY + "/" + fileName;
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
        String[] proj = {MediaStore.Images.Media.DATA};
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

    public void setAlarmFire(int id, int day, int month, int year, int hour, int minute) {
        pendingIntent = PendingIntent.getBroadcast(getActivity(), id, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        alarmmanager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
