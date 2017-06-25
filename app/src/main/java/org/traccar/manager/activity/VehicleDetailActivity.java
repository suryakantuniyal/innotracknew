package org.traccar.manager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.traccar.manager.R;
import org.traccar.manager.api.APIServices;
import org.traccar.manager.model.VehicleList;
import org.traccar.manager.network.DetailResponseCallback;
import org.traccar.manager.network.ResponseCallbackEvents;

import java.util.ArrayList;

/**
 * Created by silence12 on 21/6/17.
 */

public class VehicleDetailActivity extends AppCompatActivity {

    private static final String TAG = VehicleDetailActivity.class.getSimpleName();
    private TextView name_tv,positionId_tv,uniqueId_tv,status_tv,lastUpdate_tv,category_tv,contact_tv,speed_tv,distance_tv;
    private ImageView projImageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressDialog progressDialog;
    private String nameString,positionIdString,uniqueIdString,lastUpdatetString,categoryString,statusString,contactString;
    private static String address;
    private int id;
    private static Double speed;
    private Button homeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vehicle);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getIntentFileds();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(nameString);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#3c3c3c"));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Hold on for a moment.");
        progressDialog.show();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.name().equals(State.COLLAPSED.name())) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_grey);
                } else if (state.name().equals(State.IDLE.name())) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
                }
            }
        });
        initViews();
        setProgressBarIndeterminateVisibility(true);
        callDetailRequest(id);
    }

    private void getIntentFileds()
    {
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id",-1);
        nameString = getIntent.getStringExtra("name");
        uniqueIdString = getIntent.getStringExtra("uid");
        lastUpdatetString = getIntent.getStringExtra("lastupdate");
        statusString = getIntent.getStringExtra("status");
        categoryString = getIntent.getStringExtra("category");
        contactString = getIntent.getStringExtra("contact");
    }
    public void callDetailRequest(int id) {
        APIServices.GetVehicleDetailById(VehicleDetailActivity.this, id, new DetailResponseCallback() {
            @Override
            public void OnResponse(VehicleList Response) {
                progressDialog.dismiss();
                name_tv.setText(nameString);
                String speed = String.valueOf(Response.speed);
                String distance = String.valueOf(Response.distance_travelled);
                positionId_tv.setText(Response.address);
                uniqueId_tv.setText(uniqueIdString);
                lastUpdate_tv.setText(lastUpdatetString);
                status_tv.setText(statusString);
                category_tv.setText(categoryString);
                contact_tv.setText(contactString);
                speed_tv.setText(speed);
                distance_tv.setText(distance);
            }
        });
    }
    private void initViews() {
        projImageView = (ImageView) findViewById(R.id.category_image_iv);
        name_tv = (TextView) findViewById(R.id.project_name_tv);
        positionId_tv = (TextView) findViewById(R.id.positionId_tv);
        uniqueId_tv = (TextView) findViewById(R.id.uniqueid_tv);
        lastUpdate_tv = (TextView) findViewById(R.id.date_tv);
        status_tv = (TextView) findViewById(R.id.status_tv);
        category_tv = (TextView) findViewById(R.id.category_tv);
        contact_tv = (TextView) findViewById(R.id.contact_tv);
        speed_tv = (TextView) findViewById(R.id.speed_tv);
        distance_tv = (TextView) findViewById(R.id.distancecover_tv);
        homeButton = (Button) findViewById(R.id.gohome_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(VehicleDetailActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Glide.with(this).load(R.drawable.ic_truck).asBitmap()
                .centerCrop().placeholder(R.drawable.placeholderxx4).into(projImageView);


    }

    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

        public enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }
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
        Intent intent = new Intent(VehicleDetailActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
