package com.maxchehab.remotelinuxunlocker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends AsyncTask<Void, Void, Void> {

    String host;
    int port;
    String message;

    Client(String host, int port,String message) {
        this.host = host;
        this.port = port;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        Socket socket = null;

        try {
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
            Log.d("async-client","connected to: " + host + ":" + port);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write(message);
            bw.flush();

            Log.d("async-client","sent message: " + message);

        } catch (IOException exception) {
            Log.d("async-client", "the server is offline?");
        }finally {
            Log.d("async-client","success");
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}