package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiobuddytw.jiobuddytwmerchant.Common.PreferenceManagerLogin;
import com.jiobuddytw.jiobuddytwmerchant.Connection.UrlLink;
import com.jiobuddytw.jiobuddytwmerchant.R;
import com.jiobuddytw.jiobuddytwmerchant.StandardProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static long back_pressed;
    EditText editText_email,editText_password;
    Button button_login;
    StandardProgressDialog standardProgressDialog;
    PreferenceManagerLogin session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new PreferenceManagerLogin(getApplicationContext());
        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        button_login = findViewById(R.id.button_login);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_email.getText().toString().equals("")){
                    editText_email.setError("Please fill the value");
                }else if(editText_password.getText().toString().equals("")){
                    editText_password.setError("Please fill the value");
                }else{
                    login();
                }
            }
        });
    }

    private void login() {
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                standardProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                String token =  "123"; //hardcode dulu nnti buat
                UrlLink uLink = new UrlLink();
                return uLink.login(editText_email.getText().toString(), editText_password.getText().toString());
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                Log.d("jsonobject",jsonObject.toString());
                standardProgressDialog.dismiss();
                try {
                    if(jsonObject.getString("code").equals("1")){
                        session.createLoginSession(
                                jsonObject.getString("userid"),
                                jsonObject.getString("merchant_id"),
                                jsonObject.getString("priv"),
                                jsonObject.getString("shop_name"),
                                jsonObject.getString("parent_id")
                        );
                        Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                        startActivity(next);
                    }else{
                        AlertDialog alertDialogs = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme)
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

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis())  moveTaskToBack(true);
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
