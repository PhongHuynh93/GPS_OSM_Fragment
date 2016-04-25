package dhbk.android.gps_osm_fragment.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;
import dhbk.android.gps_osm_fragment.Voice.AccentRemover;
import dhbk.android.gps_osm_fragment.Voice.VIetnameseSpeak;

/**
 * Created by huynhducthanhphong on 4/22/16.
 */
public abstract class BaseFragment extends Fragment implements MapEventsReceiver {
    private MapView mMapView;
    private IMapController mIMapController;


    public void makeMapDefaultSetting() {
        mMapView = (MapView) getActivity().findViewById(R.id.map); // map
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);
        mIMapController = mMapView.getController(); // map controller
        mIMapController.setZoom(Constant.ZOOM);
        GeoPoint startPoint = new GeoPoint(10.772241, 106.657676);
        mIMapController.setCenter(startPoint);
    }

    // clear map, but add eventlocation
    public void clearMap() {
        mMapView.getOverlays().clear();
        // add event overlay
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
    }

    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(((MainActivity)getActivity()).getGoogleApiClient());
    }

    //phong - add marker at a location
    public void setMarkerAtLocation(Location userCurrentLocation, int icon) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            mIMapController.setCenter(userCurrentPoint);
            mIMapController.zoomTo(mMapView.getMaxZoomLevel());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));
//                        hereMarker.setTitle("You here");
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    // phong - add marker with title
    public void setMarkerAtLocation(Location userCurrentLocation, int icon, String title) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));
            final String instructionNeedRemove = "" + Html.fromHtml(title);

            // remove a part of string
            String instruction = instructionNeedRemove;
            if (instruction.indexOf("\n\n") != -1) {
                // it contains world
                instruction = instructionNeedRemove.substring(0, instructionNeedRemove.indexOf("\n\n"));
            }

            final String instructionKhongDau = new AccentRemover().toUrlFriendly(instruction);
//            Log.i(TAG, "setMarkerAtLocation: " + Html.toHtml(Html.fromHtml(title)));
            hereMarker.setTitle(instruction);

            // when click marker, speak instruction
            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    marker.showInfoWindow();
                    mapView.getController().animateTo(marker.getPosition());
                    new VIetnameseSpeak(getContext(), instructionKhongDau).speak();
                    return true;
                }
            });

            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @NonNull
    public String retrieveSubString(String s) {
        return s.substring(0, s.lastIndexOf("@"));
    }

}
