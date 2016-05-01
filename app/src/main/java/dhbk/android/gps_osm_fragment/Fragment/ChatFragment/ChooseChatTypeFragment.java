package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseChatTypeFragment extends Fragment {

    private static final String ARGUMENT_NICK = "nick";
    private String mNick;

    public static ChooseChatTypeFragment newInstance(String nick) {
        ChooseChatTypeFragment chooseChatTypeFragment = new ChooseChatTypeFragment();
        final Bundle args = new Bundle();
        args.putString(ARGUMENT_NICK, nick);
        chooseChatTypeFragment.setArguments(args);
        return chooseChatTypeFragment;
    }

    public ChooseChatTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNick = getArguments().getString(ARGUMENT_NICK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_chat_type, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TextView nick = (TextView) getActivity().findViewById(R.id.nick_choose_type);
        nick.setText("Hi, " + mNick);

        // make public chat
        getActivity().findViewById(R.id.button_public_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, PublicChatActivityFragment.newInstance(mNick))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // make private chat
        getActivity().findViewById(R.id.button_private_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, PrivateChatActivityFragment.newInstance(mNick))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
