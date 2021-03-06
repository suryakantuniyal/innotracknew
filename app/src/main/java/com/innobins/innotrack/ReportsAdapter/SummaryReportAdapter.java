package com.innobins.innotrack.ReportsAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innobins.innotrack.R;
import com.innobins.innotrack.activity.Reports.ReportData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surya on 11/10/17.
 */

public class SummaryReportAdapter extends RecyclerView.Adapter<SummaryReportAdapter.ViewHolde>{
    private final Context context;
    private List<ReportData> reportDatas = new ArrayList<>();
    private List<ReportData> mReportdata;

    public SummaryReportAdapter(Context context,List<ReportData>mReportdata) {
        this.context = context;
        this.reportDatas = mReportdata;
        this.mReportdata = mReportdata;
    }

    @Override
    public ViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.summary_report,parent,false);
        SummaryReportAdapter.ViewHolde viewHolder = new SummaryReportAdapter.ViewHolde(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolde holder, int position) {

        ReportData reportData = mReportdata.get(position);

        holder.deviceName_tv.setText(reportData.getDeviceName());
        holder.distance_tv.setText(reportData.getDistance().toString());
        holder.averageSpeed_tv.setText(reportData.getAverageSpeed().toString());
        holder.maximumSpeed_tv.setText(reportData.getMaximumSpeed().toString());
        holder.enginHrs_tv.setText(String.valueOf(reportData.getEngineHours()));
        holder.spentFuel_tv.setText(reportData.getSpentFuel());
    }

    @Override
    public int getItemCount() {
        return mReportdata.size();
    }

    public class ViewHolde extends RecyclerView.ViewHolder {
        private TextView deviceName_tv,distance_tv,averageSpeed_tv,maximumSpeed_tv,enginHrs_tv,spentFuel_tv;
        public ViewHolde(View itemView) {
            super(itemView);
            deviceName_tv = (TextView)itemView.findViewById(R.id.summryDeviceName);
            distance_tv = (TextView)itemView.findViewById(R.id.summryDistance);
            averageSpeed_tv = (TextView)itemView.findViewById(R.id.summryAvrgSpeed);
            maximumSpeed_tv = (TextView)itemView.findViewById(R.id.summryMaxSpeed);
            enginHrs_tv = (TextView)itemView.findViewById(R.id.summryEngnHrs);
            spentFuel_tv = itemView.findViewById(R.id.summrySpentFuel);
        }
    }
}
