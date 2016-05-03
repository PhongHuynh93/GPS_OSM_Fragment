package dhbk.android.gps_osm_fragment.Fragment.DirectionFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.util.GeoPoint;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.Fragment.BottomSheetFragment;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.Help.FetchAddressIntentService;
import dhbk.android.gps_osm_fragment.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment {
    public static final String TAG = "MainActivityFragment";
    private AddressResultReceiver mResultReceiver;

    public static MainActivityFragment newInstance() {
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        return mainActivityFragment;
    }

    // pass layout to parent fragment so that it can render child layout
    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        super.makeMapDefaultSetting();

        // receive address at a location.
        mResultReceiver = new AddressResultReceiver(new Handler());


        final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ((BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets)).addPlaceToBottomSheet(place);
                // remove marker on the map, center at that point and add marker.
                clearMap();
                Location placeLocation = new Location("Test");
                placeLocation.setLatitude(place.getLatLng().latitude);
                placeLocation.setLongitude(place.getLatLng().longitude);
                setMarkerAtLocation(placeLocation, Constant.MARKER);
            }

            @Override
            public void onError(Status status) {

            }
        });

        final FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab_my_location);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).getGoogleApiClient().isConnected()) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    clearMap();
                    Location userCurrentLocation = getLocation();
                    setMarkerAtLocation(userCurrentLocation, Constant.MARKER);
                } else {
                    Toast.makeText(getContext(), "GoogleApi not connect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // if click, go to another activity
        final FloatingActionButton floatingActionButtonDirection = (FloatingActionButton) getActivity().findViewById(R.id.fab_direction);
        floatingActionButtonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 5/3/16 go to another fragment

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface MainActivityFragmentInterface {
        void onClickDirection();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(getMapView());
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        clearMap();
        // reverse location into address
        // make search bar and bottom sheet contain address

        // draw marker
        Location touchLocation = new Location("touchLocation");
        touchLocation.setLatitude(p.getLatitude());
        touchLocation.setLongitude(p.getLongitude());
        setMarkerAtLocation(touchLocation, Constant.MARKER);

        startIntentService(touchLocation);
        return true;
    }

    protected void startIntentService(Location touchLocation) {
        if (((MainActivity)getActivity()).getGoogleApiClient().isConnected()) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            Intent intent = new Intent(getContext(), FetchAddressIntentService.class);

            // Pass the result receiver as an extra to the service.
            intent.putExtra(Constant.RECEIVER, mResultReceiver);

            // Pass the location data as an extra to the service.
            intent.putExtra(Constant.LOCATION_DATA_EXTRA, touchLocation);

            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            getActivity().startService(intent);
        }
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            String addressOutput = resultData.getString(Constant.RESULT_DATA_KEY);
            displayAddressOutput(addressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constant.SUCCESS_RESULT) {
                Log.i(TAG, "onReceiveResult: " + R.string.address_found);
            }
        }
    }

    // display address in bottom sheet
    private void displayAddressOutput(String addressOutput) {
        Log.i(TAG, "displayAddressOutput: " + addressOutput);
        BottomSheetFragment bottomSheetFragment = (BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets);
        BottomSheetBehavior<View> bottomSheetBehavior = bottomSheetFragment.getBottomSheetBehavior();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            // add place details
            bottomSheetFragment.getPlaceName().setText(addressOutput);
            bottomSheetBehavior.setPeekHeight(369);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

}
