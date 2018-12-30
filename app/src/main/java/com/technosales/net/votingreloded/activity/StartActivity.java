package com.technosales.net.votingreloded.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.io.File;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private File ballotDirectoryDevice;
    private File ballotDirectoryCard;
    private ShimmerFrameLayout shimmerFrameLayout;
    private EditText deviceNumber;
    private EditText numberOfcolumns;
    private Button enterPass;
    private Button clrData;
    private TextView dateTime;
    private SharedPreferences preferences;
    private boolean stagePassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);


        stagePassed = preferences.getBoolean(UtilStrings.STAGE_PASSED, false);

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

        enterPass.setOnClickListener(this);
        clrData.setOnClickListener(this);

    }

    private void viewsInitialization() {
        deviceNumber = findViewById(R.id.deviceNumber);
        numberOfcolumns = findViewById(R.id.numberOfcolumns);
        enterPass = findViewById(R.id.enterPass);
        clrData = findViewById(R.id.clrData);
        dateTime = findViewById(R.id.dateTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enterPass:

                String deviceNo = deviceNumber.getText().toString().trim();
                String noColumns = numberOfcolumns.getText().toString().trim();
                if (deviceNo.length() > 0) {
                    preferences.edit().putString(UtilStrings.DEVICE_NUMBER, deviceNo).apply();
                    if (noColumns.length() > 0) {
                        preferences.edit().putInt(UtilStrings.NO_OF_COLUMNS, Integer.parseInt(noColumns)).apply();
                    } else {
                        preferences.edit().putInt(UtilStrings.NO_OF_COLUMNS, 3).apply();
                    }

                    preferences.edit().putBoolean(UtilStrings.STAGE_PASSED, true).apply();
                    startActivity(new Intent(this, VotingActivity.class));
                } else {
                    deviceNumber.setError("Enter Device Number");
                }


                break;

            case R.id.clrData:
                GeneralUtils.deleteFilesInBallot(ballotDirectoryDevice, ballotDirectoryCard);
                break;
        }
    }

}
