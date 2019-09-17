package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jiobuddytw.jiobuddytwmerchant.Adapter.ScanHistoryAdapter;
import com.jiobuddytw.jiobuddytwmerchant.Class.ScanHistoryClass;
import com.jiobuddytw.jiobuddytwmerchant.Common.PreferenceManagerLogin;
import com.jiobuddytw.jiobuddytwmerchant.Connection.UrlLink;
import com.jiobuddytw.jiobuddytwmerchant.R;
import com.jiobuddytw.jiobuddytwmerchant.StandardProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AfterScanActivity extends AppCompatActivity {

    ImageView imageView_back;

    RecyclerView recyclerView;

    Button button_addmore,button_submit;

    PreferenceManagerLogin session;

    StandardProgressDialog standardProgressDialog;

    String transaction_id,confirm_code,user_id,parent_id,merchant_id,unique_id;

    private ScanHistoryAdapter adapter;

    List<ScanHistoryClass> scanHistoryClasses;

    int totaldatascan = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);

        session = new PreferenceManagerLogin(getApplicationContext());
        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(PreferenceManagerLogin.USER_ID);
        parent_id = user.get(PreferenceManagerLogin.PARENT_ID);
        merchant_id = user.get(PreferenceManagerLogin.MERCHANT_ID);

        transaction_id = getIntent().getStringExtra("transaction_id");
        confirm_code = getIntent().getStringExtra("confirm_code");
        unique_id = getIntent().getStringExtra("unique_id");

        button_addmore = findViewById(R.id.button_addmore);
        button_submit = findViewById(R.id.button_submit);
        recyclerView = findViewById(R.id.recyclerView);
        imageView_back = findViewById(R.id.imageView_back);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                startActivity(next);
            }
        });

        button_addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOption();
            }
        });


        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        getData();
    }

    private void submit() {
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                standardProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.submitTransaction(merchant_id,transaction_id,unique_id,confirm_code);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                standardProgressDialog.dismiss();

                try {
                    if(jsonObject.getString("code").equals("1")){
                       getListing();
                    }else {
                        AlertDialog alertDialogs = new AlertDialog.Builder(AfterScanActivity.this, R.style.AlertDialogTheme)
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

    private void getListing() {

        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                standardProgressDialog.show();
                standardProgressDialog.setCancelable(false);
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.getTransactionHistory(user_id,parent_id,1,0);
            }

            @Override
            protected void onPostExecute(final JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                try {
                    JSONArray listing = new JSONArray(jsonObject.getString("listing"));
                    for (int i =0; i < listing.length(); i++){
                        JSONObject obj = listing.getJSONObject(i);

                        AlertDialog alertDialogs = new AlertDialog.Builder(AfterScanActivity.this, R.style.AlertDialogTheme)
                                .setTitle("已完成！交易摘要")
                                .setMessage("日期/時間\t:\t"+obj.getString("datetime")+"\n" +
                                        "M匯通用戶ID\t:\t"+obj.getString("mmspotid")+"\n" +
                                        "交易ID\t:\t"+obj.getString("transaction_id")+"\n" +
                                        "FCV收到了\t:\t"+totaldatascan+" 張數\n" +
                                        "現金餐飲券（FCV）總額\t:\t"+obj.getString("total_cash"))
                                .setCancelable(false)
                                .setPositiveButton("返回", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
                                        startActivity(next);
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

    private void getData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(AfterScanActivity.this));
        scanHistoryClasses = new ArrayList<>();
        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                standardProgressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.getScanData(user_id,parent_id,transaction_id);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                standardProgressDialog.dismiss();
                try {
                    if(jsonObject.getString("code").equals("1")){
                        JSONArray listingArr = new JSONArray(jsonObject.getString("listing"));
                        for (int i =0; i < listingArr.length(); i++){
                            JSONObject listingOBJ = listingArr.getJSONObject(i);
                            scanHistoryClasses.add(new ScanHistoryClass(
                                    String.valueOf(i+1),
                                    listingOBJ.getString("code"),
                                    listingOBJ.getString("denom")
                            ));
                            totaldatascan++;
                        }
                        adapter = new ScanHistoryAdapter(getApplicationContext(), scanHistoryClasses);
                        recyclerView.setAdapter(adapter);
                    }else{
                        AlertDialog alertDialogs = new AlertDialog.Builder(AfterScanActivity.this, R.style.AlertDialogTheme)
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

    public void dialogOption(){
        final Dialog dialog = new Dialog(AfterScanActivity.this);
        dialog.setContentView(R.layout.dialog_choose_camera_qrcode);

        Button button_scan = dialog.findViewById(R.id.button_scan);
        Button button_enter = dialog.findViewById(R.id.button_enter);

        button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),EnterCodeTransactionActivity.class);
                next.putExtra("reload","reload"); //for second time scan
                next.putExtra("confirm_code",confirm_code);
                next.putExtra("transaction_id",transaction_id);
                startActivity(next);
                dialog.dismiss();
            }
        });

        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),ScanQrCodeActivity.class);
                next.putExtra("reload","reload"); //for second time scan
                next.putExtra("confirm_code",confirm_code);
                next.putExtra("transaction_id",transaction_id);
                startActivity(next);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent next = new Intent(getApplicationContext(),DashboardActivity.class);
        startActivity(next);
    }
}
