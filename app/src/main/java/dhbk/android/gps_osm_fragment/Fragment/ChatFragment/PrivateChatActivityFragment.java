package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.Help.Config;
import dhbk.android.gps_osm_fragment.Help.Nick;
import dhbk.android.gps_osm_fragment.R;

public class PrivateChatActivityFragment extends BaseFragment {
    public static final String TAG = "PrivateChatActivityFragment";
    private static final String ARG_MYNICK = "my-nick";
    private FirebaseListAdapter<Nick> mAdapter;
    private String mNickUser;
    private String mEmailUser;
    private String mMyNick;

    public PrivateChatActivityFragment() {
        // Required empty public constructor
    }

    public static PrivateChatActivityFragment newInstance(String myNick) {
        PrivateChatActivityFragment fragment = new PrivateChatActivityFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_MYNICK, myNick);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMyNick = getArguments().getString(ARG_MYNICK);
        }
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Private Chat");

        final ListView messagesView = (ListView) getActivity().findViewById(R.id.list_private_chat);

        Config.getFirebaseInitialize(getContext());
        Firebase ref = Config.getFirebaseReference().child("nickList");

        // get nick, email from from database

        mAdapter = new FirebaseListAdapter<Nick>(getActivity(), Nick.class, R.layout.list_chat_private, ref) {
            @Override
            protected void populateView(View view, Nick nick, int position) {
                // TODO: 5/1/16 add imageview (user on/ off)
                mNickUser = nick.getNick();
                mEmailUser = nick.getEmail();
                String emailUserLong = mEmailUser + "@gmail.com";
                ((TextView) view.findViewById(R.id.message)).setText(mNickUser);
                ((TextView) view.findViewById(R.id.email)).setText(emailUserLong);
            }
        };
        messagesView.setAdapter(mAdapter);

        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nickFriend = ((TextView)view.findViewById(R.id.message)).getText().toString();

                String bothNick = mergeNick(mMyNick, nickFriend);
                final PrivateChatEachUserFragment eachUserFragment =
                        PrivateChatEachUserFragment.newInstance(bothNick, nickFriend);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, eachUserFragment)
                        .addToBackStack(null)
                        .commit();


            }
        });
    }
}
