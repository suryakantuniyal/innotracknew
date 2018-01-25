package com.innobins.innotrack.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.innobins.innotrack.activity.GoGeoDataProDialog;
import com.innobins.innotrack.activity.MainActivity;
import com.innobins.innotrack.activity.MapViewActivity;
import com.innobins.innotrack.activity.OfflineActivity;
import com.innobins.innotrack.activity.OnLineOffLineActivity;
import com.innobins.innotrack.activity.Reports.ReportsActivity;
import com.innobins.innotrack.activity.SessionHandler;
import com.innobins.innotrack.activity.SignUpAccount;
import com.innobins.innotrack.adapter.ViewPagerAdapter;
import com.innobins.innotrack.network.ResponseCallback;
import com.innobins.innotrack.network.WebserviceHelper;
import com.innobins.innotrack.utils.URLContstant;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.innobins.innotrack.R;

/**
 * Created by silence12 on 22/1/18.
 */

public class HomeActivity extends BaseActivity implements OnChartValueSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static ProgressDialog progressDialog;
    TextView totalDevices, onlineDevices, offlineDevices;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor, editor;
    private static Boolean log = false;
    private int currentPage;
    private ViewPager circleviewPager;
    private ViewPagerAdapter viewPagerAdapter;

    GoGeoDataProDialog goGeoDataProDialog;
    PieChart pieChart ;
    LinearLayout mapView_ll,listView_ll ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.innotrack_icon);
        customTitle("   "+"Innotrack");
        mSharedPreferences = getSharedPreferences(URLContstant.PREFERENCE_NAME, MODE_PRIVATE);
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        goGeoDataProDialog = new GoGeoDataProDialog(this);
        Intent intent = getIntent();
        boolean b = intent.getBooleanExtra("logged",false);
        init();
//        vehicleStatusData();
        getData();
        piChartData();
        setSupportActionBar(toolbar);
        CheckGPS();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.username_tv);
        String userName = mSharedPreferences.getString(URLContstant.KEY_USERNAME, "");
        username.setText(userName);
    }

    private void getData() {
        goGeoDataProDialog.show();
        String mUrl = "https://mtrack-api.appspot.com/api/get/devices/byuser/" ;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",mSharedPreferences.getInt(URLContstant.KEY_LOGEDIN_USERID,-1));
            WebserviceHelper.getInstance().PostCall(HomeActivity.this, mUrl, jsonObject, new ResponseCallback() {
                @Override
                public void OnResponse(JSONObject Response) {
                    try {
                        JSONArray jsonArray = Response.getJSONArray("deviceData");
                        SessionHandler.updateSnessionHandler(getBaseContext(),jsonArray,mSharedPreferences);
                        goGeoDataProDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void init() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), HomeActivity.this);
        circleviewPager = (ViewPager) findViewById(R.id.pager);
        totalDevices = (TextView) findViewById(R.id.all_count);
        onlineDevices = (TextView) findViewById(R.id.online_count);
        offlineDevices = (TextView) findViewById(R.id.offline_count);
        mapView_ll = (LinearLayout)findViewById(R.id.mapview_ll);
        listView_ll = (LinearLayout)findViewById(R.id.listview_ll);
        mapView_ll.setOnClickListener(this);
        listView_ll.setOnClickListener(this);
        circleviewPager.setAdapter(viewPagerAdapter);
        circleviewPager.setCurrentItem(0);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.dotindicator);
        indicator.setViewPager(circleviewPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(3 * density);
        currentPage = 0;
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                circleviewPager.setCurrentItem(currentPage % 10, true);
                currentPage++;
            }
        };
        int delay = 500;
        int period = 1000;
        Timer swipeTimer = new Timer();
        swipeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, delay, period);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }


    private void vehicleStatusData() {
        goGeoDataProDialog.show();
        String mUrl = "https://mtrack-api.appspot.com/api/get/summary/byuser/" ;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",mSharedPreferences.getInt(URLContstant.KEY_LOGEDIN_USERID,-1));
            WebserviceHelper.getInstance().PostCall(HomeActivity.this, mUrl, jsonObject, new ResponseCallback() {
                @Override
                public void OnResponse(JSONObject Response) {
                    try {
                        JSONArray jsonArray = Response.getJSONArray("summaryData");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        mEditor = mSharedPreferences.edit();
                        mEditor.putInt("active", jsonObject1.getInt("online_vehicle"));
                        mEditor.putInt("inactive", jsonObject1.getInt("offline_vehicle"));
                        mEditor.putInt("running",jsonObject1.getInt("running_vehicle"));
                        mEditor.putInt("total", jsonObject1.getInt("total_vehicle"));
                        mEditor.putInt("unknown",jsonObject1.getInt("unknown_vehicle"));
                        mEditor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mapview) {

            Intent intent = new Intent(HomeActivity.this, MapViewActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_listview) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (id == R.id.nav_reports) {

            Intent intent = new Intent(HomeActivity.this, ReportsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (id == R.id.nav_contact) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:09999095036"));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                return TODO;
            }
            startActivity(callIntent);

        } else if (id == R.id.nav_online) {
            Intent intent = new Intent(HomeActivity.this, OnLineOffLineActivity.class);
            intent.putExtra("onoff", "online");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


        } else if (id == R.id.nav_offline) {
            Intent intent = new Intent(HomeActivity.this, OfflineActivity.class);
            intent.putExtra("onoff", "offline");
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        } else if (id == R.id.nav_logout) {
            mSharedPreferences = getSharedPreferences(URLContstant.PREFERENCE_NAME, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
            mEditor.putBoolean(URLContstant.KEY_LOGGED_IN, false);
            mEditor.putString(URLContstant.KEY_USERNAME,null);
            mEditor.putString(URLContstant.KEY_PASSWORD,null);
            mEditor.putString("deviceData","{}");
            mEditor.apply();
            Intent intent = new Intent(HomeActivity.this, SignUpAccount.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override

    public void onClick(View view) {
        if (view.getId() == R.id.listview_ll || view.getId() == R.id.alldevices_view) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view.getId() == R.id.mapview_ll ) {
            Intent intent = new Intent(HomeActivity.this, MapViewActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }



    public void CheckGPS(){

        if (!isGPSenabled(HomeActivity.this)){

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("GPS is Disable.Enbale it for better results")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

        }
    }

    public boolean isGPSenabled(Context context) {
        LocationManager lm = null;
        boolean gps_enabled = false,network_enabled=false;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}
        if (gps_enabled&&network_enabled){
            return true;
        }else {
            return false;
        }
    }

    private void piChartData(){
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        List<Integer> VORDIPLOM_COLORS = new ArrayList<>();
        PieDataSet dataSet = new PieDataSet(yvalues, "");

        ArrayList<String> xVals = new ArrayList<String>();

        if(mSharedPreferences.getInt("active",-1)!=0){
            yvalues.add(new Entry(mSharedPreferences.getInt("active",-1), 0));
            xVals.add("Online");
            VORDIPLOM_COLORS.add(Color.rgb(192, 255, 140));
        }
        if(mSharedPreferences.getInt("inactive",-1)!=0) {
            yvalues.add(new Entry(mSharedPreferences.getInt("inactive",-1), 1));
            xVals.add("Offline");
            VORDIPLOM_COLORS.add(Color.rgb(255, 77, 77));
        }
        if(mSharedPreferences.getInt("running",-1)!=0) {
            yvalues.add(new Entry(mSharedPreferences.getInt("running",-1), 2));
            xVals.add("Running");
            VORDIPLOM_COLORS.add(Color.rgb(140, 234, 255));
        }
        if(mSharedPreferences.getInt("unknown",-1)!=0) {
            yvalues.add(new Entry(mSharedPreferences.getInt("unknown",-1), 3));
            xVals.add("No Data");
            VORDIPLOM_COLORS.add(Color.rgb(30, 30, 30));
        }
        PieData data = new PieData(xVals, dataSet);
        // In Percentage
        data.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
//        int[] VORDIPLOM_COLORS = {
//                Color.rgb(192, 255, 140),Color.rgb(255, 208, 140),
//                Color.rgb(140, 234, 255),  Color.rgb(140, 234, 255),
//        };
        pieChart.setData(data);
        pieChart.setDescription("");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(28f);
        pieChart.setHoleRadius(28f);
        pieChart.setCenterText(String.valueOf(mSharedPreferences.getInt("total",-1)));
        pieChart.setDrawCenterText(true);
        pieChart.setDrawSliceText(true);
        dataSet.setColors(VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(HomeActivity.this);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;

        if(e.getXIndex()==0){
            Intent intent = new Intent(HomeActivity.this, OnLineOffLineActivity.class);
            intent.putExtra("onoff", "online");
            startActivity(intent);
        }else if(e.getXIndex()==1){
            Intent intent = new Intent(HomeActivity.this, OfflineActivity.class);
            intent.putExtra("onoff", "offline");
            startActivity(intent);
        }else if(e.getXIndex()==2){
            Intent intent = new Intent(HomeActivity.this, OnLineOffLineActivity.class);
            intent.putExtra("onoff", "online");
            startActivity(intent);
        }else if(e.getXIndex()==3){
            Toast.makeText(this,"No data found",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected() {

    }
}
