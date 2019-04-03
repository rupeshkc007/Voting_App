package com.technosales.net.votingreloded.votingVisuallyImpaired;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.utils.CheckAskPermission;
import com.technosales.net.votingreloded.utils.GeneralUtils;
import com.technosales.net.votingreloded.utils.UtilStrings;

import java.io.File;

public class StartActivityVisual extends AppCompatActivity implements View.OnClickListener {

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
    private DatabaseHelper databaseHelper;
    private int STORAGE_PERMISSION_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences(UtilStrings.SHARED_PREFERENCES, 0);


        stagePassed = preferences.getBoolean(UtilStrings.STAGE_PASSED, false);


        /*creating ballot folder*/
        ballotDirectoryDevice = new File(UtilStrings.BALLOT_PATH_DEVICE);
        ballotDirectoryCard = new File(UtilStrings.BALLOT_PATH_CARD);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CheckAskPermission.isReadStorageAllowed(StartActivityVisual.this)) {

                CheckAskPermission.askReadStorage(StartActivityVisual.this);
            } else {

                if (CheckAskPermission.isWriteStorageAllowed(StartActivityVisual.this))
                    CheckAskPermission.askWriteStorage(StartActivityVisual.this);

            }
        } else {
            if (!ballotDirectoryDevice.isDirectory()) {
                GeneralUtils.createBallotFolder(ballotDirectoryDevice);
            }

            if (!ballotDirectoryCard.isDirectory()) {
                GeneralUtils.createBallotFolder(ballotDirectoryCard);
            }
        }


        if (stagePassed) {
            startActivity(new Intent(this, VotingActivityVisual.class));
        }

        viewsInitialization();

        setClickListener();


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

        dateTime.setText(GeneralUtils.getDateOnly());
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
                        preferences.edit().putInt(UtilStrings.NO_OF_COLUMNS, 4).apply();
                    }

                    preferences.edit().putBoolean(UtilStrings.STAGE_PASSED, true).apply();
                    GeneralUtils.createVoteTextFile(deviceNo, UtilStrings.BALLOT_PATH_DEVICE);
                    GeneralUtils.createVoteTextFile(deviceNo, UtilStrings.BALLOT_PATH_CARD);
                    startActivity(new Intent(this, VotingActivityVisual.class));
                    finish();
                } else {
                    deviceNumber.setError("Enter Device Number");
                }


                break;

            case R.id.clrData:
                try {

                    GeneralUtils.deleteFilesInBallot(ballotDirectoryDevice, ballotDirectoryCard);
                } catch (Exception ex) {

                }
                clrData.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UtilStrings.READ_STORAGE_CODE) {

            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    CheckAskPermission.askReadStorage(StartActivityVisual.this);

                    break;
                } else {


                    if (CheckAskPermission.isWriteStorageAllowed(StartActivityVisual.this))
                        CheckAskPermission.askWriteStorage(StartActivityVisual.this);
                }
            }
        } else if (requestCode == UtilStrings.WRITE_STORAGE_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    CheckAskPermission.askWriteStorage(StartActivityVisual.this);
                    break;
                } else {
                    if (!ballotDirectoryDevice.isDirectory()) {
                        GeneralUtils.createBallotFolder(ballotDirectoryDevice);
                    }

                    if (!ballotDirectoryCard.isDirectory()) {
                        GeneralUtils.createBallotFolder(ballotDirectoryCard);
                    }

                }
            }

        }
    }

}
