package com.example.divyansh.myapplication;

import android.app.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private Toolbar toolbar;
    Marker mGroup1;
    Marker mGroup2;
    Marker mGroup3;
    Marker mGroup4;
    Marker mGroup5;
    Button groupInfoButton;
    Context mContext;
    FloatingActionButton filterButton;
    FloatingActionButton rangeButton;
    SeekBar knnSeekBar;
    TextView knnTextView;
    SeekBar capacitySeekbar;
    TextView capacityTextView;
    Switch mknnFilterSwitch;
    Switch mCapacityFilterSwitch;
    HashMap<String, String> mSubjectsMap;
    SeekBar rangeSeekBar2;
    Circle mCircle = null;

    String currentUser = "ranjini";

    StudyGroups[] mStudyGroups;
    ArrayList<Integer> currentGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        rangeSeekBar2 = (SeekBar) findViewById(R.id.rangeSeekbar2);
        //AppCompatActivity appCompatActivity = new AppCompatActivity();
        //appCompatActivity.setSupportActionBar(toolbar);
        mSubjectsMap = new SubjectsMap().getSubjectMap();
        filterButton = (FloatingActionButton) findViewById(R.id.filterOption);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog filterDialog = new Dialog(MapsActivity.this);
                filterDialog.setContentView(R.layout.filter_groups);
                filterDialog.setTitle("    Filter Groups");

                mknnFilterSwitch = (Switch) filterDialog.findViewById(R.id.knnSwitch);
                knnSeekBar = (SeekBar) filterDialog.findViewById(R.id.knnSeekbar);
                knnTextView = (TextView) filterDialog.findViewById(R.id.knnText);
                mCapacityFilterSwitch = (Switch) filterDialog.findViewById(R.id.capacitySwitch);
                capacitySeekbar = (SeekBar) filterDialog.findViewById(R.id.capacitySeekbar);
                capacityTextView = (TextView) filterDialog.findViewById(R.id.capacityText);

                Button dialogButton2 = (Button) filterDialog.findViewById(R.id.dialogButtonOK2);
                dialogButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createFilterParameters(capacitySeekbar.getProgress());
                        filterDialog.dismiss();
                    }
                });

                filterDialog.show();


                initializeFilterOptions();
            }
        });

        rangeButton = (FloatingActionButton)  findViewById(R.id.rangeOption);
        rangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rangeSeekBar2.getVisibility()==View.INVISIBLE){
                    rangeSeekBar2.setVisibility(View.VISIBLE);
                } else{
                    if(mCircle!=null){
                        mCircle.setRadius(0);
                    }
                    rangeSeekBar2.setVisibility(View.INVISIBLE);
                }
            }
        });

        rangeSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int rangeProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangeProgress = progress;
                if(mCircle==null){
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.fillColor(Color.parseColor("#447755ff"));
                    circleOptions.strokeColor(Color.TRANSPARENT);
                    circleOptions.center(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                    circleOptions.radius(rangeProgress*50);
                    circleOptions.strokeWidth(3.0f);
                    mCircle = mMap.addCircle(circleOptions);
                } else {
                    mCircle.setCenter(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                    mCircle.setRadius(rangeProgress*50);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mapFragment.getMapAsync(this);
        FloatingActionButton listviewbutton = (FloatingActionButton) findViewById(R.id.fab2);
        listviewbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent listIntent = new Intent(MapsActivity.this, ListActivity.class);
                startActivity(listIntent);
            }
        });
        ImageButton createviewbutton = (ImageButton) findViewById(R.id.FilterButton);
        createviewbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent createIntent = new Intent(MapsActivity.this, CreateScreen.class);
                startActivity(createIntent);
            }
        });
    }

    public void initializeFilterOptions(){

        mknnFilterSwitch.setChecked(false);
        mknnFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    knnSeekBar.setVisibility(View.VISIBLE);
                    knnTextView.setVisibility(View.VISIBLE);
                }
                else{
                    knnSeekBar.setVisibility(View.GONE);
                    knnTextView.setVisibility(View.GONE);
                }
            }
        });

        knnTextView.setText("" + knnSeekBar.getProgress() + "/" + knnSeekBar.getMax());

        knnSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                knnTextView.setText("" + progress + "/" + seekBar.getMax());
            }
        });

        mCapacityFilterSwitch.setChecked(false);
        mCapacityFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    capacitySeekbar.setVisibility(View.VISIBLE);
                    capacityTextView.setVisibility(View.VISIBLE);
                } else {
                    capacitySeekbar.setVisibility(View.GONE);
                    capacityTextView.setVisibility(View.GONE);
                }
            }
        });

        capacityTextView.setText("" + capacitySeekbar.getProgress() + "/" + capacitySeekbar.getMax());

        capacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int capacityprogress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                capacityprogress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                capacityTextView.setText("" + capacityprogress + "/" + seekBar.getMax());
            }
        });
    }

    public void createFilterParameters(int filterknn){
        new FilterStudyGroups().execute(String.valueOf(filterknn));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else{
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        new GetAllStudyGroups().execute("sx");
        plotCurrentUserLocation();

    }

    public void plotCurrentUserLocation(){
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(latLng);
        //markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //markerOptions.snippet("Other Info");
        //mCurrLocationMarker = mMap.addMarker(markerOptions);
        //mCurrLocationMarker.showInfoWindow();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void plotStudyGroups(){
        for(int i=0;i<mStudyGroups.length;i++){
            StudyGroups currentGroup = mStudyGroups[i];
            LatLng latLng = new LatLng(currentGroup.mGroupLatitude, currentGroup.mGroupLongitude);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latLng);
            markerOptions1.title(currentGroup.mGroupName);
            markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions1.snippet(currentGroup.mSubjectId);
            mGroup1 = mMap.addMarker(markerOptions1);
        }
    }

    private void addMockMarkers(Location currentLocation){
        //Place current location marker
        LatLng latLng = new LatLng(currentLocation.getLatitude()+.0006, currentLocation.getLongitude()+.0004);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(latLng);
        markerOptions1.title("Group 1");
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions1.snippet("Algorithms");
        mGroup1 = mMap.addMarker(markerOptions1);

        LatLng latLng2 = new LatLng(currentLocation.getLatitude()-0.0023, currentLocation.getLongitude()-0.0036);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(latLng2);
        markerOptions2.title("Group 2");
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions2.snippet("Machine Learning");
        mGroup2 = mMap.addMarker(markerOptions2);

        LatLng latLng3 = new LatLng(currentLocation.getLatitude()-0.0033, currentLocation.getLongitude()-0.0021);
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(latLng3);
        markerOptions3.title("Group 3");
        markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions3.snippet("Database");
        mGroup3 = mMap.addMarker(markerOptions3);

        LatLng latLng4 = new LatLng(currentLocation.getLatitude()-0.0032, currentLocation.getLongitude()-0.0046);
        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(latLng4);
        markerOptions4.title("Group 4");
        markerOptions4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions4.snippet("Comp. Networks");
        mGroup4 = mMap.addMarker(markerOptions4);

        LatLng latLng5 = new LatLng(currentLocation.getLatitude()-0.0006, currentLocation.getLongitude()-0.0014);
        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(latLng5);
        markerOptions5.title("Group 5");
        markerOptions5.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOptions5.snippet("Database");
        mGroup5 = mMap.addMarker(markerOptions5);
    }

    public StudyGroups getInfoWindowStudyGroup(String groupName){
        for(int i=0;i<mStudyGroups.length;i++){
            StudyGroups currentGroup = mStudyGroups[i];
            if(currentGroup.mGroupName.equals(groupName))
                return currentGroup;
        }
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        final StudyGroups clickedGroup = getInfoWindowStudyGroup(marker.getTitle());
        // custom dialog
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.group_info_dialog);
        dialog.setTitle(marker.getTitle());
        new GetJoinedGroups().execute("random");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.subjectName);
        text.setText(marker.getSnippet());
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.ml);

        TextView topics = (TextView) dialog.findViewById(R.id.topics);
        topics.setText("Topics : " + clickedGroup.mTopic);

        TextView locationName = (TextView) dialog.findViewById(R.id.locationName);
        locationName.setText("Location : " + clickedGroup.mLocationName);

        TextView groupTimeInfo = (TextView) dialog.findViewById(R.id.groupTime);
        groupTimeInfo.setText("Time : " + clickedGroup.mStartTime + " - " + clickedGroup.mEndTime);

        TextView groupCapacityInfo = (TextView) dialog.findViewById(R.id.groupCapacityInfo);
        groupCapacityInfo.setText("Capacity : " + clickedGroup.mNumMembers);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        if(currentGroups!=null){
            for(int i=0;i<currentGroups.size();i++){
                if(currentGroups.get(i)==Integer.parseInt(clickedGroup.mGroupId)){
                    dialogButton.setVisibility(View.INVISIBLE);
                }
            }
        }
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JoinStudyGroups().execute(clickedGroup.mGroupId);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.info_marker,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (mCurrLocationMarker != null
                    && mCurrLocationMarker.isInfoWindowShown()) {
                mCurrLocationMarker.hideInfoWindow();
                mCurrLocationMarker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            mCurrLocationMarker = marker;

            String url = null;

            final String title = marker.getTitle();
            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final TextView snippetUi = ((TextView) view
                    .findViewById(R.id.snippet));
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            groupInfoButton = (Button) view.findViewById(R.id.markerInfoButton);
            mContext = view.getContext();
            // add button listener
            /*groupInfoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    groupInfoButton.setText("qaz");
                    // custom dialog
                    final Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.group_info_dialog);
                    dialog.setTitle("Title...");

                    // set the custom dialog components - text, image and button
                    TextView text = (TextView) dialog.findViewById(R.id.text);
                    text.setText("Android custom dialog example!");
                    ImageView image = (ImageView) dialog.findViewById(R.id.image);
                    image.setImageResource(R.drawable.ml);

                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });*/

            return view;
        }

    }


    class GetAllStudyGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/getAllGroups";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                mStudyGroups = new StudyGroups[groupList.length()];
                for(int i=0;i<groupList.length();i++){
                    JSONObject groupInfo = groupList.getJSONObject(i);
                    String subjectName = mSubjectsMap.get(groupInfo.getString("subjectId"));
                    mStudyGroups[i] = new StudyGroups(groupInfo.getString("id"),
                            subjectName,
                            groupInfo.getString("groupName"),
                            groupInfo.getString("adminId"),
                            groupInfo.getLong("startTimestamp"),
                            groupInfo.getLong("endTimestamp"),
                            groupInfo.getInt("capacity"),
                            groupInfo.getInt("numMembers"),
                            groupInfo.getString("topic"),
                            groupInfo.getString("locationName"),
                            groupInfo.getDouble("latitude"),
                            groupInfo.getDouble("longitude"));
                    Log.d("GroupId ",groupInfo.getString("id"));
                    Log.d("SubjectId ",groupInfo.getString("subjectId"));
                    Log.d("groupName ",groupInfo.getString("groupName"));
                    Log.d("adminId ",groupInfo.getString("adminId"));
                    Log.d("startTimestamp ",String.valueOf(groupInfo.getLong("startTimestamp")));
                    Log.d("endTimestamp ", String.valueOf(groupInfo.getLong("endTimestamp")));
                    Log.d("capacity ",String.valueOf(groupInfo.getInt("capacity")));
                    Log.d("numMembers ",String.valueOf(groupInfo.getInt("numMembers")));
                    Log.d("locationName ",groupInfo.getString("locationName"));
                    Log.d("latitude ",String.valueOf(groupInfo.getDouble("latitude")));
                    Log.d("longitude ", String.valueOf(groupInfo.getDouble("longitude")));
                }

                plotStudyGroups();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class JoinStudyGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String non_parametrized_url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/joinGroup?";
                String url = non_parametrized_url + "groupId=" + key[0] + "&userId=" + currentUser;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String filterApiResponse) {
            Log.d("Filter response",filterApiResponse);
        }
    }

    class GetJoinedGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/getJoinedGroups/"+currentUser;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            currentGroups = new ArrayList<>();
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                for(int i=0;i<groupList.length();i++){
                    currentGroups.add(groupList.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class FilterStudyGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/searchGroups?" + "maxCapacity=" + Integer.parseInt(key[0]);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            mMap.clear();
            plotCurrentUserLocation();
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                mStudyGroups = new StudyGroups[groupList.length()];
                for(int i=0;i<groupList.length();i++){
                    JSONObject groupInfo = groupList.getJSONObject(i);
                    String subjectName = mSubjectsMap.get(groupInfo.getString("subjectId"));
                    mStudyGroups[i] = new StudyGroups(groupInfo.getString("id"),
                            subjectName,
                            groupInfo.getString("groupName"),
                            groupInfo.getString("adminId"),
                            groupInfo.getLong("startTimestamp"),
                            groupInfo.getLong("endTimestamp"),
                            groupInfo.getInt("capacity"),
                            groupInfo.getInt("numMembers"),
                            groupInfo.getString("topic"),
                            groupInfo.getString("locationName"),
                            groupInfo.getDouble("latitude"),
                            groupInfo.getDouble("longitude"));
                    Log.d("GroupId ",groupInfo.getString("id"));
                    Log.d("SubjectId ",groupInfo.getString("subjectId"));
                    Log.d("groupName ",groupInfo.getString("groupName"));
                    Log.d("adminId ",groupInfo.getString("adminId"));
                    Log.d("startTimestamp ",String.valueOf(groupInfo.getLong("startTimestamp")));
                    Log.d("endTimestamp ", String.valueOf(groupInfo.getLong("endTimestamp")));
                    Log.d("capacity ",String.valueOf(groupInfo.getInt("capacity")));
                    Log.d("numMembers ",String.valueOf(groupInfo.getInt("numMembers")));
                    Log.d("locationName ",groupInfo.getString("locationName"));
                    Log.d("latitude ",String.valueOf(groupInfo.getDouble("latitude")));
                    Log.d("longitude ", String.valueOf(groupInfo.getDouble("longitude")));
                }

                plotStudyGroups();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
