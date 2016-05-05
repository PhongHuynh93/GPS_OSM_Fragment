package dhbk.android.gps_osm_fragment.Fragment.ShareFragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShareActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShareActivityFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final int ACTIVITY_CAMERA_APP = 0;
    // private static final String GALLERY_LOCATION = "tripGallery" + new SimpleDateFormat("ydM_Hms").format(new Date()); // name of the folder gallery
    private static final String GALLERY_LOCATION = "tripGallery";
    LocationManager locationManager;
    RoadManager roadManager = new OSRMRoadManager(getContext());
    ArrayList<GeoPoint> route;
    Marker mStart, mEnd;
    MapView mMap;
    LocationListener locationListenerGPS, locationListenerNetWork;
    private IMapController mapController;
    private ImageView img;

    private String imageFileLocation = "";
    private File galleryFoler;


    public ShareActivityFragment() {
        // Required empty public constructor
    }

    public static ShareActivityFragment newInstance() {
        ShareActivityFragment fragment = new ShareActivityFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_share_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createImageGallery(); //create gallery when onCreate
        makeMapDefaultSetting();
        mMap = getMapView();
        mapController = getIMapController();
        mapController.setZoom(18);
        route = new ArrayList<GeoPoint>();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mStart = new Marker(mMap);
        Drawable startIcon = getResources().getDrawable(R.drawable.start);
        mStart.setIcon(startIcon);
        mEnd = new Marker(mMap);
        Drawable end = getResources().getDrawable(R.drawable.end);
        mEnd.setIcon(end);


        locationListenerNetWork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint temp = new GeoPoint(location.getLatitude(), location.getLongitude(), 16);
                // Toast.makeText(getBaseContext(), String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                mapController.setCenter(temp);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // TODO: 5/5/16 fix lại chỗ này, bị crash
        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GeoPoint temp = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                route.add(temp);
                Road road = new Road(route);
                Polyline line = roadManager.buildRoadOverlay(road, getContext());
                mMap.getOverlays().add(line);
                mMap.invalidate();
                if (route.size() == 1) {
                    mStart.setPosition(temp);
                    mMap.getOverlays().add(mStart);
                    mMap.invalidate();
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.removeUpdates(locationListenerNetWork);
                    }

                } else if (route.size() > 2) {
                    if (mEnd.getPosition() != null)
                        mMap.getOverlays().remove(mEnd);
                    mEnd.setPosition(temp);
                    mMap.getOverlays().add(mEnd);
                    mMap.invalidate();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 10000, 0, locationListenerNetWork);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            mMap.getOverlays().clear();
        }

//        floating action button
        getActivity().findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = null;
                try {
                    photo = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, ACTIVITY_CAMERA_APP);
            }
        });

        getActivity().findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    if (route.size() > 2) {
                        locationManager.removeUpdates(locationListenerGPS);
//                        Gson gson = new Gson();
//                        String routeJSON = gson.toJson(route);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("routeJSON", routeJSON);
//                        Intent intent = new Intent(this, SaveRouteActivity.class);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
                        // TODO: 5/4/16 make fragment, copy bundle
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.root_layout, SaveRouteActivityFragment.newInstance(route))
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), " Chua co thong tin lo trinh  ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }
        });
    }

    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera"); //path to folder gallery
        galleryFoler = new File(storageDirectory, GALLERY_LOCATION); // prepare create folder
        if (!galleryFoler.exists()) {
            galleryFoler.mkdirs(); //create folder
        }
    }

    public File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyydMM_Hms").format(new Date());
        String imgName = "IMG_" + timestamp;

        File img = File.createTempFile(imgName, ".jpg", galleryFoler);
        img.setReadable(true);
        imageFileLocation = img.getAbsolutePath();

        return img;
    }

}
