package dhbk.android.gps_osm_fragment.Fragment.DirectionFragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.osmdroid.views.MapView;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

public class DirectionActivityFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DESTPLACE = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1 = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_2 = 2;
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

    // TODO: Rename and change types of parameters
    private String mDesplaceName;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;
    private Toolbar mToolbar;
    private MapView mMapView;
    private EditText mStartPoint;
    private EditText mEndPoint;
    private BottomBar mBottomBar;
    private double mLatitudeDesplace;
    private double mLongitudeDesplace;
    private Location mStartPlace;
    private Location mDestinationPlace;

    public DirectionActivityFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DirectionActivityFragment newInstance(String desPlace, double latitude, double longitude) {
        DirectionActivityFragment fragment = new DirectionActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESTPLACE, desPlace);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDesplaceName = getArguments().getString(ARG_DESTPLACE);
            mLatitudeDesplace = getArguments().getDouble(ARG_LATITUDE);
            mLongitudeDesplace = getArguments().getDouble(ARG_LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_direction_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        makeMapDefaultSetting();
        mMapView = getMapView();
        declareSearch();

        // set dest place
        if (mDesplaceName != null) {
            Location place = new Location("destinationPlace");
            place.setLatitude(mLatitudeDesplace);
            place.setLongitude(mLongitudeDesplace);
            setDestinationPlace(place);
        }

        // set startplace
        if (mStartPlace == null) {
            Location place = getLocation();
            setStartPlace(place);

        }

        // TODO: 5/3/16 declareBottomNavigation
        declareBottomNavigation(savedInstanceState);
    }

    // phong - khung chứa 4 icons phương tiện.
    private void declareBottomNavigation(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach(getActivity().findViewById(R.id.map), savedInstanceState);
        mBottomBar.noTopOffset();

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemRun) {

                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot1));
                    drawNewPath(Constant.MODE_RUN);

                } else if (menuItemId == R.id.bottomBarItemBike) {

                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot2));
                    drawNewPath(Constant.MODE_BIKE);


                } else if (menuItemId == R.id.bottomBarItemBus) {

                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot3));
                    drawNewPath(Constant.MODE_BUS);


                } else if (menuItemId == R.id.bottomBarItemCar) {

                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot4));
                    drawNewPath(Constant.MODE_CAR);

                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemRun) {

                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot1));
                    drawNewPath(Constant.MODE_RUN);

                } else if (menuItemId == R.id.bottomBarItemBike) {
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot2));
                    drawNewPath(Constant.MODE_BIKE);


                } else if (menuItemId == R.id.bottomBarItemBus) {
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot3));
                    drawNewPath(Constant.MODE_BUS);


                } else if (menuItemId == R.id.bottomBarItemCar) {
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.bot4));
                    drawNewPath(Constant.MODE_CAR);
                }
            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, "#795548");//0xFF5D4037);
        mBottomBar.mapColorForTab(1, "#7B1FA2");//"#7B1FA2");
        mBottomBar.mapColorForTab(2, "#FF5252");//"#FF5252");
        mBottomBar.mapColorForTab(3, "#FF9800");//"#FF9800"  );
    }

    // phong draw path depends on current tab
    private void drawNewPathOnTab() {
        switch (mBottomBar.getCurrentTabPosition()) {
            case 0:
                drawNewPath(Constant.MODE_RUN);
                break;
            case 1:
                drawNewPath(Constant.MODE_BIKE);
                break;
            case 2:
                drawNewPath(Constant.MODE_BUS);
                break;
            case 3:
                drawNewPath(Constant.MODE_CAR);
                break;
            default:
        }
    }

    // xóa overlay + vẽ + phóng to
    private void drawNewPath(String mode) {
        if (mStartPlace != null && mDestinationPlace != null) {
            mMapView.getOverlays().clear();
            drawPathOSMWithInstruction(mStartPlace, mDestinationPlace, mode, Constant.WIDTH_LINE);
        }
    }

    // khai bao listen cho thanh edittext, set text cho destination edittext
    private void declareSearch() {
        mStartPoint = (EditText) getActivity().findViewById(R.id.start_point);
        mStartPoint.setText(R.string.yourLocation);
        mStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call search activity
                openAutocompleteActivity(REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1);
            }
        });

        mEndPoint = (EditText) getActivity().findViewById(R.id.end_point);
        if (mDesplaceName != null) {
            mEndPoint.setText(mDesplaceName);
        }

        mEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call search activity
                openAutocompleteActivity(REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_2);
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


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setDestinationPlace(Location destinationPlace) {
        this.mDestinationPlace = destinationPlace;
    }

    public void setStartPlace(Location startPlace) {
        this.mStartPlace = startPlace;
    }
}
