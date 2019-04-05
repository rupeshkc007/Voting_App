package com.technosales.net.votingreloded.utils;

import android.os.Build;
import android.os.Environment;

public class UtilStrings {
    public static String BALLOT_PATH_CARD = BALLOT_PATH_CARD();
    //    public static String BALLOT_PATH_CARD = "/mnt/extsd/votingmachine/ballot";
    public static String BALLOT_PATH_DEVICE = Environment.getExternalStorageDirectory() + "/ballot";
    //    public static String VOTINGMACHINE_FOLDER_PATH = "/mnt/extsd/votingmachine/";
//    public static String VOTINGMACHINE_FOLDER_PATH = "/storage/extsd/votingmachine/";
//    public static String VOTINGMACHINE_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/votingmachine/";//onlyForVisual
    public static String CANDIDATES_TXT_PATH = VOTINGMACHINE_FOLDER_PATH() + "csv/candidates.txt";
    public static String POSTS_TXT_PATH = VOTINGMACHINE_FOLDER_PATH() + "csv/posts.txt";
    public static String CANDIDATES_IMAGE_PATH = VOTINGMACHINE_FOLDER_PATH() + "pictures/";


    public static String SHARED_PREFERENCES = "preferences";
    public static String DEVICE_NUMBER = "device_number";
    public static String NO_OF_COLUMNS = "no_of_columns";
    public static String STAGE_PASSED = "stage_passed";
    public static String VOTE_STAGE_PASSED = "stage_completed";

    public static String VOTER_COUNT = "voter_count";

    public static String SCREENSHOT_DEVICE = BALLOT_PATH_DEVICE + "/";
    public static String SCREENSHOT_CARD = BALLOT_PATH_CARD() + "/";


    public static final int READ_STORAGE_CODE = 101;
    public static final int WRITE_STORAGE_CODE = 400;


    public static final String VOTINGMACHINE_FOLDER_PATH() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return Environment.getExternalStorageDirectory() + "/votingmachine/";
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            return "/storage/extsd/votingmachine/";
        } else if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return "/mnt/extsd/votingmachine/";
        }

        return "";
    }

    public static final String BALLOT_PATH_CARD() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return "/storage/extsd/votingmachine/ballot";
        } else {
            return "/mnt/extsd/votingmachine/ballot";
        }

    }


}
