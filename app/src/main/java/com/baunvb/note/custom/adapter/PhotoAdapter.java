package com.baunvb.note.custom.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.baunvb.note.R;
import com.baunvb.note.activity.activity.MainActivity;
import com.baunvb.note.db.DatabaseManager;
import com.baunvb.note.model.Photo;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private ArrayList<String> photos;
    private Context context;

    public PhotoAdapter(Context context, ArrayList<String> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, final int position) {
        final File imgFile = new File(photos.get(position));
        Bitmap bmPhoto = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgFile.getAbsolutePath()), 500, 500 );
        //Picasso.with(context).load(imgFile).into(holder.iv_create_note_photo);
        holder.iv_create_note_photo.setImageBitmap(bmPhoto);
        holder.btn_create_note_close_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.notification_delete))
                        .setCancelable(false)
                        .setTitle(context.getString(R.string.delete_label))
                        .setPositiveButton(context.getString(R.string.yes_label),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        photos.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, photos.size());
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(context.getString(R.string.no_label),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(imgFile), "image/*");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        View itemView;
        AppCompatImageView iv_create_note_photo;
        Button btn_create_note_close_photo;

        public PhotoHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_create_note_photo = (AppCompatImageView) itemView.findViewById(R.id.iv_create_note_photo);
            btn_create_note_close_photo = (Button) itemView.findViewById(R.id.btn_create_note_close_photo);
        }
    }

}
