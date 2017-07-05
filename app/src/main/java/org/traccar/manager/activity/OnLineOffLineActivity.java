package org.traccar.manager.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import org.traccar.manager.R;
import org.traccar.manager.adapter.VehicleslistAdapter;
import org.traccar.manager.model.VehicleList;
import java.util.ArrayList;

/**
 * Created by silence12 on 5/7/17.
 */

public class OnLineOffLineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, VehicleslistAdapter.OnItemClickListener {
    private static ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static MenuItem searchMenuItem;
    private VehicleslistAdapter vehiclesAdapter;
    private RecyclerView recyclerView;
    private ArrayList<VehicleList> listArrayList;
    private    String onnOff ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlineoffline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.luncher_icon);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait a moment...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Intent intent = getIntent();
        onnOff = intent.getStringExtra("onoff");
        if(onnOff.equals("online")){
            setTitle("Online Devices");
        }else if(onnOff.equals("offline")){
            setTitle("Offline Devices");
        }
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_online);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        new Async().execute();
        recyclerView = (RecyclerView) findViewById(R.id.onlineoffline_rv);
        listArrayList = new ArrayList<VehicleList>();
        Log.d("OnlineSizeaa", String.valueOf(listArrayList.size()));
        vehiclesAdapter = new VehicleslistAdapter(getBaseContext(), listArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(vehiclesAdapter);
        vehiclesAdapter.setOnItemClickListener(this);
    }


    public class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            parseView();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    private void parseView() {
        ArrayList<VehicleList> result = null;
        if(onnOff.equals("online")){
            result = MainActivity.onLineList;
        }else if(onnOff.equals("offline")){
           result = MainActivity.offlineList;
        }
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                for (int i = 0; i < result.size(); i++) {
                    final int finalI = i;
                    VehicleList vehicles = new VehicleList(result.get(finalI).id, result.get(finalI).name, result.get(finalI).uniqueId
                                    , result.get(finalI).status, result.get(finalI).lastUpdates, result.get(finalI).category, result.get(finalI).positionId, result.get(finalI).address,
                                    result.get(finalI).time, result.get(finalI).timeDiff);
                            listArrayList.add(vehicles);
                }

    }
    @Override
    public void onRefresh() {

        new Async().execute();

    }


    @Override
    public void OnItemClick(View view, int position) {
        if (view.getId() == R.id.detail_btn) {
            Intent intent = new Intent(OnLineOffLineActivity.this, VehicleDetailActivity.class);
            intent.putExtra("id", listArrayList.get(position).getId());
            intent.putExtra("name", listArrayList.get(position).getName());
            intent.putExtra("pid", listArrayList.get(position).getPositionId());
            intent.putExtra("uid", listArrayList.get(position).getUniqueId());
            intent.putExtra("status", listArrayList.get(position).getStatus());
            intent.putExtra("category", listArrayList.get(position).getCategory());
            intent.putExtra("lastupdate", listArrayList.get(position).getLastUpdates());
            intent.putExtra("diff", listArrayList.get(position).getTimeDiff());
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else  if (view.getId() == R.id.track_tv) {
            Intent trackIntent = new Intent(OnLineOffLineActivity.this, TrackingDevicesActivity.class);
            trackIntent.putExtra("device_id", listArrayList.get(position).getPositionId());
            trackIntent.putExtra("tname", listArrayList.get(position).getName());
            trackIntent.putExtra("tupdate", listArrayList.get(position).getLastUpdates());
            trackIntent.putExtra("ttimer", listArrayList.get(position).getTime());
            trackIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(trackIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
                searchVehicle(searchView);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void searchVehicle(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vehiclesAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
