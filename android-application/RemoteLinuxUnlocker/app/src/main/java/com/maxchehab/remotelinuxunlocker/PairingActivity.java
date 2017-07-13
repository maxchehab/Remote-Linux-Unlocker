package com.maxchehab.remotelinuxunlocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PairingActivity extends AppCompatActivity {

    private EditText ipInput;
    private Button pairButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        ipInput = (EditText)findViewById(R.id.ipInput);
        pairButton = (Button)findViewById(R.id.pairButton);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ipInput.setFilters(filters);

        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pair();
            }
        });
    }

    void pair(){
        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        String key = sharedPref.getString("key",null);
        String ipString = sharedPref.getString("ips",null);
        List<String> ips = new ArrayList<String>();
        if(ipString.length() > 0){
            ips = Arrays.asList(sharedPref.getString("ips",null).split(","));
            ips = new ArrayList<String>(ips);
        }
        if(ips.contains(ipInput.getText().toString())){
            ipInput.setError("IP address is already paired.");
            return;
        }

        try {
            String response = new Client(ipInput.getText().toString(), 61598, "{\"command\":\"pair\",\"key\":\"" +  key + "\"}").execute().get(1, TimeUnit.SECONDS);
            Log.d("UI RESPONSE", "response: " + response);
            if(response == null){
                pairFailed();
            }else{
                pairSuccess();
            }
        } catch (InterruptedException e) {
            pairFailed();
            e.printStackTrace();
        } catch (ExecutionException e) {
            pairFailed();
            e.printStackTrace();
        }catch (TimeoutException e) {
            pairFailed();
            e.printStackTrace();
        }
    }

    void pairFailed(){
        ipInput.setError("IP address is invalid. Make sure your computer is in pairing mode.");
    }

    void pairSuccess(){
        ipInput.setError(null);

        SharedPreferences sharedPref = getSharedPreferences("data", MODE_PRIVATE);
        List<String> ips = Arrays.asList(sharedPref.getString("ips",null).split(","));
        ips = new ArrayList<String>(ips);

        ips.add(ipInput.getText().toString());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ips", android.text.TextUtils.join(",", ips));
        editor.commit();
        finish();
    }

}
