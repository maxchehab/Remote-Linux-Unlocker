package com.maxchehab.remotelinuxunlocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
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
        init(ip,key);
    }

    private TextView hostname;
    private TextView lockText;
    private Switch lockSwitch;
    private boolean locked = false;

    private void init(final String ip, final String key) {

        inflate(getContext(), R.layout.computer_layout, this);
        hostname = (TextView) findViewById(R.id.hostname);
        lockText = (TextView) findViewById(R.id.lockText);
        lockSwitch = (Switch) findViewById(R.id.lockSwitch);



        echo(ip, key);


    }

    private void echo(String ip, String key){
        String echoResponse = null;
        try {
            echoResponse = new Client(ip,61599,"{\"command\":\"echo\",\"key\":\"" +  key + "\"}").execute().get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Log.d("echo-response","Response: " + echoResponse);

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
                lockText.setText("Unlock");
            }else{
                lockText.setText("Lock");
            }
            lockSwitch.setChecked(locked);
        }

    }

}
