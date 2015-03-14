/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.pushTest;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import org.apache.http.util.EncodingUtils;

public class MainActivity extends Activity {

    WebView webView;
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "1098776457859";
    static Window mainWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainWindow = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바 제거
        webView = new WebView(this);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.WebView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setSaveFormData(false);

        final Context myApp = this;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(myApp)
                        .setTitle("AlertDialog")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            };
        });
        getRegId();
    }

    /*
 * 화면 회전시 화면이 초기화되버리는 문제 방지
 * 매니페스트 액티비티 속성에
 * android:configChanges="orientation|screenSize"
 * 추가 필요
 */
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    public void getRegId(){

        new AsyncTask<Void, Void, String>() {
            String msg = "";
            @Override
            protected String doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = regid;
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("asdf" , msg);

                TelephonyManager systemService = (TelephonyManager)getSystemService    (Context.TELEPHONY_SERVICE);
                String PhoneNumber = systemService.getLine1Number();
                PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
                PhoneNumber="0"+PhoneNumber;
                String postData = "regId="+msg+"&phoneNum="+PhoneNumber;
                webView.postUrl("https://simpletalks.herokuapp.com/mobile", EncodingUtils.getBytes(postData, "BASE64"));
            }
        }.execute(null, null, null);
    }
}