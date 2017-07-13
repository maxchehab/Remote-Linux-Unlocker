package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Max on 5/10/2017.
 */

public class ComputerLayout extends CardView{

    public ComputerLayout(Context context, String ip, String key) {
        super(context);
        init(ip,key,null);
    }

    public ComputerLayout(Context context, String ip, String key, String command) {
        super(context);
        init(ip,key, command);
    }

    private TextView hostname;
    private Button lockButton;

    private boolean locked = false;

    private void init(final String ip, final String key, String command) {

        inflate(getContext(), R.layout.computer_layout, this);
        hostname = (TextView) findViewById(R.id.hostname);
        lockButton = (Button) findViewById(R.id.lockButton);

        status(ip, key);

        lockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockButton.setEnabled(false);
                lockButton.setClickable(false);
                if(locked){
                    lock(ip,key,"unlock");
                }else{
                    lock(ip, key,"lock");
                }
            }
        });

        if(command != null){
            lock(ip,key,command);
        }
    }

    private void lock(String ip, String key, String action){
        try {
            new Client(ip,61599,"{\"command\":\"" + action + "\",\"key\":\"" +  key + "\"}").execute().get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        boolean tempLocked = locked;
        while(tempLocked == locked){
            status(ip,key);
        }
        lockButton.setClickable(true);
        lockButton.setEnabled(true);
    }


    private void status(String ip, String key){
        String echoResponse = null;
        try {
            echoResponse = new Client(ip,61599,"{\"command\":\"status\",\"key\":\"" +  key + "\"}").execute().get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("status-response","Response: " + echoResponse);

        if(echoResponse == null){
            this.setVisibility(View.GONE);
        }else{
            this.setVisibility(View.VISIBLE);
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(echoResponse);
            JsonObject rootobj = root.getAsJsonObject();
            Log.d("hostname",rootobj.get("hostname").getAsString());
            hostname.setText(rootobj.get("hostname").getAsString());
            locked = rootobj.get("isLocked").getAsBoolean();

            if(locked){
                lockButton.setText("Unlock");
            }else{
                lockButton.setText("Lock");
            }
        }
    }

}
