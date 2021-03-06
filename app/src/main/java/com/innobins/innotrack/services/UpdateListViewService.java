package com.innobins.innotrack.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.innobins.innotrack.activity.MapViewActivity;
import com.innobins.innotrack.activity.VehicleDetailActivity;
import com.innobins.innotrack.activity.MainActivity;
import com.innobins.innotrack.activity.OnLineOffLineActivity;
import com.innobins.innotrack.activity.TrackingDevicesActivity;
import com.innobins.innotrack.vehicleonmap.VehicleOnMap;

/**
 * Created by surya on 27/9/17.
 */

public class UpdateListViewService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate(){
       // Toast.makeText(getBaseContext(), "ONCREATEEEEEEEEEEEEEee..", Toast.LENGTH_SHORT).show();

    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){

       // Toast.makeText(getBaseContext(), "Service has been Started..", Toast.LENGTH_SHORT).show();
        MainActivity mainActivity = MainActivity.instance;
       // VehicleDetailActivity vehicleDetailActivity = VehicleDetailActivity.instance;
        VehicleDetailActivity vehicleDetailActivity = VehicleDetailActivity.vehicleDetailActivity;
        TrackingDevicesActivity trackingDevicesActivity = TrackingDevicesActivity.trackingDevicesActivity;
        MapViewActivity mapViewActivity = MapViewActivity.mapViewActivity;
        VehicleOnMap vehicleOnMap = VehicleOnMap.vehicleOnMap;
      //  mainActivity.uploadNewData();

        OnLineOffLineActivity onLineOffLineActivity = OnLineOffLineActivity.onLineInstance;

        if (mainActivity!=null){
          //  Toast.makeText(getBaseContext(), "NULL ACTIVITY MAIN..", Toast.LENGTH_SHORT).show();
            mainActivity.uploadNewData();
        }
        if (trackingDevicesActivity!=null){
            trackingDevicesActivity.uploadIndividualData();
        }

        if (onLineOffLineActivity!=null){

            onLineOffLineActivity.uploadNewData();
        }

        if(vehicleOnMap!=null){
            vehicleOnMap.uploadIndividualData();
        }
       /* if (vehicleDetailActivity!= null){
            vehicleDetailActivity.individualDetail();

        }*/
        if (vehicleDetailActivity!=null){
            vehicleDetailActivity.uploadIndividualData();
        }

        if (mapViewActivity!=null){
            mapViewActivity.reloadMap();
        }


        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy(){

        stopSelf();
        super.onDestroy();
       // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
