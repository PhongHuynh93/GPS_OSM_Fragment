package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;

// TODO: 4/25/16 make animation when transform from fragment to fragment
public class ChatActivityFragment extends BaseFragment {
    public static final String TAG = "ChatActivityFragment";

//    private OnFragmentChatInteractionListener mListener;

    public ChatActivityFragment() {
        // Required empty public constructor
    }


    public static ChatActivityFragment newInstance() {
        ChatActivityFragment fragment = new ChatActivityFragment();
        return fragment;
    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentChatInteractionListener) {
//            mListener = (OnFragmentChatInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentChatInteractionListener");
//        }
//    }

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

        getActivity().findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // thay bang fragment login
                ((MainActivity)getActivity()).getFirebaseRefer().authWithPassword(emailEdt.getText().toString(), passEdt.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        ((MainActivity) getActivity()).getFirebaseRefer().child(retrieveSubString(emailEdt.getText().toString())).child("nick").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.root_layout, ChooseChatTypeFragment.newInstance(dataSnapshot.getValue(String.class)))
                                .addToBackStack(null)
                                .commit();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
//                        getActivity().getSupportFragmentManager()
//                                .beginTransaction()
//                                .replace(R.id.root_layout, ChooseChatTypeFragment.newInstance())
//                                .addToBackStack(null)
//                                .commit();
                    }
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // there was an error
                        Snackbar.make(getActivity().findViewById(R.id.login_coordinator), firebaseError.getMessage(), Snackbar.LENGTH_LONG).show();


                    }
                });

            }
        });
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    public interface OnFragmentChatInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
}
