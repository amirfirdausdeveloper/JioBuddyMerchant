package com.jiobuddytw.jiobuddytwmerchant.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiobuddytw.jiobuddytwmerchant.Network.NetworkStateReceiver;
import com.jiobuddytw.jiobuddytwmerchant.R;

public class SplashscreenActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private int network = 0;
    private AlertDialog alertDialog;
    private NetworkStateReceiver networkStateReceiver;
    Handler handler;
    Context mContext;
    int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            this.unregisterReceiver(networkStateReceiver);
        } catch (Exception e) {
        }
        handler.removeMessages(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mContext = getApplicationContext();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(next);
            }
        }, 3500);
    }

    @Override
    public void networkAvailable() {
        network = 1;
    }

    public void networkUnavailable() {
        handler.removeMessages(0);
        network = 0;
        alertDialog = new AlertDialog.Builder(SplashscreenActivity.this, R.style.AlertDialogTheme)
                .setMessage(getString(R.string.string_no_network_notification))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string_try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isOnline()) {
                            click++;
                            if(click == 1){
                                dialog.dismiss();
                                handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent next = new Intent(getApplicationContext(), DashboardActivity.class);
                                        startActivity(next);
                                    }
                                }, 3500);
                            }
                        } else {
                            networkUnavailable();
                            click = 0;
                        }
                    }
                })
                .setNegativeButton(getString(R.string.string_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
