package dhbk.android.gps_osm_fragment.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import dhbk.android.gps_osm_fragment.Fragment.ChatFragment.ChatActivityFragment;
import dhbk.android.gps_osm_fragment.Fragment.DirectionFragment.DirectionActivityFragment;
import dhbk.android.gps_osm_fragment.Fragment.DirectionFragment.MainActivityFragment;
import dhbk.android.gps_osm_fragment.Fragment.ShareFragment.SavedListTripActivityFragment;
import dhbk.android.gps_osm_fragment.Fragment.ShareFragment.ShareActivityFragment;
import dhbk.android.gps_osm_fragment.Help.Config;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DirectionActivityFragment.DirectionInterface, LocationListener
        , ShareActivityFragment.ShareFragmentInterface {
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private GoogleApiClient mGoogleApiClient;

    private Firebase mFirebaseRefer;

    protected LocationRequest mLocationRequest;

    public Firebase getFirebaseRefer() {
        return mFirebaseRefer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariable();

        // navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.direction) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, MainActivityFragment.newInstance())
                            .commit();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
                } else if (id == R.id.chat) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, ChatActivityFragment.newInstance())
                            .commit();
                } else if (id == R.id.nav_tracking) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, ShareActivityFragment.newInstance())
                            .commit();
                } else if (id == R.id.nav_gallery) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, SavedListTripActivityFragment.newInstance())
                            .commit();
                } else if (id == R.id.speak_en) {
                    navChooseLanguage(Constant.LAN_EN);
//                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.root_layout, SavedListTripActivityFragment.newInstance())
//                            .commit();
                } else if (id == R.id.speak_vi) {
                    navChooseLanguage(Constant.LAN_VI);
//                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.root_layout, SavedListTripActivityFragment.newInstance())
//                            .commit();
                }
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // load fragment main
        if (savedInstanceState == null) {
            // FragmentManager to add/remove fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, MainActivityFragment.newInstance())
                    .commit();
        }

    }

    private void initializeVariable() {
        // build + connect to google client;
        buildGoogleApiClient();
        // inialize firebase
        Config.getFirebaseInitialize(getApplicationContext());
        mFirebaseRefer = Config.getFirebaseReference();
    }

    // When press "Back",
    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();
        createLocationRequest();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Fail to connect to Google", Toast.LENGTH_SHORT).show();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    // khi ta chọn language trong navigation drawer thì gọi hàm này
    @Override
    public void navChooseLanguage(String language) {
        // neu fragment đang mở là fragment
        DirectionActivityFragment myFragment = (DirectionActivityFragment) getSupportFragmentManager().findFragmentByTag(Constant.DIRECTION_FRAGMENT);
        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            myFragment.setLanguage(language);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        // xet co lây dc fragment share ko
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (fragment instanceof ShareActivityFragment) {
            ((ShareActivityFragment) fragment).addLocationToRoute(location);
        }
    }

    // 2 method dựa vào interface của DirectionActivityFragment
    @Override
    public void canUpdateLocation() {
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void stopUpdateLocation() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }


    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

}
