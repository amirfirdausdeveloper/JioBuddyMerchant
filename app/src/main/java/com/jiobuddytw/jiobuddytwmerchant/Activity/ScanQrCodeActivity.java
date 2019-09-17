package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.jiobuddytw.jiobuddytwmerchant.Common.PreferenceManagerLogin;
import com.jiobuddytw.jiobuddytwmerchant.Connection.UrlLink;
import com.jiobuddytw.jiobuddytwmerchant.MainActivity;
import com.jiobuddytw.jiobuddytwmerchant.R;
import com.jiobuddytw.jiobuddytwmerchant.StandardProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    PreferenceManagerLogin session;

    StandardProgressDialog standardProgressDialog;

    String transaction_id,confirm_code,user_id,parent_id,unique_id;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        session = new PreferenceManagerLogin(getApplicationContext());
        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(PreferenceManagerLogin.USER_ID);
        parent_id = user.get(PreferenceManagerLogin.PARENT_ID);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result result) {


        if(getIntent().hasExtra("reload")){
            standardProgressDialog.show();
            transaction_id = getIntent().getStringExtra("transaction_id");
            confirm_code = getIntent().getStringExtra("confirm_code");
            unique_id = getIntent().getStringExtra("unique_id");
            validateCode(result.getText());
        }else{
            generateTransactionID(result.getText());
        }

    }

    private void generateTransactionID(final String text) {
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                standardProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.generateTransactionID();
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                Log.d("masuk","masuk");
                try {
                    if(jsonObject.getString("code").equals("1")){
                        transaction_id = jsonObject.getString("transaction_id");
                        confirm_code = jsonObject.getString("confirm_code");
                        validateCode(text);
                    }else{
                        standardProgressDialog.dismiss();
                        AlertDialog alertDialogs = new AlertDialog.Builder(ScanQrCodeActivity.this, R.style.AlertDialogTheme)
                                .setMessage(jsonObject.getString("message"))
                                .setCancelable(false)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
                standardProgressDialog.dismiss();
            }
        }
        new AsyncTaskRunner().execute();
    }

    private void validateCode(final String text) {
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.scanCode(user_id,parent_id,text,confirm_code,transaction_id);
            }

            @Override
            protected void onPostExecute(final JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                Log.d("masuk","validate");
                standardProgressDialog.dismiss();

                try {
                    if(jsonObject.getString("code").equals("1")){
                        JSONObject customer = new JSONObject(jsonObject.getString("customer"));
                        unique_id = customer.getString("unique_id");

                        if(jsonObject.getString("message").contains("invalid")){
                            if(getIntent().hasExtra("reload")){
                                AlertDialog alertDialogs = new AlertDialog.Builder(ScanQrCodeActivity.this, R.style.AlertDialogTheme)
                                        .setMessage(jsonObject.getString("message"))
                                        .setCancelable(false)
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Intent next = new Intent(getApplicationContext(),AfterScanActivity.class);
                                                next.putExtra("transaction_id",transaction_id);
                                                next.putExtra("confirm_code",confirm_code);
                                                next.putExtra("unique_id",unique_id);
                                                startActivity(next);
                                            }
                                        }).show();
                            }else{
                                AlertDialog alertDialogs = new AlertDialog.Builder(ScanQrCodeActivity.this, R.style.AlertDialogTheme)
                                        .setMessage(jsonObject.getString("message"))
                                        .setCancelable(false)
                                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                                                startActivity(next);
                                            }
                                        }).show();
                            }

                        }else {
                            Intent next = new Intent(getApplicationContext(),AfterScanActivity.class);
                            next.putExtra("transaction_id",transaction_id);
                            next.putExtra("confirm_code",confirm_code);
                            next.putExtra("unique_id",unique_id);
                            startActivity(next);
                        }
                    }else{
                        if(getIntent().hasExtra("reload")){
                            AlertDialog alertDialogs = new AlertDialog.Builder(ScanQrCodeActivity.this, R.style.AlertDialogTheme)
                                    .setMessage(jsonObject.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent next = new Intent(getApplicationContext(),AfterScanActivity.class);
                                            next.putExtra("transaction_id",transaction_id);
                                            next.putExtra("confirm_code",confirm_code);
                                            next.putExtra("unique_id",unique_id);
                                            startActivity(next);
                                        }
                                    }).show();
                        }else{
                            AlertDialog alertDialogs = new AlertDialog.Builder(ScanQrCodeActivity.this, R.style.AlertDialogTheme)
                                    .setMessage(jsonObject.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                                            startActivity(next);
                                        }
                                    }).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
                standardProgressDialog.dismiss();
            }
        }
        new AsyncTaskRunner().execute();
    }
}
