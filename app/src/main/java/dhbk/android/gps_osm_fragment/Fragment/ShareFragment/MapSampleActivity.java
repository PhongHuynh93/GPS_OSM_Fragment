package dhbk.android.gps_osm_fragment.Fragment.ShareFragment;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapSampleActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapSampleActivity extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ROUTE = "route";

    private String mParam1;
    private String mParam2;

    RoadManager roadManager = new OSRMRoadManager(getContext());
    MapView mMap;
    Marker mStart, mEnd;
    private IMapController mapController;
    private String routeJson;

    public MapSampleActivity() {
        // Required empty public constructor
    }

    public static MapSampleActivity newInstance(String route) {
        MapSampleActivity fragment = new MapSampleActivity();
        Bundle args = new Bundle();
        args.putString(ARG_ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routeJson = getArguments().getString(ARG_ROUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_map_sample, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        makeMapDefaultSetting();
        mMap = getMapView();
        mapController = getIMapController();

        ArrayList<GeoPoint> route = getRoute(routeJson);
        Road road = new Road(route);
        Polyline line = roadManager.buildRoadOverlay(road, getContext());
        mMap.getOverlays().add(line);

        mapController.setCenter(route.get(0));
        mapController.setZoom(18);
        mStart = new Marker(mMap);
        Drawable startIcon = getResources().getDrawable(R.drawable.ic_place_green_24dp);
        mStart.setIcon(startIcon);
        mStart.setPosition(route.get(0));
        mMap.getOverlays().add(mStart);

        mEnd = new Marker(mMap);
        Drawable end = getResources().getDrawable(R.drawable.ic_place_blue_24dp);
        mEnd.setIcon(end);
        mEnd.setPosition(route.get(route.size() - 1));
        mMap.getOverlays().add(mEnd);

        mMap.invalidate();
    }

    private ArrayList<GeoPoint> getRoute(String routeJson) {
        SharedPreferences settings;
        List<GeoPoint> favorites;
        String jsonFavorites = routeJson;
        Gson gson = new Gson();
        GeoPoint[] favoriteItems = gson.fromJson(jsonFavorites,
                GeoPoint[].class);

        favorites = Arrays.asList(favoriteItems);
        favorites = new ArrayList<GeoPoint>(favorites);
        return (ArrayList<GeoPoint>) favorites;

    }
}
