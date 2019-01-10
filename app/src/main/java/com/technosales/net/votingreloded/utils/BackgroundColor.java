package com.technosales.net.votingreloded.utils;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.technosales.net.votingreloded.R;

public class BackgroundColor {

    public static void setBackroundColor(Context context, int postId, View container) {
        int colorOne = context.getResources().getColor(R.color.colorOne);
        int colorTwo = context.getResources().getColor(R.color.colorTwo);
        int colorThree = context.getResources().getColor(R.color.colorThree);
        int colorFour = context.getResources().getColor(R.color.colorFour);
        int colorFive = context.getResources().getColor(R.color.colorFive);
        int colorSix = context.getResources().getColor(R.color.colorSix);
        int colorSeven = context.getResources().getColor(R.color.colorSeven);
        int colorEight = context.getResources().getColor(R.color.colorEight);
        int colorNine = context.getResources().getColor(R.color.colorNine);
        int colorTen = context.getResources().getColor(R.color.colorTen);
        int colorElev = context.getResources().getColor(R.color.colorElev);
        int colorTwel = context.getResources().getColor(R.color.colorTwel);
        int colorThirt = context.getResources().getColor(R.color.colorThirt);
        int colorWhite = context.getResources().getColor(R.color.white);


        if (postId == 1) {
            container.setBackgroundColor(colorOne);

        } else if (postId == 2) {
            container.setBackgroundColor(colorTwo);
        } else if (postId == 3) {
            container.setBackgroundColor(colorThree);
        } else if (postId == 4) {
            container.setBackgroundColor(colorFour);
        } else if (postId == 5) {
            container.setBackgroundColor(colorFive);
        } else if (postId == 6) {
            container.setBackgroundColor(colorSix);
        } else if (postId == 7) {
            container.setBackgroundColor(colorSeven);
        } else if (postId == 8) {
            container.setBackgroundColor(colorEight);
        } else if (postId == 9) {
            container.setBackgroundColor(colorNine);
        } else if (postId == 10) {
            container.setBackgroundColor(colorTen);
        } else if (postId == 11) {
            container.setBackgroundColor(colorElev);
        } else if (postId == 12) {
            container.setBackgroundColor(colorTwel);
        } else if (postId == 13) {
            container.setBackgroundColor(colorThirt);
        } else {
            container.setBackgroundColor(colorWhite);
        }
    }
}
