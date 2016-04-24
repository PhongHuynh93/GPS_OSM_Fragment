package dhbk.android.gps_osm_fragment.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment {


    public static MainActivityFragment newInstance() {
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        return mainActivityFragment;
    }

    // pass layout to parent fragment so that it can render child layout
    public MainActivityFragment() {
    }

    // make interface to activity
    // TODO: 4/22/16 make interface


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        super.setRootView(rootView); // set parent rootview
        super.makeMapDefaultSetting();

        final SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Log.i("MA", "onStart: " + autocompleteFragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                ((BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets)).addPlaceToBottomSheet(place);
                // TODO: 4/23/16 add marker to that place
            }

            @Override
            public void onError(Status status) {

            }
        });
        // TODO: 4/24/16 listen floating button
        final FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab_my_location);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).getGoogleApiClient().isConnected()) {
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
        final FloatingActionButton floatingActionButtonDirection = (FloatingActionButton) rootView.findViewById(R.id.fab_direction);
        floatingActionButtonDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 4/24/16 hiện thực interface để replace fragment
            }
        });

        return rootView;
    }

    public interface MainActivityFragmentInterface {
        void onClickDirection();
    }

}
