package com.technosales.net.votingreloded;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technosales.net.votingreloded.utils.GeneralUtils;

public class CustomAlertDialog {
    public static AlertDialog customAlert(final Context context) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.input_password, null);
        alertDialog.setTitle("Finish Vote");

        final EditText enterPass = view.findViewById(R.id.enterPass);
        TextView alertDateView = view.findViewById(R.id.alertDateView);
        Button alertPassBtn = view.findViewById(R.id.alertPassBtn);

        alertDateView.setText(GeneralUtils.getDateOnly());

        alertPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = GeneralUtils.getTimePass();
            int date = GeneralUtils.getDatePass();

            int mul = time * time;
            int sub = 9999999 - mul;
            int pass = sub + date;
                if (enterPass.getText().toString().trim().length() > 0) {
                int enterdPass = Integer.parseInt(enterPass.getText().toString().trim());

                if (pass == enterdPass) {
                    Toast.makeText(context, "Matched", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "NOT Matched", Toast.LENGTH_SHORT).show();
            }
        }
        });


        alertDialog.setView(view);

        AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


}
