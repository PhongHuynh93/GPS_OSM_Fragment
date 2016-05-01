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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public PrivateChatEachUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrivateChatEachUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrivateChatEachUserFragment newInstance(String param1, String param2) {
        PrivateChatEachUserFragment fragment = new PrivateChatEachUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_private_chat_each_user, container, false);
    }

}
