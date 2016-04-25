package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PublicChatActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PublicChatActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicChatActivityFragment extends Fragment {
    private static final String ARG_NICK = "nick";

    private String mNick;


    public PublicChatActivityFragment() {
    }

    public static PublicChatActivityFragment newInstance(String param1) {
        PublicChatActivityFragment fragment = new PublicChatActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NICK, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNick = getArguments().getString(ARG_NICK);
        }
    }

    // TODO: 4/25/16 make layout recycler view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public_chat_activity, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


}
