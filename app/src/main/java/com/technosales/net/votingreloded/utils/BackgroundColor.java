package com.technosales.net.votingreloded.utils;

import android.content.Context;
import android.view.View;

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


        switch (postId % 10) {
            case 1:
                container.setBackgroundColor(colorOne);
                break;
            case 2:
                container.setBackgroundColor(colorTwo);
                break;
            case 3:
                container.setBackgroundColor(colorThree);
                break;
            case 4:
                container.setBackgroundColor(colorFour);
                break;
            case 5:
                container.setBackgroundColor(colorFive);
                break;
            case 6:
                container.setBackgroundColor(colorSix);
                break;
            case 7:
                container.setBackgroundColor(colorSeven);
                break;
            case 8:
                container.setBackgroundColor(colorEight);
                break;
            case 9:
                container.setBackgroundColor(colorNine);
                break;
            case 0:
                container.setBackgroundColor(colorTen);
                break;
        }
    }
       /* if (postId % 10 == 1) {
            container.setBackgroundColor(colorOne);

        } else if (postId % 10 == 2) {
            container.setBackgroundColor(colorTwo);
        } else if (postId % 10 == 3) {
            container.setBackgroundColor(colorThree);
        } else if (postId % 10 == 4) {
            container.setBackgroundColor(colorFour);
        } else if (postId % 10 == 5) {
            container.setBackgroundColor(colorFive);
        } else if (postId % 10 == 6) {
            container.setBackgroundColor(colorSix);
        } else if (postId % 10 == 7) {
            container.setBackgroundColor(colorSeven);
        } else if (postId % 10 == 8) {
            container.setBackgroundColor(colorEight);
        } else if (postId % 10 == 9) {
            container.setBackgroundColor(colorNine);
        } else if (postId % 10 == 0) {
            container.setBackgroundColor(colorTen);
        } *//*else if (postId %10 == 11) {
            container.setBackgroundColor(colorElev);
        } else if (postId %10 == 12) {
            container.setBackgroundColor(colorTwel);
        } else if (postId %10 == 13) {
            container.setBackgroundColor(colorThirt);
        } else {
            container.setBackgroundColor(colorWhite);
        }*//*
    }*/
}
