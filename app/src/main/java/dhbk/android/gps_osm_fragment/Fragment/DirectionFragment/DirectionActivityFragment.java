package dhbk.android.gps_osm_fragment.Fragment.DirectionFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

// TODO: 5/5/16 remove bottom bar
public class DirectionActivityFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DESTPLACE = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1 = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_2 = 2;
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";

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
    private DirectionInterface mListener;

    private String language = Constant.LAN_EN;

    public void setLanguage(String language) {
        this.language = language;
    }

    public DirectionActivityFragment() {
        // Required empty public constructor
    }

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

        declareBottomNavigation(savedInstanceState);

        getActivity().findViewById(R.id.fab_my_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location place = getLocation();
                setStartPlace(place);
                mStartPoint.setText(R.string.yourLocation);
                drawNewPathOnTab();
            }
        });



    }

    // phong - khung chứa 4 icons phương tiện.
    private void declareBottomNavigation(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attach(getActivity().findViewById(R.id.map), savedInstanceState);
        mBottomBar.noTopOffset();

        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemRun) {

                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot1));
                    drawNewPath(Constant.MODE_RUN);

                } else if (menuItemId == R.id.bottomBarItemBike) {

                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot2));
                    drawNewPath(Constant.MODE_BIKE);


                } else if (menuItemId == R.id.bottomBarItemBus) {

                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot3));
                    drawNewPath(Constant.MODE_BUS);


                } else if (menuItemId == R.id.bottomBarItemCar) {

                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot4));
                    drawNewPath(Constant.MODE_CAR);

                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarItemRun) {

                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot1));
                    drawNewPath(Constant.MODE_RUN);

                } else if (menuItemId == R.id.bottomBarItemBike) {
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot2));
                    drawNewPath(Constant.MODE_BIKE);


                } else if (menuItemId == R.id.bottomBarItemBus) {
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot3));
                    drawNewPath(Constant.MODE_BUS);


                } else if (menuItemId == R.id.bottomBarItemCar) {
                    mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bot4));
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
            drawPathOSMWithInstruction(mStartPlace, mDestinationPlace, mode, Constant.WIDTH_LINE, language);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mStartPoint.setText(place.getName());
                // set startPlace
                Location startPlace = new Location("location");
                startPlace.setLatitude(place.getLatLng().latitude);
                startPlace.setLongitude(place.getLatLng().longitude);
                setStartPlace(startPlace);

                //drawpath
                drawNewPathOnTab();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                mEndPoint.setText(place.getName());

                // set dest
                Location destPlace = new Location("location");
                destPlace.setLatitude(place.getLatLng().latitude);
                destPlace.setLongitude(place.getLatLng().longitude);
                setDestinationPlace(destPlace);

                // draw path
                drawNewPathOnTab();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    // tạo interface và gỡ interface(nếu là listen thì gỡ ra)
    public interface DirectionInterface {
        void navChooseLanguage(String language);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DirectionInterface) {
            mListener = (DirectionInterface) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnRageComicSelected.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

    //     tap để đóng cửa sổ lại
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return true;
    }

//    @Override
//    public boolean singleTapConfirmedHelper(GeoPoint p) {
//        // xóa marker cửa số đang mở trên map
//        InfoWindow.closeAllInfoWindowsOn(getMapView());
//
//        // hide navigation bar
//        View decorView = getActivity().getWindow().getDecorView();
//        // Hide both the navigation bar and the status bar.
//        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        // a general rule, you should design your app to hide the status bar whenever you
//        // hide the navigation bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//        return true;
//    }

    public void setDestinationPlace(Location destinationPlace) {
        this.mDestinationPlace = destinationPlace;
    }

    public void setStartPlace(Location startPlace) {
        this.mStartPlace = startPlace;
    }
}
