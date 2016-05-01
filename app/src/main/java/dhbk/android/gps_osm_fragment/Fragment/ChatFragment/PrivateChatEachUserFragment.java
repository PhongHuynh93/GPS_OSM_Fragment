package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivateChatEachUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: 5/1/16 make layout for private chat for each user.
public class PrivateChatEachUserFragment extends Fragment {
    private static final String ARG_NICK = "param1";
    private static final String ARG_EMAIL = "param2";

    private String mNickUser;
    private String mEmailUser;


    public PrivateChatEachUserFragment() {
        // Required empty public constructor
    }

    public static PrivateChatEachUserFragment newInstance(String param1, String param2) {
        PrivateChatEachUserFragment fragment = new PrivateChatEachUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NICK, param1);
        args.putString(ARG_EMAIL, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNickUser = getArguments().getString(ARG_NICK);
            mEmailUser = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_chat_each_user, container, false);
    }

}
