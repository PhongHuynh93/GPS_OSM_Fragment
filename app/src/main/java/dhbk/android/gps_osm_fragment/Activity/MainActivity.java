package dhbk.android.gps_osm_fragment.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import dhbk.android.gps_osm_fragment.Fragment.ChatFragment.ChatActivityFragment;
import dhbk.android.gps_osm_fragment.Fragment.MainActivityFragment;
import dhbk.android.gps_osm_fragment.Help.Config;
import dhbk.android.gps_osm_fragment.R;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, MainActivityFragment.MainActivityFragmentInterface, ChatActivityFragment.OnFragmentChatInteractionListener {

    private GoogleApiClient mGoogleApiClient;

    private Firebase mFirebaseRefer;

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
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
                } else if (id == R.id.chat) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, ChatActivityFragment.newInstance())
                            .commit();
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
                .enableAutoManage(this, this)
                .build();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Fail to connect to Google", Toast.LENGTH_SHORT).show();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    // TODO: 4/24/16  go to direction Fragment
    @Override
    public void onClickDirection() {

    }

    // TODO: 4/24/16 chat method
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
