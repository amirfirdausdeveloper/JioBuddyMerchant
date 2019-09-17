package com.jiobuddytw.jiobuddytwmerchant.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiobuddytw.jiobuddytwmerchant.Class.ScanHistoryClass;
import com.jiobuddytw.jiobuddytwmerchant.R;

import java.util.List;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ProductViewHolder> {


    private Context mCtx;
    public static List<ScanHistoryClass> scanHistoryClassList;
    public ScanHistoryAdapter(Context mCtx, List<ScanHistoryClass> scanHistoryClassList) {
        this.mCtx = mCtx;
        this.scanHistoryClassList = scanHistoryClassList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_scan_history, null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ProductViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        final ScanHistoryClass scanHistoryClass = scanHistoryClassList.get(position);

        holder.textView_no.setText(scanHistoryClass.getNo());
        holder.textView_code.setText(scanHistoryClass.getCode());
        holder.textView_fcm.setText(scanHistoryClass.getDenom());


    }

    @Override
    public int getItemCount() {
        return scanHistoryClassList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{


        TextView textView_no,textView_code,textView_fcm;
        public ProductViewHolder(View itemView) {
            super(itemView);

            textView_no = itemView.findViewById(R.id.textView_no);
            textView_code = itemView.findViewById(R.id.textView_code);
            textView_fcm = itemView.findViewById(R.id.textView_fcm);


        }
    }



}
