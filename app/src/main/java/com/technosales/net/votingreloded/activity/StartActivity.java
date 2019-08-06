package com.technosales.net.votingreloded.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.io.File;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private File ballotDirectoryDevice;
    private File ballotDirectoryCard;
    private EditText deviceNumber;
    private EditText numberOfcolumns;
    private Button clrData;
    private TextView dateTime;
    private SharedPreferences preferences;
    private Button btn_mandatory, btn_nonMandatory,btn_nrna;
    private LinearLayout ll_typeElection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);


        boolean stagePassed = preferences.getBoolean(UtilStrings.STAGE_PASSED, false);

        if (stagePassed) {
            startActivity(new Intent(this, VotingActivity.class));
        }

        viewsInitialization();
        setClickListener();

        /*creating ballot folder*/
        ballotDirectoryDevice = new File(UtilStrings.BALLOT_PATH_DEVICE);
        ballotDirectoryCard = new File(UtilStrings.BALLOT_PATH_CARD);

        if (!ballotDirectoryDevice.isDirectory()) {
            GeneralUtils.createBallotFolder(ballotDirectoryDevice);
        }

        if (!ballotDirectoryCard.isDirectory()) {
            GeneralUtils.createBallotFolder(ballotDirectoryCard);
        }

    }

    private void setClickListener() {

        clrData.setOnClickListener(this);
        btn_mandatory.setOnClickListener(this);
        btn_nonMandatory.setOnClickListener(this);
        btn_nrna.setOnClickListener(this);

    }

    private void viewsInitialization() {
        deviceNumber = findViewById(R.id.deviceNumber);
        numberOfcolumns = findViewById(R.id.numberOfcolumns);
        clrData = findViewById(R.id.clrData);
        dateTime = findViewById(R.id.dateTime);
        btn_mandatory = findViewById(R.id.btn_mandatory);
        btn_nonMandatory = findViewById(R.id.btn_nonMandatory);
        btn_nrna = findViewById(R.id.btn_nrna);
        ll_typeElection = findViewById(R.id.ll_typeElection);

        dateTime.setText(GeneralUtils.getDateOnly());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.clrData:
                GeneralUtils.deleteFilesInBallot(ballotDirectoryDevice, ballotDirectoryCard);
                clrData.setVisibility(View.GONE);
                ll_typeElection.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_nonMandatory:
                preferences.edit().putBoolean(UtilStrings.MANDATORY, false).apply();
                preferences.edit().putBoolean(UtilStrings.ENGLISH_NAME_DISPLAY, false).apply();
                startNextActivity();
                break;
            case R.id.btn_mandatory:
                preferences.edit().putBoolean(UtilStrings.MANDATORY, true).apply();
                preferences.edit().putBoolean(UtilStrings.ENGLISH_NAME_DISPLAY, false).apply();
                startNextActivity();
                break;
            case R.id.btn_nrna:
                preferences.edit().putBoolean(UtilStrings.MANDATORY, false).apply();
                preferences.edit().putBoolean(UtilStrings.ENGLISH_NAME_DISPLAY, true).apply();
                startNextActivity();
                break;


        }
    }

    private void startNextActivity() {
        String deviceNo = deviceNumber.getText().toString().trim();
        String noColumns = numberOfcolumns.getText().toString().trim();
        if (deviceNo.length() > 0) {
            preferences.edit().putString(UtilStrings.DEVICE_NUMBER, deviceNo).apply();
            if (noColumns.length() > 0) {
                preferences.edit().putInt(UtilStrings.NO_OF_COLUMNS, Integer.parseInt(noColumns)).apply();
            } else {
                preferences.edit().putInt(UtilStrings.NO_OF_COLUMNS, 4).apply();
            }

            preferences.edit().putBoolean(UtilStrings.STAGE_PASSED, true).apply();
            GeneralUtils.createVoteTextFile(deviceNo, UtilStrings.BALLOT_PATH_DEVICE);
            GeneralUtils.createVoteTextFile(deviceNo, UtilStrings.BALLOT_PATH_CARD);
            startActivity(new Intent(this, VotingActivity.class));
            finish();
        } else {
            deviceNumber.setError("Enter Device Number");
        }

    }

}
