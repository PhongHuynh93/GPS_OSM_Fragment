package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.transition.Fade;
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
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.Help.DetailsTransition;
import dhbk.android.gps_osm_fragment.R;

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

        // TODO: 5/8/16 read share ref to write email, pass
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String email = sharedPref.getString(Constant.KEY_PREF_EMAIL, "Email@gmail.com");
        final String pass = sharedPref.getString(Constant.KEY_PREF_PASS, "123");

        emailEdt.setText(email);
        passEdt.setText(pass);

        getActivity().findViewById(R.id.button_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivityFragment registerActivityFragment = RegisterActivityFragment.newInstance(emailEdt.getText().toString(), passEdt.getText().toString());
                // thay bang fragment register
                // Note that we need the API version check here because the actual transition classes (e.g. Fade)
                // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
                // ARE available in the support library (though they don't do anything on API < 21)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    registerActivityFragment.setSharedElementEnterTransition(new DetailsTransition());
                    registerActivityFragment.setEnterTransition(new Fade());
                    setExitTransition(new Fade());
                    registerActivityFragment.setSharedElementReturnTransition(new DetailsTransition());
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(getActivity().findViewById(R.id.button_signup), "sign_up")
                        .replace(R.id.root_layout, registerActivityFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        getActivity().findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress;
                progress = ProgressDialog.show(getContext(), "Login.....",
                        "Please wait!!!!", true);
                // thay bang fragment login
                ((MainActivity) getActivity()).getFirebaseRefer().authWithPassword(emailEdt.getText().toString(), passEdt.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        progress.dismiss();
                        // TODO: 5/8/16 add to share pref
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(Constant.KEY_PREF_EMAIL, emailEdt.getText().toString());
                        editor.putString(Constant.KEY_PREF_PASS, passEdt.getText().toString());
                        editor.apply();

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
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        progress.dismiss();
                        // there was an error
                        Snackbar.make(getActivity().findViewById(R.id.login_coordinator), firebaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

}
