package com.baunvb.note.custom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baunvb.note.R;
import com.baunvb.note.activity.fragments.CreateNoteFragment;
import com.baunvb.note.model.Note;
import com.baunvb.note.utils.Constant;

import java.util.ArrayList;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter{
    private ArrayList<Note> listNote;
    private Context context;
    private OnItemNoteClickListener listener;

    public void setListener(OnItemNoteClickListener listener) {
        this.listener = listener;
    }

    public NoteAdapter(Context context, ArrayList<Note> listNote) {
        this.context = context;
        this.listNote = listNote;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemNote = View.inflate(context, R.layout.item_note, null);
        RecyclerView.LayoutParams params
                = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        itemNote.setLayoutParams(params);

        return new NoteHolder(itemNote);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,  final int position) {
        final NoteHolder noteHolder = (NoteHolder) holder;
        Note note = listNote.get(position);
        noteHolder.tvTitle.setText(note.getTitle());
        noteHolder.tvContent.setText(note.getContent());
        noteHolder.tvDate.setText(note.getDate() + " " +note.getTime());
        noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickListener(noteHolder.getAdapterPosition());
            }
        });

        String color = note.getColor();
        switch (color){
            case Constant.BLUE:
                noteHolder.layout.setBackgroundResource(R.drawable.bg_item_note_blue);
                break;
            case Constant.ORANGE:
                noteHolder.layout.setBackgroundResource(R.drawable.bg_item_note_orange);
                break;
            case Constant.PINK:
                noteHolder.layout.setBackgroundResource(R.drawable.bg_item_note_pink);
                break;
            case Constant.YELLOW:
                noteHolder.layout.setBackgroundResource(R.drawable.bg_item_note_yellow);
                break;
        }

        int alarm = note.getAlarm();
         switch (alarm){
             case 1:
                 noteHolder.imgAlarm.setVisibility(View.VISIBLE);
                 break;
             case 0:
                 noteHolder.imgAlarm.setVisibility(View.INVISIBLE);
                 break;
             default:
                 break;
         }

    }

    @Override
    public int getItemCount() {
        return listNote.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle, tvContent, tvDate;
        private ImageView imgAlarm;
        private LinearLayout layout;
        public NoteHolder(View itemNote) {
            super(itemNote);
            tvTitle = (TextView) itemNote.findViewById(R.id.tv_item_title);
            tvContent = (TextView) itemNote.findViewById(R.id.tv_item_content);
            tvDate = (TextView) itemNote.findViewById(R.id.tv_item_date);
            imgAlarm = (ImageView) itemNote.findViewById(R.id.iv_item_alarm);
            layout = (LinearLayout) itemNote.findViewById(R.id.layout_item_note);
        }
    }

    public interface OnItemNoteClickListener {
        void onClickListener(int position);
    }
}
