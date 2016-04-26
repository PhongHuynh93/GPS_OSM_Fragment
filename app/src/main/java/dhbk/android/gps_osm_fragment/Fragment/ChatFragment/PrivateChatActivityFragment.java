package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import dhbk.android.gps_osm_fragment.Help.Config;
import dhbk.android.gps_osm_fragment.Help.Nick;
import dhbk.android.gps_osm_fragment.R;

// TODO: 4/26/16 fix listview
public class PrivateChatActivityFragment extends Fragment {
    private FirebaseListAdapter<Nick> mAdapter;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ListView messagesView = (ListView) getActivity().findViewById(R.id.list_private_chat);

        Config.getFirebaseInitialize(getContext());
        Firebase ref = Config.getFirebaseReference().child("nickList");

        mAdapter = new FirebaseListAdapter<Nick>(getActivity(), Nick.class, android.R.layout.two_line_list_item, ref) {
            @Override
            protected void populateView(View view, Nick nick, int position) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(nick.getNick());
            }
        };
        messagesView.setAdapter(mAdapter);
    }
}
