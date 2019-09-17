package com.jiobuddytw.jiobuddytwmerchant.Connection;

import org.json.JSONObject;

import java.util.HashMap;

public class UrlLink {
    private JSONParser2 jsonParser;

    public UrlLink() {
        jsonParser = new JSONParser2();
    }

    public static String url_path = "https://jiobuddy.tw/api/";

    public static String URL_LOGIN = url_path + "login.php";
    public static String URL_DASHBOARD = url_path + "dashboard.php";
    public static String URL_TRANSACTION_HISTORY = url_path + "listing.php";
    public static String URL_GENERATE_TRANSACTION_ID = url_path + "transaction.php";
    public static String URL_SCAN_CODE = url_path + "scan.php";
    public static String URL_SCAN_DATA = url_path + "fcvlist.php";
    public static String URL_SUBMIT = url_path + "success.php";


    public JSONObject login(String email, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        return jsonParser.makeHttpRequest(URL_LOGIN, "GET", params);
    }

    public JSONObject dashboard(String user_id, String parent_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("parent_id", parent_id);
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        return jsonParser.makeHttpRequest(URL_DASHBOARD, "GET", params);
    }

    public JSONObject getTransactionHistory(String user_id, String parent_id,int show,int page) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("parent_id", parent_id);
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        params.put("page",  String.valueOf(page));
        params.put("show", String.valueOf(show));
        return jsonParser.makeHttpRequest(URL_TRANSACTION_HISTORY, "GET", params);
    }

    public JSONObject generateTransactionID() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        return jsonParser.makeHttpRequest(URL_GENERATE_TRANSACTION_ID, "GET", params);
    }

    public JSONObject scanCode(String user_id, String parent_id,String verify_code,String confirm_code,String transaction_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("parent_id", parent_id);
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        params.put("verify_code", verify_code);
        params.put("confirm_code",confirm_code);
        params.put("transaction_id",transaction_id);
        return jsonParser.makeHttpRequest(URL_SCAN_CODE, "GET", params);
    }

    public JSONObject getScanData(String user_id, String parent_id,String transaction_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("parent_id", parent_id);
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        params.put("transaction_id",transaction_id);
        return jsonParser.makeHttpRequest(URL_SCAN_DATA, "GET", params);
    }

    public JSONObject submitTransaction(String merchant_id, String transaction_id,String cust_unique_id,String confirm_code) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", "JIO.a10e95d81d0f3a6553cea7b0f1bbd201");
        params.put("merchant_id",merchant_id);
        params.put("transaction_id",transaction_id);
        params.put("cust_unique_id",cust_unique_id);
        params.put("confirm_code",confirm_code);
        return jsonParser.makeHttpRequest(URL_SUBMIT, "GET", params);
    }


}
