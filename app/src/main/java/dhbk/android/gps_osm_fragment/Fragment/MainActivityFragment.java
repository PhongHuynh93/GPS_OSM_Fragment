package dhbk.android.gps_osm_fragment.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

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
        return rootView;
    }

    // listen auto search bar
    @Override
    public void onStart() {
        super.onStart();
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        Log.i("MA", "onStart: " + autocompleteFragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: 4/23/16 add place to bottom sheet
                ((BottomSheetFragment) getChildFragmentManager().findFragmentById(R.id.map_bottom_sheets)).addPlaceToBottomSheet(place);
                // TODO: 4/23/16 add marker to that place
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

}
