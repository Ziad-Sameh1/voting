package com.example.voting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voting.database.Poll;
import com.example.voting.database.PollAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<Poll> data = new ArrayList<Poll>();

    //    private Button btn;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data.add(new Poll("Test 1", "Desc", 1));
        data.add(new Poll("Test 2", "Desc", 2));
        data.add(new Poll("Test 3", "Desc", 3));
        data.add(new Poll("Test 4", "Desc", 4));
        data.add(new Poll("Test 5", "Desc", 5));
        setContentView(R.layout.activity_main);
//        btn = findViewById(R.id.search_btn);
//        Shader textShader = new LinearGradient(0, 0, btn.getPaint().measureText(btn.getText().toString()), btn.getTextSize(),
//                new int[]{
//                        Color.parseColor("#3bf1f4"),
//                        Color.parseColor("#bca1f7"),
//                }, null, Shader.TileMode.CLAMP);
//        btn.getPaint().setShader(textShader);
        RecyclerView rv = findViewById(R.id.polls_list_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        // initializing adapter for recycler view.
        final PollAdapter adapter = new PollAdapter();
        // setting adapter class for recycler view.
        rv.setAdapter(adapter);
        adapter.submitList(data);
        adapter.setOnItemClickListener(new PollAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Poll model) {
                // after clicking on item of recycler view
                // we are opening a new activity and passing
                // a data to our activity.
                Intent intent = new Intent(MainActivity.this, ViewPollActivity.class);
                startActivity(intent);
                Log.i("TAG", "onItemClick: id -> " + model.getId());
            }
        });
    }
}