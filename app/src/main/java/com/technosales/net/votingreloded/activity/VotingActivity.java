package com.technosales.net.votingreloded.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.pojoModels.CandidatesList;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.InsertingDataFromTxt;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class VotingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout canContainer;
    private SharedPreferences preferences;
    private int numberOfColumns;
    private int postId = 0;

    public ImageView voting_cover_image;
    private List<CandidatesList> candidatesLists = new ArrayList<>();
    private int candidatesData;
    private int postsData;
    private HashMap<String, String> vote = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);


        databaseHelper = new DatabaseHelper(this);

        candidatesData = databaseHelper.candidatesData();
        postsData = databaseHelper.postsData();


        initializePreferences();
        initializationViews();


    }

    private void getNameListFromPost() {
        voting_cover_image.setVisibility(View.GONE);
        candidatesLists = databaseHelper.voteProcess(1);

        generateRuntimeGridView();
    }

    private void generateRuntimeGridView() {
        int value = Math.abs(candidatesLists.size() / numberOfColumns);
        int rem = candidatesLists.size() % numberOfColumns;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels / numberOfColumns;
        int count = 0;

        canContainer.removeAllViews();

        canContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Log.i("CanmodelSize", "CanmodelSize:" + candidatesLists.size());
        for (int i = 0; i < value; i++) {
            LinearLayout itemsLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            itemsLayout.setWeightSum(numberOfColumns);
            itemsLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .65)));


            for (int j = 0; j < numberOfColumns; j++) {
                View view = getView(count);
                view.setLayoutParams(params);
                itemsLayout.addView(view);
                count++;
            }
            canContainer.addView(itemsLayout);

        }
        LinearLayout row = new LinearLayout(this);

        row.setWeightSum(numberOfColumns);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * .65)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        row.setWeightSum(numberOfColumns);

        for (int i = 0; i < rem; i++) {
            View view = getView(count);
            view.setLayoutParams(params);

            row.addView(view);
            count++;
        }
        canContainer.addView(row);


    }


    public View getView(int i) {
        final CandidatesList canList = candidatesLists.get(i);
        String canImageName = canList.can_id;
        final View candidateItemView = LayoutInflater.from(this).inflate(R.layout.candidates_item_layout, null);

        ImageView candidateImage = candidateItemView.findViewById(R.id.candidateImage);
        final ImageView swastikImage = candidateItemView.findViewById(R.id.candidateSwastik);
        TextView candidateName = candidateItemView.findViewById(R.id.candidateName);
        candidateName.setTextSize((float) (28 - (numberOfColumns * 1.5)));

        candidateName.setText(canList.can_nep_name);
//        alertImage(imageName, im, ".png");


        candidateItemView.setTag(canList.can_id);
        if (vote == null || vote.get(canList.can_id) == null || vote.get(canList.can_id).equals("0")) {
            swastikImage.setVisibility(View.INVISIBLE);

        } else {
            swastikImage.setVisibility(View.VISIBLE);

        }

        candidateItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                }, 1000);
                view.setClickable(false);
            }
        });
        return candidateItemView;
    }

    private void initializationViews() {
        canContainer = findViewById(R.id.canContainer);
        voting_cover_image = findViewById(R.id.voting_cover_image);

        if (databaseHelper.postsData() == 0) {
            voting_cover_image.setImageResource(R.drawable.image1);
        }


    }

    private void initializePreferences() {
        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);
        numberOfColumns = preferences.getInt(UtilStrings.NO_OF_COLUMNS, 0);
    }

    /*key Events from Keyboard*/


    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (keyCode == KeyEvent.KEYCODE_NUMPAD_6 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_2 && event.getSource() == InputDevice.SOURCE_KEYBOARD) {

            /*main functionHere*/
            if (databaseHelper.candidatesData() > 0 && databaseHelper.postsData() > 0) {
                getNameListFromPost();
            }


        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            try {
                // clearing app data
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_MULTIPLY && event.getSource() == InputDevice.SOURCE_KEYBOARD) {
            /*insert data here*/

            if (databaseHelper.candidatesData() == 0 && databaseHelper.postsData() == 0) {
                new InsertingDataFromTxt(this).insertCandidatesTxt();
            }

        }


        return false;
    }

}
