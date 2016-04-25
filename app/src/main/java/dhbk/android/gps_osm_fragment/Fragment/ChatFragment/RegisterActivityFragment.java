package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterActivityFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMAIL = "param1";
    private static final String ARG_PASS = "param2";
    private static final String TAG = "RegisterFragment";

    private String mEmail;
    private String mPass;

    private OnFragmentInteractionListener mListener;

    public RegisterActivityFragment() {
        // Required empty public constructor
    }

    public static RegisterActivityFragment newInstance(String param1, String param2) {
        RegisterActivityFragment fragment = new RegisterActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, param1);
        args.putString(ARG_PASS, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_EMAIL);
            mPass = getArguments().getString(ARG_PASS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_activity, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText emailEdt = (EditText) getActivity().findViewById(R.id.email_register);
        final EditText passEdt = (EditText) getActivity().findViewById(R.id.pass_register);

        emailEdt.setText(mEmail);
        passEdt.setText(mPass);

        // dang ky tai khoan
        getActivity().findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dk voi email va pass
                ((MainActivity)getActivity()).getFirebaseRefer().createUser(emailEdt.getText().toString(), passEdt.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    // dk thành công
                    public void onSuccess(Map<String, Object> result) {
                        // TODO: 4/25/16 go to another layout (not addToBackStack)
                        Snackbar.make(getActivity().findViewById(R.id.register_coordinator), "Register Success", Snackbar.LENGTH_LONG)
                                .show();

                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        //  4/25/16 go to login because your account has already registered
                        Snackbar.make(getActivity().findViewById(R.id.register_coordinator), firebaseError.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("LOG IN", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                })
                                .show();
                    }
                });
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
