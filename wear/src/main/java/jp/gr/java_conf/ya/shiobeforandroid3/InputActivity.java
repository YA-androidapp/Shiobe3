package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (C) 2014 YA<ya.androidapp@gmail.com> All rights reserved.

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class InputActivity extends Activity {

    private Button button;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private GoogleApiClient client;
    private SharedPreferences pref_wear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
        setContentView(R.layout.activity_input);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                button = (Button) stub.findViewById(R.id.button);
                editText1 = (EditText) stub.findViewById(R.id.editText1);
                editText2 = (EditText) stub.findViewById(R.id.editText2);
                editText3 = (EditText) stub.findViewById(R.id.editText3);

                pref_wear = getSharedPreferences("Twitter_setting_wear", MODE_PRIVATE);
                if(pref_wear!=null){
                    final String str1 = pref_wear.getString("str1", "");
                    final String str3 = pref_wear.getString("str3", "");
                    if(!str1.equals("")){
                        editText1.setText(str1);
                    }
                    if(!str3.equals("")){
                        editText3.setText(str3);
                    }
                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        log("onClick");

                        SharedPreferences.Editor editor = pref_wear.edit();
                        if(!editText1.getText().toString().equals("")){
                            editor.putString("str1", editText1.getText().toString());
                        }
                        if(!editText3.getText().toString().equals("")){
                            editor.putString("str3", editText3.getText().toString());
                        }
                        editor.commit();

                        final String message = tweetstr(editText1.getText().toString(),editText2.getText().toString(),editText3.getText().toString());
                        sendDataTask(message);
                    }
                });
            }
        });
    }

    private void init() {
        if (client == null) {
            log("init");
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            log("onConnected");
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            log("onConnectionSuspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            log("onConnectionFailed");
                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            client.connect();
        }
    }

    private void sendDataTask(final String message) {
        log("sendDataTask");
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();

                sendData(message);

                Intent intent = new Intent(InputActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                        getString(R.string.sent_message));
                startActivity(intent);

                finish();
            }
        }).start();
    }

    private void sendData(String message) {
        log("sendData");
        log("path: " + (message.equals("") ? "/notification" : "/updateStatus"));
        log("data: " + message);
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(client).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result =null;
            try {
                result = Wearable.MessageApi.sendMessage(
                        client,
                        node.getId(),
                        (message.equals("") ? "/notification" : "/updateStatus"),
                        message.getBytes())
                        .await();
            }catch(Exception e){
                log(e.getMessage());
            }
            if(result!=null) {
                if (result.getStatus().isSuccess()) {
                    log("done");
                } else {
                    log("failure");
                }
            } else {
                log("failure");
            }
        }
    }

    private void log(String str) {
            Log.v("S4A InputActivity", str);
    }

    private String tweetstr(String str1, String str2, String str3) {
        if (str1 == null) {
            str1 = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str3 == null) {
            str3 = "";
        }
        String tweetstr = str1;
        if (( str1.length() > 0 ) && ( str2.length() > 0 )) {
            tweetstr += " ";
        }
        tweetstr += str2;
        if (( str2.length() > 0 ) && ( str3.length() > 0 )) {
            tweetstr += " ";
        }
        tweetstr += str3;
        return tweetstr;
    }
}
