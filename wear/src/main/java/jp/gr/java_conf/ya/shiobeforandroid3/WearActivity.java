package jp.gr.java_conf.ya.shiobeforandroid3; // Copyright (C) 2014 YA<ya.androidapp@gmail.com> All rights reserved.

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class WearActivity extends Activity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private GoogleApiClient client;
    private TextView textView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            log("onActivityResult");
            if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
                final String message = list2message(data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS));
                if (message.equals("")) {
                    sendDataTask("");
                } else {
                    sendDataTask(message);
                }
            }
        }catch (Exception e){
            log(e.getMessage());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            log("onCreate");

            setContentView(R.layout.activity_main);

            textView = (TextView) findViewById(R.id.textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    log("onClick");
                    displaySpeechRecognizer();
                }
            });

            final String message = intent2message(getIntent());
            if (message.equals("")) {
                log("message.equals(\"\")");
                displaySpeechRecognizer();
            } else {
                log("Take a note");
                sendDataTask(message);
            }
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    String intent2message(Intent intent) {
        try{
            if (intent != null) {
                String action = intent.getAction();
                if (Intent.ACTION_SEND.equals(action)) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        CharSequence ext = extras.getCharSequence(Intent.EXTRA_TEXT);
                        if (ext != null) {
                            if (!ext.equals("")) {
                                return ext.toString();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            log(e.getMessage());
        }
        return "";
    }

    String list2message(List<String> results) {
        try{
            if (results != null) {
                if (results.get(0) != null) {
                    if (!results.get(0).equals("")) {
                        return results.get(0);
                    }
                }
            }
        }catch (Exception e){
            log(e.getMessage());
        }
        return "";
    }

    private void init() {
        try{
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
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    private void sendDataTask(final String message) {
        try{
            log("sendDataTask");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    init();

                    sendData(message);

                    Intent intent = new Intent(WearActivity.this, ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.SUCCESS_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.sent_message));
                    startActivity(intent);

                    finish();
                }
            }).start();
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    private void sendData(String message) {
        try{
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
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    private void displaySpeechRecognizer() {
        try{
            log("displaySpeechRecognizer");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    private void log(String str) {
        Log.v("S4AW MainActivity", str);
    }

}
