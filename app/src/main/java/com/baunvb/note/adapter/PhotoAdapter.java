package com.baunvb.note.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baunvb.note.R;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private ArrayList<String> photos;
    private Context context;

    public PhotoAdapter(Context context,ArrayList<String> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, final int position) {
        File imgFile = new File(photos.get(position));
        Bitmap bmPhoto = null;
        if (imgFile.exists()) {
            bmPhoto = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (bmPhoto == null){
                photos.remove(position);
            } else {
                holder.iv_create_note_photo.setImageBitmap(bmPhoto);
            }
        }

        holder.btn_create_note_close_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(photos.get(position))));
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
               // Uri uri = Uri.parse("file://" + file.getAbsolutePath());
                File file = new File(photos.get(position));

                Uri uri = Uri.parse(photos.get(position));
                intent.setDataAndType(Uri.fromFile(file),"image/*");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {
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
