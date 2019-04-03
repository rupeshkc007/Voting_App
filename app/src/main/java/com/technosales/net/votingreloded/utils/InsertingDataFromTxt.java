package com.technosales.net.votingreloded.utils;

import android.content.Context;
import android.view.View;

import com.technosales.net.votingreloded.R;
import com.technosales.net.votingreloded.activity.VotingActivity;
import com.technosales.net.votingreloded.helper.DatabaseHelper;
import com.technosales.net.votingreloded.votingVisuallyImpaired.VotingActivityVisual;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class InsertingDataFromTxt {
    private Context context;
    DatabaseHelper databaseHelper;

    public InsertingDataFromTxt(Context context) {
        this.context = context;
    }

    public void insertCandidatesTxt() {
        databaseHelper = new DatabaseHelper(context);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(UtilStrings.CANDIDATES_TXT_PATH), "UTF-16"), 100);

            String line = "";
            GeneralUtils.shortMessage(context, context.getString(R.string.inserting_data));
            try {
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");

                    String can_post_id = tokens[0];
                    String can_id = tokens[1];
                    String nepaliName = tokens[2];
                    String englishName = tokens[3];

                    String Idp = can_post_id;
                    String Idc = can_id;
                    if (Idp.length() < 2) {
                        Idp = "0" + Idp;
                    }
                    if (Idc.length() < 2) {
                        Idc = "00" + Idc;
                    } else if (Idc.length() == 2) {
                        Idc = "0" + Idc;
                    }


                    int initial = 0;
                    databaseHelper.insertCandidates(Integer.parseInt(can_post_id),
                            Idp + Idc,
                            nepaliName,
                            englishName,
                            initial);


                }
                insertPostTxt();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void insertPostTxt() {

        databaseHelper = new DatabaseHelper(context);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(UtilStrings.POSTS_TXT_PATH), "UTF-16"), 100);


            String line;
            try {

                while ((line = br.readLine()) != null) {

                    String[] tokens = line.split("\t");


                    int post_id = Integer.parseInt(tokens[0]);
                    String post_nepali = tokens[1];
                    String post_english = tokens[2];
                    int post_count = Integer.parseInt(tokens[3]);
                    databaseHelper.insertPosts(post_id, post_nepali, post_english, post_count);

                }
                GeneralUtils.shortMessage(context, context.getString(R.string.data_inserted));
                /*((VotingActivity) context).voting_cover_image.setImageResource(R.drawable.image2);
                ((VotingActivity) context).voterCountTxt.setVisibility(View.VISIBLE);
                ((VotingActivity) context).voterCountTxt.setText(context.getString(R.string.total_voters)+"0");*/
                ((VotingActivityVisual) context).voting_cover_image.setImageResource(R.drawable.image2);
                ((VotingActivityVisual) context).voterCountTxt.setVisibility(View.VISIBLE);
                ((VotingActivityVisual) context).voterCountTxt.setText(context.getString(R.string.total_voters) + "0");

            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
