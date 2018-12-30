package com.technosales.net.votingreloded.utils;

import android.os.Environment;

public class UtilStrings {
    public static String BALLOT_PATH_CARD = "/mnt/extsd/votingmachine/ballot";
    public static String BALLOT_PATH_DEVICE = Environment.getExternalStorageDirectory() + "/ballot";

    public static String CANDIDATES_TXT_PATH = "/mnt/extsd/votingmachine/csv/candidates.txt";
    public static String POSTS_TXT_PATH = "/mnt/extsd/votingmachine/csv/posts.txt";


    public static String SHARED_PREFERENCES = "preferences";
    public static String DEVICE_NUMBER = "device_number";
    public static String NO_OF_COLUMNS = "no_of_columns";
    public static String STAGE_PASSED = "stage_passed";


}
