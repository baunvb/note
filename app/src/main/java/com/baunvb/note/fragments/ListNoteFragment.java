package com.baunvb.note.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baunvb.note.MainActivity;
import com.baunvb.note.R;
import com.baunvb.note.adapter.NoteAdapter;
import com.baunvb.note.item.Note;

import java.util.ArrayList;

/**
 * Created by Baunvb on 4/17/2017.
 */

public class ListNoteFragment extends Fragment implements View.OnClickListener, NoteAdapter.OnItemNoteClickListener {
    public static final String LIST_NOTE_FRAG_KEY = "list note fragment";
    private View view;
    private RecyclerView rvListNote;
    private ArrayList<Note> listNote;
    private NoteAdapter adapter;

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
        MainActivity mainActivity = (MainActivity)getActivity();
        listNote = mainActivity.getDatabase().getAll();

        adapter = new NoteAdapter(getActivity(), listNote);
        adapter.notifyDataSetChanged();
        adapter.setListener(this);

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
