package com.technosales.net.votingreloded.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GeneralUtils {

    public static void createBallotFolder(File file) {
        File ballot = new File(file.toString());
        ballot.mkdirs();
    }

    public static void createVoteTextFile(String device_id, String path) {
        File txtFile = new File(path + "/" + device_id + ".txt");
        if (!txtFile.exists()) {

            final File files = new File(path + "/" + device_id + ".txt");
            // Save your stream, don't forget to flush() it before closing it.

            try {
                files.createNewFile();

            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }

        }

    }

    public static void createVoteStartEndTimeFile(String device_id, String path, String startEnd) {
        File txtFile = new File(path + "/" + device_id + getVoteStartEndTime() + startEnd + ".txt");
        if (!txtFile.exists()) {

            final File files = new File(path + "/" + device_id + getVoteStartEndTime() + startEnd + ".txt");
            // Save your stream, don't forget to flush() it before closing it.

            try {
                files.createNewFile();
                writeInTxt(txtFile, getVoteStartEndTime() + startEnd);
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }

        } else {
            writeInTxt(txtFile, getVoteStartEndTime());
        }

    }

    public static void writeInTxt(File txtFile, String data) {
        try {
            FileOutputStream fOut = new FileOutputStream(txtFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void deleteFilesInBallot(File deviceFile, File cardFile) {
        File device = new File(deviceFile.toString());
        String[] childrenDevice = device.list();
        for (int i = 0; i < childrenDevice.length; i++) {
            new File(device, childrenDevice[i]).delete();
        }
        try {

            File card = new File(cardFile.toString());
            String[] childrenCard = card.list();
            for (int i = 0; i < childrenCard.length; i++) {
                new File(card, childrenCard[i]).delete();
            }
        } catch (Exception ex) {
        }
    }

    public static void shortMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yMMddHHmmss");
        return df.format(c.getTime());
    }

    public static String getDateOnly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("[y-MM-dd]");
        return df.format(c.getTime());
    }

    public static String getVoteStartEndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("_y_MM_dd_HH_mm_");
        return df.format(c.getTime());
    }

    public static void canImagePath(String filename, ImageView view) {


        File imageFile = new File(UtilStrings.CANDIDATES_IMAGE_PATH + filename + ".png");
        if (imageFile.exists()) {
            view.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));

        }
    }

    public static int getDatePass() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        return Integer.parseInt(df.format(c.getTime()));

    }

    public static int getTimePass() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HHmm");
        return Integer.parseInt(df.format(c.getTime()));

    }

    public static boolean passwordMatch(String password) {
        boolean matched = false;
        int time = GeneralUtils.getTimePass();
        int date = GeneralUtils.getDatePass();

        int mul = time * time;
        int sub = 9999999 - mul;
        int pass = sub + date;
        if (password.length() > 0) {
            int enterdPass = Integer.parseInt(password);

            if (pass == enterdPass) {
                matched = true;
            } else {
                matched = false;
            }
        } else {
            matched = false;
        }
        return matched;
    }

    public static long getDelayTime(int charLength) {
        return charLength / 12 * 1000 + 3000;
    }

    public static int summaryColumnSpan(int size) {

        if (size <= 18) {
            return 3;
        } else if (size > 18 && size <= 24) {
            return 4;
        } else if (size > 24 && size <= 40) {
            return 5;
        } else if (size > 40 && size <= 60) {
            return 6;
        } else if (size > 60 && size <= 80) {
            return 7;
        } else {
            return 8;
        }

    }
}
