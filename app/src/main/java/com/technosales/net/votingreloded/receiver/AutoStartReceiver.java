package com.technosales.net.votingreloded.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.technosales.net.votingreloded.activity.StartActivity;

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        Intent i = new Intent(context, StartActivity.class);  //MyActivity can be anything start on bootup...
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);


    }
}
