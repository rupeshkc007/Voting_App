package com.technosales.net.votingreloded.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import java.io.File;

public class GeneralUtils {

    public static void createBallotFolder(File file) {
        File ballot = new File(file.toString());
        ballot.mkdirs();
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
}
