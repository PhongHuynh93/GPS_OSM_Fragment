package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dhbk.android.gps_osm_fragment.R;

public class PrivateChatActivityFragment extends Fragment {
    public PrivateChatActivityFragment() {
        // Required empty public constructor
    }

    public static PrivateChatActivityFragment newInstance() {
        PrivateChatActivityFragment fragment = new PrivateChatActivityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_chat_activity, container, false);
    }

}
