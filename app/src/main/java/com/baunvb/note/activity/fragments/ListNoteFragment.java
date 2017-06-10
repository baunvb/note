package com.baunvb.note.activity.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baunvb.note.activity.activity.MainActivity;
import com.baunvb.note.R;
import com.baunvb.note.custom.adapter.NoteAdapter;
import com.baunvb.note.db.DatabaseManager;
import com.baunvb.note.model.Note;

import java.util.ArrayList;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class ListNoteFragment extends Fragment implements View.OnClickListener, NoteAdapter.OnItemNoteClickListener {
    private View view;
    private RecyclerView rvListNote;
    private ArrayList<Note> listNote;
    private NoteAdapter adapter;
    private TextView tvNoNote;
    private ImageView ivAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_note, null);
        initViews();
        return view;
    }

    private void initViews() {
        listNote = new ArrayList<>();
        DatabaseManager database = new DatabaseManager(getActivity());
        listNote = database.getAllNotes();

        adapter = new NoteAdapter(getActivity(), listNote);
        adapter.notifyDataSetChanged();
        adapter.setListener(this);
        tvNoNote = (TextView) view.findViewById(R.id.tv_no_note);
        if (adapter.getItemCount() == 0){
            tvNoNote.setVisibility(View.VISIBLE);
        } else{
            tvNoNote.setVisibility(View.GONE);
        }

        rvListNote = (RecyclerView) view.findViewById(R.id.rv_list_note);
        int orient = getResources().getConfiguration().orientation;
        if (orient == Configuration.ORIENTATION_LANDSCAPE){
            rvListNote.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            rvListNote.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        rvListNote.setAdapter(adapter);

        ivAdd = (ImageView) view.findViewById(R.id.iv_list_note_add);
        ivAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_list_note_add:
                ((MainActivity)getActivity()).showCreateNoteFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickListener(int position) {
        ((MainActivity)getActivity()).setPosition(position);
        ((MainActivity)getActivity()).showEditNoteFragment();
    }
}
