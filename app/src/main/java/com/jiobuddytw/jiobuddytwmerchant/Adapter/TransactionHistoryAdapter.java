package com.jiobuddytw.jiobuddytwmerchant.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiobuddytw.jiobuddytwmerchant.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TransactionHistoryAdapter extends BaseAdapter {

    private ArrayList<Map<String, String>> data = null;
    private Context context = null;
    private LayoutInflater inflater;
    boolean statusUp = false;

    private class ViewHolder {
        TextView datetime,mmspotid,total_cash,mdr_amount,transaction_id,submerchant_id,fcv_status,settlement_status,remarks;
        LinearLayout linear_1,linear_2,linear_3,linear_4,linear_5,linear_6,linear_7,linear_8,linear_9;
        ImageView imageView_up,imageView_down;
    }

    public TransactionHistoryAdapter(Context context, ArrayList<Map<String, String>> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return (data == null) ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_transaction_history_adapter_layout, null);

            holder = new ViewHolder();

            holder.datetime = view.findViewById(R.id.datetime);
            holder.mmspotid = view.findViewById(R.id.mmspotid);
            holder.total_cash = view.findViewById(R.id.total_cash);
            holder.mdr_amount = view.findViewById(R.id.mdr_amount);
            holder.transaction_id = view.findViewById(R.id.transaction_id);
            holder.submerchant_id = view.findViewById(R.id.submerchant_id);
            holder.fcv_status = view.findViewById(R.id.fcv_status);
            holder.settlement_status = view.findViewById(R.id.settlement_status);
            holder.remarks = view.findViewById(R.id.remarks);

            holder.linear_1 = view.findViewById(R.id.linear_1);
            holder.linear_2 = view.findViewById(R.id.linear_2);
            holder.linear_3 = view.findViewById(R.id.linear_3);
            holder.linear_4 = view.findViewById(R.id.linear_4);
            holder.linear_5 = view.findViewById(R.id.linear_5);
            holder.linear_6 = view.findViewById(R.id.linear_6);
            holder.linear_7 = view.findViewById(R.id.linear_7);
            holder.linear_8 = view.findViewById(R.id.linear_8);
            holder.linear_9 = view.findViewById(R.id.linear_9);
            holder.imageView_up = view.findViewById(R.id.imageView_up);
            holder.imageView_down = view.findViewById(R.id.imageView_down);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.datetime.setText(data.get(position).get("datetime"));
        holder.mmspotid.setText(data.get(position).get("mmspotid"));
        holder.total_cash.setText(data.get(position).get("total_cash"));
        holder.mdr_amount.setText(data.get(position).get("mdr_amount"));
        holder.transaction_id.setText(data.get(position).get("transaction_id"));
        holder.submerchant_id.setText(data.get(position).get("submerchant_id"));
        holder.fcv_status.setText(data.get(position).get("fcv_status"));
        holder.settlement_status.setText(data.get(position).get("settlement_status"));
        holder.remarks.setText(data.get(position).get("remarks"));

        holder.linear_2.setVisibility(View.GONE);
        holder.linear_3.setVisibility(View.GONE);
        holder.linear_4.setVisibility(View.GONE);
        holder.linear_5.setVisibility(View.GONE);
        holder.linear_6.setVisibility(View.GONE);
        holder.linear_7.setVisibility(View.GONE);
        holder.linear_8.setVisibility(View.GONE);
        holder.linear_9.setVisibility(View.GONE);
        holder.imageView_up.setVisibility(View.GONE);
        holder.imageView_down.setVisibility(View.VISIBLE);

        holder.imageView_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.linear_2.setVisibility(View.VISIBLE);
                holder.linear_3.setVisibility(View.VISIBLE);
                holder.linear_4.setVisibility(View.VISIBLE);
                holder.linear_5.setVisibility(View.VISIBLE);
                holder.linear_6.setVisibility(View.VISIBLE);
                holder.linear_7.setVisibility(View.VISIBLE);
                holder.linear_8.setVisibility(View.VISIBLE);
                holder.linear_9.setVisibility(View.VISIBLE);
                holder.imageView_up.setVisibility(View.VISIBLE);
                holder.imageView_down.setVisibility(View.GONE);
            }
        });

        holder.imageView_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.linear_2.setVisibility(View.GONE);
                holder.linear_3.setVisibility(View.GONE);
                holder.linear_4.setVisibility(View.GONE);
                holder.linear_5.setVisibility(View.GONE);
                holder.linear_6.setVisibility(View.GONE);
                holder.linear_7.setVisibility(View.GONE);
                holder.linear_8.setVisibility(View.GONE);
                holder.linear_9.setVisibility(View.GONE);
                holder.imageView_up.setVisibility(View.GONE);
                holder.imageView_down.setVisibility(View.VISIBLE);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    public void refreshData(ArrayList<Map<String, String>> newData) {
        this.data = newData;
        this.notifyDataSetChanged();
    }

    public CharSequence createDate(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(d);
    }
}