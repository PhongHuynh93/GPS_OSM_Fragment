package dhbk.android.gps_osm_fragment.Fragment.DirectionFragment;

import android.content.Intent;
import android.os.Bundle;
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

import org.osmdroid.views.MapView;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;

public class DirectionActivityFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DESTPLACE = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_1 = 1;
    private static final int REQUEST_CODE_AUTOCOMPLETE_EDITTEXT_2 = 2;

    // TODO: Rename and change types of parameters
    private String mDesplace;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;
    private Toolbar mToolbar;
    private MapView mMapView;
    private EditText mStartPoint;
    private EditText mEndPoint;

    public DirectionActivityFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DirectionActivityFragment newInstance(String desPlace) {
        DirectionActivityFragment fragment = new DirectionActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESTPLACE, desPlace);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDesplace = getArguments().getString(ARG_DESTPLACE);
//            mParam2 = getArguments().getString(ARG_PARAM2);
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
        if (mDesplace != null) {
            mEndPoint.setText(mDesplace);
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
}
