package dhbk.android.gps_osm_fragment.Fragment.DirectionFragment;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

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
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1 = 1;
    public static final String TAG = "MainActivityFragment";
    private AddressResultReceiver mResultReceiver;
    private Place mPlace;
    private Location touchLocation;
    private String addressOutput;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        BottomSheetFragment bottomSheetFragment = BottomSheetFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map_bottom_sheets, bottomSheetFragment)
                .commit();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.makeMapDefaultSetting();

        // receive address at a location.
        mResultReceiver = new AddressResultReceiver(new Handler());

        declareSearch();

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
                if (mPlace == null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, DirectionActivityFragment.newInstance(null, 0, 0), Constant.DIRECTION_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
                } else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.root_layout, DirectionActivityFragment.newInstance(mPlace.getName().toString(), mPlace.getLatLng().latitude, mPlace.getLatLng().longitude), Constant.DIRECTION_FRAGMENT)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

    }

    // go to another fragment with marker place, when click a dialog
    public void goToDirectionFragment() {
        if (touchLocation != null && addressOutput != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, DirectionActivityFragment.newInstance(addressOutput, touchLocation.getLatitude(), touchLocation.getLongitude()), Constant.DIRECTION_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        // xóa marker cửa số đang mở trên map
        InfoWindow.closeAllInfoWindowsOn(getMapView());

        // hide navigation bar
        View decorView = getActivity().getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        clearMap();
        // reverse location into address
        // make search bar and bottom sheet contain address

        // draw marker
        touchLocation = new Location("touchLocation");
        touchLocation.setLatitude(p.getLatitude());
        touchLocation.setLongitude(p.getLongitude());

        setMarkerAtLocationWithDialog(touchLocation, Constant.MARKER, getActivity().getSupportFragmentManager());

        startIntentService(touchLocation);
        return true;
    }

    protected void startIntentService(Location touchLocation) {
        if (((MainActivity) getActivity()).getGoogleApiClient().isConnected()) {
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
         * Receives data sent from FetchAddressIntentService
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

    // display address in bottom sheet + add to search
    private void displayAddressOutput(String addressOutput) {
        this.addressOutput = addressOutput;
        Log.i(TAG, "displayAddressOutput: " + addressOutput);
        BottomSheetFragment bottomSheetFragment = (BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets);
        BottomSheetBehavior<View> bottomSheetBehavior = bottomSheetFragment.getBottomSheetBehavior();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            // add place details
            bottomSheetFragment.getPlaceName().setText(addressOutput);
            bottomSheetBehavior.setPeekHeight(369);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        EditText searchBar = (EditText) getActivity().findViewById(R.id.search_bar);
        searchBar.setText(addressOutput);
    }


    //    search bar
// khai bao listen cho thanh edittext, set text cho destination edittext
    private void declareSearch() {
        EditText searchBar = (EditText) getActivity().findViewById(R.id.search_bar);
        searchBar.setText(R.string.yourLocation);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call search activity
                openAutocompleteActivity(REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1);
            }
        });

    }

    private void openAutocompleteActivity(int code) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, code);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                 mPlace = PlaceAutocomplete.getPlace(getContext(), data);
                ((EditText) getActivity().findViewById(R.id.search_bar)).setText(mPlace.getName());

                ((BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets)).addPlaceToBottomSheet(mPlace);
//                // remove marker on the map, center at that point and add marker.
                clearMap();
                Location placeLocation = new Location("Test");
                placeLocation.setLatitude(mPlace.getLatLng().latitude);
                placeLocation.setLongitude(mPlace.getLatLng().longitude);
                setMarkerAtLocation(placeLocation, Constant.MARKER);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }

        }
    }
}
