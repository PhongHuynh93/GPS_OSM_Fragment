package dhbk.android.gps_osm_fragment.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;

import dhbk.android.gps_osm_fragment.Help.Config;
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
    private Firebase mFirebaseReference;
    private TextView txtEmail, txtPassword;
    private CallbackManager callbackManager;

    public ChatActivityFragment() {
        // Required empty public constructor
    }


    public static ChatActivityFragment newInstance() {
        ChatActivityFragment fragment = new ChatActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.getFirebaseInitialize(getActivity());
        mFirebaseReference = Config.getFirebaseReference();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) getActivity().findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentChatInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
