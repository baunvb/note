package com.baunvb.note.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.baunvb.note.R;
import com.baunvb.note.activity.fragments.CreateNoteFragment;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class InsertPictureDialog extends Dialog implements View.OnClickListener {
    private  Context mContext;
    private LinearLayout llCamera;
    private LinearLayout llGallery;


    private InsertPictureDialogListener listener;
    public void setListener(InsertPictureDialogListener listener) {
        this.listener = listener;
    }

    public InsertPictureDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_insert_picture);
        iniViews();
        setCancelable(true);
        getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void iniViews() {
        llCamera = (LinearLayout) findViewById(R.id.ll_dialog_insert_picture_camera);
        llCamera.setOnClickListener(this);
        llGallery = (LinearLayout) findViewById(R.id.ll_dialog_insert_picture_gallery);
        llGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_dialog_insert_picture_camera:
                listener.onInsertListener(CreateNoteFragment.TAKE_PHOTO);
                this.dismiss();
                break;
            case R.id.ll_dialog_insert_picture_gallery:
                listener.onInsertListener(CreateNoteFragment.CHOOSE_PHOTO);
                this.dismiss();;
                break;

        }
    }

    public interface InsertPictureDialogListener {
        void onInsertListener(int typeInsert);
    }

}
