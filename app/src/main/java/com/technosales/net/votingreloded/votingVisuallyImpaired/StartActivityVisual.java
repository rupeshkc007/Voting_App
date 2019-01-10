package com.technosales.net.votingreloded.votingVisuallyImpaired;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
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

        if (!ballotDirectoryDevice.isDirectory()) {
            GeneralUtils.createBallotFolder(ballotDirectoryDevice);
        }

        if (!ballotDirectoryCard.isDirectory()) {
            GeneralUtils.createBallotFolder(ballotDirectoryCard);
        }


        if (stagePassed) {
            startActivity(new Intent(this, VotingActivityVisual.class));
        }

        viewsInitialization();

        setClickListener();


    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int resultread = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int rwrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (resultread == PackageManager.PERMISSION_GRANTED && rwrite == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false


        return false;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /*creating ballot folder*/
                ballotDirectoryDevice = new File(UtilStrings.BALLOT_PATH_DEVICE);
                ballotDirectoryCard = new File(UtilStrings.BALLOT_PATH_CARD);

                if (!ballotDirectoryDevice.isDirectory()) {
                    GeneralUtils.createBallotFolder(ballotDirectoryDevice);
                }

                if (!ballotDirectoryCard.isDirectory()) {
                    GeneralUtils.createBallotFolder(ballotDirectoryCard);
                }
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
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

}
