package com.example.voting;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voting.database.Poll;
import com.example.voting.database.VoteItem;
import com.example.voting.database.VotingDatabaseHandler;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoteActivity extends AppCompatActivity {
    private final List<VoteItem> data = new ArrayList<VoteItem>();
    private VotingDatabaseHandler db;
    private PollState pollState;
    final VoteAdapter adapter = new VoteAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        data.add(new VoteItem(1, "Vote 1"));
//        data.add(new VoteItem(2, "Vote 2"));
//        data.add(new VoteItem(3, "Vote 3"));
//        data.add(new VoteItem(4, "Vote 4"));
        super.onCreate(savedInstanceState);
        db = new VotingDatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_view_poll);
        Objects.requireNonNull(getSupportActionBar()).hide();
        RecyclerView rv = findViewById(R.id.votes_options_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        rv.setAdapter(adapter);
        registerForContextMenu(rv);
//        adapter.submitList(data);

        adapter.setOnItemClickListener(new VoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(VoteItem model) {
                // after clicking on item of recycler view
                // we are opening a new activity and passing
                // a data to our activity.
                for (int i = 0; i < data.size(); i++) {
                    VoteItem curr = data.get(i);

                    if (curr.getBody() == model.getBody()) curr.setChecked(true);
                    else curr.setChecked(false);
                }

               adapter.notifyDataSetChanged();
                adapter.submitList(data);
            }
        });

        MaterialToolbar mtr = findViewById(R.id.topAppBar);
        mtr.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoteActivity.super.onBackPressed();
            }
        });
        final View view = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Enter option");
        alertDialog.setView(view);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final EditText editText = (EditText) view.findViewById(R.id.voting_option_edit_txt);
                data.add(new VoteItem(data.size() + 1, editText.getText().toString(), -1));
               adapter.notifyDataSetChanged();
                adapter.submitList(data);
                editText.setText("");
            }
        });
        Button addOptionBtn = findViewById(R.id.add_btn);
        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
            }
        });


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.delete_menu){
            int position = -1;
            position = adapter.getPosition();
            try {
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
                return super.onContextItemSelected(item);
            }

            data.remove(position);
            adapter.notifyDataSetChanged();
            adapter.submitList(data);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String edit_state = intent.getStringExtra("edit_state");
        if(edit_state == null) {
            finish();
            return;
        }
        RecyclerView rv = findViewById(R.id.votes_options_rv);
        Button addOptionBtn = findViewById(R.id.add_btn);
        Button saveBtn = (Button) findViewById(R.id.save_btn);
        EditText titleEditTxt = (EditText) findViewById(R.id.title_view);
        EditText descEditTxt = (EditText) findViewById(R.id.desc_view);

        if(id != 0){
            Cursor cursor = db.getPollById(String.valueOf(id));
            if(cursor == null){
                finish();
                return;
            }

            pollState = new PollState(cursor.getString(1), cursor.getString(2), cursor.getInt(0), EDITABLE_STATE.NOT_EDITABLE);
            db.closeDB();
            addOptionBtn.setVisibility(View.INVISIBLE);
            titleEditTxt.setText(pollState.getTitle());
            titleEditTxt.setEnabled(false);
            descEditTxt.setText(pollState.getDesc());
            descEditTxt.setEnabled(false);

            // get data from db
            data.clear();
            cursor = db.getVotes(String.valueOf(id));
            do {
                data.add(new VoteItem(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("body")), cursor.getInt(cursor.getColumnIndex("votes"))));
            } while (cursor.moveToNext());
            cursor.close();
            db.closeDB();
           adapter.notifyDataSetChanged();
            adapter.submitList(data);
        }else{
            pollState = new PollState("", "", 0, EDITABLE_STATE.EDITABLE);
            addOptionBtn.setVisibility(View.VISIBLE);
            titleEditTxt.setEnabled(true);
            descEditTxt.setEnabled(true);
        }



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pollState.getState() == EDITABLE_STATE.EDITABLE){
                    ArrayList<String> options = new ArrayList<String>();
                    for (int i = 0; i < data.size(); i++) {
                        options.add(data.get(i).getBody());
                    }
                    if(titleEditTxt.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Title!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(descEditTxt.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please Enter Description!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(options.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please add voting options!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    db.newPoll(titleEditTxt.getText().toString(), descEditTxt.getText().toString(), options);
                    Toast.makeText(getApplicationContext(), "Successfully Added Poll!",
                            Toast.LENGTH_LONG).show();
                }else{
                    boolean voted = false;
                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).isChecked()){
                            voted = true;
                            Log.i("TAG", "onStart: "+data.get(i).getId());

                            db.addVote(data.get(i).getId());
                        }
                    }
                    if(!voted) {
                        Toast.makeText(getApplicationContext(), "Please choose your vote!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Successfully Added A Vote!",
                            Toast.LENGTH_LONG).show();
                }

                finish();
            }
        });

    }
}