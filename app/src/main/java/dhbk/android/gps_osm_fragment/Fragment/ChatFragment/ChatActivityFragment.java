package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatActivityFragment.OnFragmentChatInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatActivityFragment extends BaseFragment {
    public static final String TAG = "ChatActivityFragment";

    private OnFragmentChatInteractionListener mListener;

    public ChatActivityFragment() {
        // Required empty public constructor
    }


    public static ChatActivityFragment newInstance() {
        ChatActivityFragment fragment = new ChatActivityFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChatInteractionListener) {
            mListener = (OnFragmentChatInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentChatInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText emailEdt = (EditText) getActivity().findViewById(R.id.email_login);
        final EditText passEdt = (EditText) getActivity().findViewById(R.id.password_login);
        getActivity().findViewById(R.id.button_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // thay bang fragment register
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.root_layout, RegisterActivityFragment.newInstance(emailEdt.getText().toString(), passEdt.getText().toString()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // TODO: 4/25/16 add login
        getActivity().findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // thay bang fragment register
                ((MainActivity)getActivity()).getFirebaseRefer().authWithPassword(emailEdt.getText().toString(), passEdt.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // TODO: 4/25/16 go to fragment private chat or public chat
                        
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        // TODO: 4/25/16 make snackbar
                    }
                });

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentChatInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
