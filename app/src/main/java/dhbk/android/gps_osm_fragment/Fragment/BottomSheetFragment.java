package dhbk.android.gps_osm_fragment.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Help.ImagePagerAdapter;
import dhbk.android.gps_osm_fragment.Help.PhotoTask;
import dhbk.android.gps_osm_fragment.R;

public class BottomSheetFragment extends Fragment {
    public static final String TAG = "BottomSheetFragment";
    public static final String LIFE = "Vòng đời Fragment";
    private View mRootView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    public static ArrayList<PhotoTask.AttributedPhoto> mArrayListAttributedPhoto;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        return mRootView;
    }

    // behavior callback
    // TODO: 4/23/16 when place auto return, add state to this
    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior = BottomSheetBehavior.from(mRootView);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // add place to this
    public  void addPlaceToBottomSheet(Place place) {
        final TextView placeName = (TextView) mRootView.findViewById(R.id.place_name);
        final TextView addressName = (TextView) mRootView.findViewById(R.id.address_name);
        final TextView phoneName = (TextView) mRootView.findViewById(R.id.phone_name);
        final TextView websiteName = (TextView) mRootView.findViewById(R.id.website_name);
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            // add place details
            if (place.getName() != null) {
                placeName.setText(place.getName());
            }
            if (place.getAddress() != null) {
                addressName.setText(place.getAddress());
            }
            if (place.getPhoneNumber() != null) {
                phoneName.setText(place.getPhoneNumber());
            }
            if (place.getWebsiteUri() != null) {
                websiteName.setText(place.getWebsiteUri() + "");
            }

            // add place photos
            addPhotoToBottomSheet(place.getId(), ((MainActivity)getActivity()).getGoogleApiClient());

            mBottomSheetBehavior.setPeekHeight(369);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    private void addPhotoToBottomSheet(String id, GoogleApiClient googleApiClient) {
        final ViewPager viewPager = (ViewPager) mRootView.findViewById(R.id.imageSlider);

        new PhotoTask(viewPager.getWidth(), viewPager.getHeight()) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
            }

            @Override
            protected void onPostExecute(ArrayList<AttributedPhoto> attributedPhotos) {
                // TODO: 4/24/16 remove progress dialog
                // load image on viewpager, remove old images and add new ones.
                if (attributedPhotos.size() > 0) {
                    mArrayListAttributedPhoto = attributedPhotos;
                    ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getChildFragmentManager(), attributedPhotos.size());
                    viewPager.setAdapter(imagePagerAdapter);
                }
            }
        }.execute(new PhotoTask.MyTaskParams(id, ((MainActivity)getActivity()).getGoogleApiClient()));
    }


}
