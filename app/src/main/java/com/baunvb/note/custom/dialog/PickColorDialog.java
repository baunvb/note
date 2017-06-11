package com.baunvb.note.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baunvb.note.R;
import com.baunvb.note.activity.fragments.CreateNoteFragment;
import com.baunvb.note.utils.Constant;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class PickColorDialog extends Dialog implements View.OnClickListener {
    private  Context mContext;
    private ImageView ivBlue;
    private ImageView ivPink;
    private ImageView ivOrange;
    private ImageView ivYellow;


    private PickerColorDialogListener mListener;

    public void setmListener(PickerColorDialogListener mListener) {
        this.mListener = mListener;
    }

    public PickColorDialog(Context context) {
        super(context);
        mContext = context;
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
                mListener.onSelectColorListener(Constant.BLUE);
                this.dismiss();
                break;
            case R.id.iv_color_pink:
                mListener.onSelectColorListener(Constant.PINK);
                this.dismiss();
                break;
            case R.id.iv_color_orange:
                mListener.onSelectColorListener(Constant.ORANGE);
                this.dismiss();
                break;
            case R.id.iv_color_yellow:
                mListener.onSelectColorListener(Constant.YELLOW);
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
