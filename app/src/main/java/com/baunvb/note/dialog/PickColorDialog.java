package com.baunvb.note.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baunvb.note.R;
import com.baunvb.note.fragments.CreateNoteFragment;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class PickColorDialog extends Dialog implements View.OnClickListener {
    private  Context context;
    private ImageView ivBlue;
    private ImageView ivPink;
    private ImageView ivOrange;
    private ImageView ivYellow;


    private PickerColorDialogListener listener;

    public void setListener(PickerColorDialogListener listener) {
        this.listener = listener;
    }

    public PickColorDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pick_color);
        iniViews();
        setCancelable(true);
        getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }


    private void iniViews() {
        ivBlue = (ImageView) findViewById(R.id.iv_color_blue);
        ivPink = (ImageView) findViewById(R.id.iv_color_pink);
        ivOrange = (ImageView) findViewById(R.id.iv_color_orange);
        ivYellow = (ImageView) findViewById(R.id.iv_color_yellow);

        ivBlue.setOnClickListener(this);
        ivPink.setOnClickListener(this);
        ivOrange.setOnClickListener(this);
        ivYellow.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_color_blue:
                listener.onSelectColorListener(CreateNoteFragment.BLUE);
                this.dismiss();
                break;
            case R.id.iv_color_pink:
                listener.onSelectColorListener(CreateNoteFragment.PINK);
                this.dismiss();
                break;
            case R.id.iv_color_orange:
                listener.onSelectColorListener(CreateNoteFragment.ORANGE);
                this.dismiss();
                break;
            case R.id.iv_color_yellow:
                listener.onSelectColorListener(CreateNoteFragment.YELLOW);
                this.dismiss();
                break;
            default:
                break;
        }

    }

    public interface PickerColorDialogListener {
        void onSelectColorListener(String color);
    }

}
