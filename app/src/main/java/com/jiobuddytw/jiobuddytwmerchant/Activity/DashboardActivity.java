package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiobuddytw.jiobuddytwmerchant.Adapter.TransactionHistoryAdapter;
import com.jiobuddytw.jiobuddytwmerchant.Common.PreferenceManagerLogin;
import com.jiobuddytw.jiobuddytwmerchant.Connection.UrlLink;
import com.jiobuddytw.jiobuddytwmerchant.R;
import com.jiobuddytw.jiobuddytwmerchant.StandardProgressDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity implements AbsListView.OnScrollListener{

    PreferenceManagerLogin session;

    private static long back_pressed;

    private SlidingUpPanelLayout slidingLayout;

    LinearLayout hideLayout;

    Boolean statusDown = false;

    TextView textView_title,textView_merchant_name,textView_merchant_id,unused_fcv,unused_amount,used_fcv,used_amount,
            sale_pack,qty_per_month,balance;

    StandardProgressDialog standardProgressDialog;

    String user_id,parent_id,merchant_name,merchant_id;

    ListView listView;

    TransactionHistoryAdapter adapter;

    private ArrayList<Map<String, String>> data = null;

    private int currentPagination = 1;

    private int pageniationremain = 1;
    private int totaltotal = 0;

    private int total = 100;

    boolean reloadToBottom = false;

    Button button_transaction;

    ImageView imageView_back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        session = new PreferenceManagerLogin(getApplicationContext());
        standardProgressDialog = new StandardProgressDialog(this.getWindow().getContext());

        if(session.checkLogin()){
            finish();
        }else{

            HashMap<String, String> user = session.getUserDetails();
            user_id = user.get(PreferenceManagerLogin.USER_ID);
            parent_id = user.get(PreferenceManagerLogin.PARENT_ID);
            merchant_name = user.get(PreferenceManagerLogin.SHOP_NAME);
            merchant_id = user.get(PreferenceManagerLogin.MERCHANT_ID);


            slidingLayout = findViewById(R.id.sliding_layout);
            hideLayout = findViewById(R.id.hideLayout);

            textView_title = findViewById(R.id.textView_title);
            textView_merchant_name = findViewById(R.id.textView_merchant_name);
            textView_merchant_id = findViewById(R.id.textView_merchant_id);
            unused_fcv = findViewById(R.id.unused_fcv);
            unused_amount = findViewById(R.id.unused_amount);
            used_fcv = findViewById(R.id.used_fcv);
            used_amount = findViewById(R.id.used_amount);
            sale_pack = findViewById(R.id.sale_pack);
            qty_per_month = findViewById(R.id.qty_per_month);
            balance = findViewById(R.id.balance);
            button_transaction = findViewById(R.id.button_transaction);
            listView = findViewById(R.id.listView);
            imageView_back_button = findViewById(R.id.imageView_back_button);
            slidingLayout.setPanelSlideListener(onSlideListener());


            imageView_back_button.setVisibility(View.GONE);
            button_transaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogOption();
                }
            });
            getDashboad();
        }


    }

    public void dialogOption(){
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.dialog_choose_camera_qrcode);

        Button button_scan = dialog.findViewById(R.id.button_scan);
        Button button_enter = dialog.findViewById(R.id.button_enter);

        button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),EnterCodeTransactionActivity.class);
                startActivity(next);
                dialog.dismiss();
            }
        });

        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),ScanQrCodeActivity.class);
                startActivity(next);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount >0 && listView != null) {
            int lastPosition = listView.getLastVisiblePosition();
            if (lastPosition + 1 == data.size() && data.size() < total) {

                listView.smoothScrollBy(0, 0);
                currentPagination++;
               getTransactionHistory();

            }
        }
    }

    private void getDashboad(){
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
                return uLink.dashboard(user_id,parent_id);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    if(jsonObject.getString("code").equals("1")){
                        sale_pack.setText(jsonObject.getString("sale_pack"));
                        qty_per_month.setText(jsonObject.getString("qty_per_month"));
                        balance.setText(jsonObject.getString("balance"));
                        textView_merchant_name.setText(merchant_name);
                        textView_merchant_id.setText(merchant_id);

                        JSONObject fcv = new JSONObject(jsonObject.getString("fcv"));

                        unused_fcv.setText(fcv.getString("unused_fcv"));
                        unused_amount.setText(fcv.getString("unused_amount"));
                        used_fcv.setText(fcv.getString("used_fcv"));
                        used_amount.setText(fcv.getString("used_amount"));


                        getTransactionHistoryLength();
                    }else{
                        AlertDialog alertDialogs = new AlertDialog.Builder(DashboardActivity.this, R.style.AlertDialogTheme)
                                .setMessage(jsonObject.getString("message"))
                                .setCancelable(false)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent next = new Intent(getApplicationContext(),LoginActivity.class);
                                        startActivity(next);
                                        session.logoutUser();
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

    private void getTransactionHistoryLength() {
        data = new ArrayList<Map<String, String>>(); // data
        adapter = new TransactionHistoryAdapter(getApplicationContext(), data);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);

        class AsyncTaskRunner extends AsyncTask<String, String, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... params) {
                UrlLink uLink = new UrlLink();
                return uLink.getTransactionHistory(user_id,parent_id,100,0);
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                standardProgressDialog.dismiss();
                try {
                    if(jsonObject.getString("code").equals("1")){
                        JSONArray listing = new JSONArray(jsonObject.getString("listing"));
                        total = listing.length();
                    }
                    getTransactionHistory();
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

    private void getTransactionHistory() {

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
                return uLink.getTransactionHistory(user_id,parent_id,10,currentPagination);
            }

            @Override
            protected void onPostExecute(final JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                Log.d("PAGINATION", String.valueOf(currentPagination));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(jsonObject == null){
                            standardProgressDialog.dismiss();
                        }else{
                            try {
                                if(jsonObject.getString("code").equals("1")){
                                    JSONArray listing = new JSONArray(jsonObject.getString("listing"));
                                    for (int i =0; i < listing.length(); i++){
                                        JSONObject listingOBJ = listing.getJSONObject(i);
                                        Map<String, String> addData = new HashMap<String, String>();
                                        addData.put("datetime", listingOBJ.getString("datetime"));
                                        addData.put("mmspotid", listingOBJ.getString("mmspotid"));
                                        addData.put("total_cash", listingOBJ.getString("total_cash"));
                                        addData.put("mdr_amount", listingOBJ.getString("mdr_amount"));
                                        addData.put("transaction_id", listingOBJ.getString("transaction_id"));
                                        addData.put("submerchant_id", listingOBJ.getString("submerchant_id"));
                                        addData.put("fcv_status", listingOBJ.getString("fcv_status"));
                                        addData.put("settlement_status", listingOBJ.getString("settlement_status"));
                                        addData.put("remarks", listingOBJ.getString("remarks"));
                                        data.add(addData);
                                    }
                                    totaltotal = totaltotal + listing.length();
                                    standardProgressDialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, 1500);



            }
            @Override
            protected void onCancelled() {
                super.onCancelled();
                standardProgressDialog.dismiss();
            }
        }
        new AsyncTaskRunner().execute();
    }

    private View.OnClickListener onShowListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };
    }

    private View.OnClickListener onHideListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        };
    }

    private SlidingUpPanelLayout.PanelSlideListener onSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                hideLayout.setVisibility(View.GONE);
                statusDown = true;
                textView_title.setText("TRANSACTION HISOTRY");
                imageView_back_button.setVisibility(View.VISIBLE);
                imageView_back_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                });
            }

            @Override
            public void onPanelCollapsed(View view) {
                hideLayout.setVisibility(View.VISIBLE);
                statusDown = false;
                textView_title.setText("MY ACCOUNT");
                imageView_back_button.setVisibility(View.GONE);
            }

            @Override
            public void onPanelExpanded(View view) {
                hideLayout.setVisibility(View.GONE);
                statusDown = true;
                textView_title.setText("TRANSACTION HISOTRY");
                imageView_back_button.setVisibility(View.VISIBLE);
                imageView_back_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                });
            }

            @Override
            public void onPanelAnchored(View view) {
            }

            @Override
            public void onPanelHidden(View view) {
            }
        };
    }

    @Override
    public void onBackPressed() {

        if(statusDown == true){
            statusDown = false;
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            textView_title.setText("MY ACCOUNT");
        }else {
            if (back_pressed + 2000 > System.currentTimeMillis())  moveTaskToBack(true);
            else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }
}
