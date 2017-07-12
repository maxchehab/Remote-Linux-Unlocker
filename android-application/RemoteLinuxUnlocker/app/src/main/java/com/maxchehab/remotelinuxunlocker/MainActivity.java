package com.maxchehab.remotelinuxunlocker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button lockButton;

    private boolean locked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lockButton = (Button)findViewById(R.id.lock);
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(locked){
                   new Client("10.0.1.29", 61599, "unlock").execute();
                   lockButton.setText("lock");
               }else{
                   new Client("10.0.1.29", 61599, "lock").execute();
                   lockButton.setText("unlock");
               }
               locked = !locked;

            }
        });

    }

}
