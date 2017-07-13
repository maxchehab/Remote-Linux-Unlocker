package com.maxchehab.remotelinuxunlocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UnlockComputer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_computer);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_computer);
        Intent intent = new Intent(getBaseContext(), ComputerListActivity.class);
        intent.putExtra("command", "unlock");
        startActivity(intent);
        finish();
    }
}
