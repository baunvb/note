package com.baunvb.note.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.baunvb.note.R;

import java.util.ArrayList;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    ArrayList<Bitmap> photos;

    public PhotoAdapter(ArrayList<Bitmap> photos) {
        this.photos = photos;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, final int position) {
        holder.iv_create_note_photo.setImageBitmap(photos.get(position));
        holder.btn_create_note_close_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photos.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_create_note_photo;
        Button btn_create_note_close_photo;

        public PhotoHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_create_note_photo = (ImageView) itemView.findViewById(R.id.iv_create_note_photo);
            btn_create_note_close_photo = (Button) itemView.findViewById(R.id.btn_create_note_close_photo);
        }
    }
}
