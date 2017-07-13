package com.maxchehab.remotelinuxunlocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class ComputerListActivity extends AppCompatActivity {

    private static Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        if(!sharedPref.contains("key")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("key", new BigInteger(getRandomNumber(64)).toString());
            editor.commit();
        }
        if(!sharedPref.contains("ips")){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("ips", "");
            editor.commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_computer_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PairingActivity.class);
                startActivity(intent);
            }
        });


        /*
            First time : Create random key

            Check for devices, if key is successful, display.
            If not, display pairing



        */

    }

    public static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }

}
