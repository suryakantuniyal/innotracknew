package com.innobins.innotrack.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.innobins.innotrack.activity.VehicleDetailActivity;
import com.innobins.innotrack.model.VehicleList;

import com.innobins.innotrack.R;
import com.innobins.innotrack.activity.TrackingDevicesActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silence12 on 15/7/17.
 */

public class OnLineAdapter extends RecyclerView.Adapter<OnLineAdapter.MyViewHolder> implements Filterable {

    OnLineAdapter.OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private List<VehicleList> vehicleLists = new ArrayList<>();
    private List<VehicleList> mFilteredList;
    private OnLineAdapter.OnItemClickListener cnoteClick;


    public OnLineAdapter(Context mContext, ArrayList<VehicleList> mVehicleList, OnLineAdapter.OnItemClickListener cnoteClick) {
        this.mContext = mContext;
        this.vehicleLists = mVehicleList;
        mFilteredList = mVehicleList;
        this.cnoteClick = cnoteClick;
    }


    @Override
    public OnLineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_listview, parent, false);
        OnLineAdapter.MyViewHolder vh = new OnLineAdapter.MyViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(OnLineAdapter.MyViewHolder holder, int position) {

        VehicleList vehicle = mFilteredList.get(position);
        holder.name_tv.setText(vehicle.getName());
        holder.lastupdated_tv.setText(vehicle.getLastUpdates());
        holder.timediff_tv.setText(vehicle.getTimeDiff());
        if (vehicle.address.equals("null")) {
            holder.positionId_tv.setText("Loading...");
        } else {
            holder.positionId_tv.setText(vehicle.address);
        }
        if (vehicle.status.equals("online")) {

            holder.online_tv.setText("Online");
            Glide.with(mContext).load(R.drawable.online_icon).asBitmap()
                    .centerCrop().placeholder(R.drawable.placeholderxx4).into(holder.status_iv);
        } else {
            holder.online_tv.setText("Offline");
            Glide.with(mContext).load(R.drawable.offline_icon).asBitmap()
                    .centerCrop().placeholder(R.drawable.placeholderxx4).into(holder.status_iv);
        }

    }

    public void setOnItemClickListener(final OnLineAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    mFilteredList = vehicleLists;
                } else {
                    List<VehicleList> filteredList = new ArrayList<>();
                    for (VehicleList projectsModel : vehicleLists) {
                        if (projectsModel.getName().contains(charString) || projectsModel.getStatus().contains(charString)) {
                            filteredList.add(projectsModel);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (List<VehicleList>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener {
        public void OnItemClick(View view, int position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        private TextView name_tv, positionId_tv, lastupdated_tv, timediff_tv, online_tv;
        private LinearLayout track_ll, detail_ll;
        private ImageView vehivle_iv, status_iv;
        private LinearLayout detail, track;
        private View v;

        public MyViewHolder(final View itemView) {
            super(itemView);
            v = itemView;
            track = (LinearLayout) itemView.findViewById(R.id.track_ll);
            detail = (LinearLayout) itemView.findViewById(R.id.detail_ll);
            name_tv = (TextView) itemView.findViewById(R.id.vehicle_name);
            lastupdated_tv = (TextView) itemView.findViewById(R.id.lastupdate_tv);
            status_iv = (ImageView) itemView.findViewById(R.id.status_iv);
            positionId_tv = (TextView) itemView.findViewById(R.id.address_tv);
            timediff_tv = (TextView) itemView.findViewById(R.id.timediff_tv);
            online_tv = (TextView) itemView.findViewById(R.id.onlinet_tv);
            track.setOnClickListener(this);
            detail.setOnClickListener(this);

            track.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    if (view.getId() == R.id.detail_ll) {
                        Intent intent = new Intent(context, VehicleDetailActivity.class);
                        intent.putExtra("id", mFilteredList.get(getPosition()).getId());
                        intent.putExtra("name", mFilteredList.get(getPosition()).getName());
                        intent.putExtra("pid", mFilteredList.get(getPosition()).getPositionId());
                        intent.putExtra("uid", mFilteredList.get(getPosition()).getUniqueId());
                        intent.putExtra("status", mFilteredList.get(getPosition()).getStatus());
                        intent.putExtra("category", mFilteredList.get(getPosition()).getCategory());
                        intent.putExtra("lastupdate", mFilteredList.get(getPosition()).getLastUpdates());
                        intent.putExtra("diff", mFilteredList.get(getPosition()).getTimeDiff());
                        intent.putExtra("speed",mFilteredList.get(getPosition()).getSpeed());
                        intent.putExtra("address",mFilteredList.get(getPosition()).getAddress());
                        intent.putExtra("lat",mFilteredList.get(getPosition()).getLatitute());
                        intent.putExtra("long",mFilteredList.get(getPosition()).getLongitute());
                        intent.putExtra("distance",mFilteredList.get(getPosition()).getDistance_travelled());
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent);


                    } else if (view.getId() == R.id.track_ll) {


                    }

                }
            });


            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = itemView.getContext();
                    Intent trackIntent = new Intent(context, TrackingDevicesActivity.class);
                    trackIntent.putExtra("device_id", mFilteredList.get(getPosition()).getPositionId());
                    trackIntent.putExtra("tname", mFilteredList.get(getPosition()).getName());
                    trackIntent.putExtra("tupdate", mFilteredList.get(getPosition()).getLastUpdates());
                    trackIntent.putExtra("status", mFilteredList.get(getPosition()).getStatus());
                    trackIntent.putExtra("ttimer", mFilteredList.get(getPosition()).getTime());
                    trackIntent.putExtra("address",mFilteredList.get(getPosition()).getAddress());
                    trackIntent.putExtra("speed",mFilteredList.get(getPosition()).getSpeed());
                    trackIntent.putExtra("lat",mFilteredList.get(getPosition()).getLatitute());
                    trackIntent.putExtra("long",mFilteredList.get(getPosition()).getLongitute());
                    trackIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    context.startActivity(trackIntent);
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.OnItemClick(v, getAdapterPosition());

            } else {
                Log.d("itemclick", "OnItemClickListener is null");
            }
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            detail.requestFocus();
        }
    }
}
