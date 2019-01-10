package com.technosales.net.votingreloded.votingVisuallyImpaired;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.adapters.PostResultAdaptar;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.pojoModels.PostList;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.util.ArrayList;
import java.util.List;

public class VoteResultsActivity extends AppCompatActivity implements View.OnClickListener {

    private List<PostList> postLists = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private RecyclerView postListView;
    private SharedPreferences preferences;
    private int clearDataCount = 0;
    private ImageView voteFinishImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_results);

        postListView = findViewById(R.id.postListView);
        voteFinishImage = findViewById(R.id.voteFinishImage);
        databaseHelper = new DatabaseHelper(this);

        voteFinishImage.setOnClickListener(this);
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        postLists = databaseHelper.postLists();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        postListView.setLayoutManager(linearLayoutManager);
        postListView.setHasFixedSize(true);
        postListView.setAdapter(new PostResultAdaptar(postLists, VoteResultsActivity.this));


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            clearDataCount++;
            if (clearDataCount == 3) {
                try {
                    // clearing app data
                    String packageName = getApplicationContext().getPackageName();
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec("pm clear " + packageName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_MULTIPLY && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            voteFinishImage.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voteFinishImage:
//                voteFinishImage.setVisibility(View.GONE);
                break;
        }
    }
}
