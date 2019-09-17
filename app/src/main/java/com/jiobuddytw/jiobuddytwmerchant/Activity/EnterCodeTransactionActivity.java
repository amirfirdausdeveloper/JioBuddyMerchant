package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jiobuddytw.jiobuddytwmerchant.Common.PreferenceManagerLogin;
import com.jiobuddytw.jiobuddytwmerchant.Connection.UrlLink;
import com.jiobuddytw.jiobuddytwmerchant.R;
import com.jiobuddytw.jiobuddytwmerchant.StandardProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EnterCodeTransactionActivity extends AppCompatActivity {

    PreferenceManagerLogin session;
    
    ImageView imageView_back;
    
    EditText editText_code;
    
    Button button_submint;
    
    StandardProgressDialog standardProgressDialog;
    
    String transaction_id,confirm_code,user_id,parent_id,unique_id;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code_transaction);

        session = new PreferenceManagerLogin(getApplicationContext());
        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(PreferenceManagerLogin.USER_ID);
        parent_id = user.get(PreferenceManagerLogin.PARENT_ID);

        imageView_back = findViewById(R.id.imageView_back);
        editText_code = findViewById(R.id.editText_code);
        button_submint = findViewById(R.id.button_submint);
        
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        button_submint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_code.getText().toString().equals("")){
                    editText_code.setError("Empty");
                }else{
                    if(getIntent().hasExtra("reload")){
                        standardProgressDialog.show();
                        transaction_id = getIntent().getStringExtra("transaction_id");
                        confirm_code = getIntent().getStringExtra("confirm_code");
                        unique_id = getIntent().getStringExtra("unique_id");
                        validateCode();
                    }else{
                        generateTransactionID();
                    }

                }
            }
        });
        
    }

    private void generateTransactionID() {
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
                
                try {
                    if(jsonObject.getString("code").equals("1")){
                        JSONObject customer = new JSONObject(jsonObject.getString("customer"));
                        transaction_id = jsonObject.getString("transaction_id");
                        confirm_code = jsonObject.getString("confirm_code");
                        unique_id = customer.getString("unique_id");
                        validateCode();
                    }else{
                        standardProgressDialog.dismiss();
                        AlertDialog alertDialogs = new AlertDialog.Builder(EnterCodeTransactionActivity.this, R.style.AlertDialogTheme)
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

    private void validateCode() {
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.scanCode(user_id,parent_id,editText_code.getText().toString(),confirm_code,transaction_id);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                standardProgressDialog.dismiss();

                try {
                    if(jsonObject.getString("code").equals("1")){

                        JSONObject customer = new JSONObject(jsonObject.getString("customer"));
                        unique_id = customer.getString("unique_id");

                        if(jsonObject.getString("message").contains("invalid")){
                            AlertDialog alertDialogs = new AlertDialog.Builder(EnterCodeTransactionActivity.this, R.style.AlertDialogTheme)
                                    .setMessage(jsonObject.getString("message"))
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }else {
                            Intent next = new Intent(getApplicationContext(),AfterScanActivity.class);
                            next.putExtra("transaction_id",transaction_id);
                            next.putExtra("confirm_code",confirm_code);
                            next.putExtra("unique_id",unique_id);
                            startActivity(next);
                        }
                    }else{
                        AlertDialog alertDialogs = new AlertDialog.Builder(EnterCodeTransactionActivity.this, R.style.AlertDialogTheme)
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
}
