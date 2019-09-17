package com.jiobuddytw.jiobuddytwmerchant.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jiobuddytw.jiobuddytwmerchant.Activity.LoginActivity;

import java.util.HashMap;


public class PreferenceManagerLogin {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "JioBuddy";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String USER_ID = "USER_ID";
    public static final String MERCHANT_ID = "MERCHANT_ID";
    public static final String PRIV = "PRIV";
    public static final String SHOP_NAME = "SHOP_NAME";
    public static final String PARENT_ID = "PARENT_ID";


    public PreferenceManagerLogin(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userid,String merchantid, String priv, String shopname,String parentid){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(USER_ID, userid);
        editor.putString(MERCHANT_ID, merchantid);
        editor.putString(PRIV, priv);
        editor.putString(SHOP_NAME, shopname);
        editor.putString(PARENT_ID,parentid);
        // commit changes
        editor.commit();
    }
    public boolean checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(MERCHANT_ID, pref.getString(MERCHANT_ID, null));
        user.put(PRIV, pref.getString(PRIV, null));
        user.put(SHOP_NAME, pref.getString(SHOP_NAME, null));
        user.put(PARENT_ID, pref.getString(PARENT_ID, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}