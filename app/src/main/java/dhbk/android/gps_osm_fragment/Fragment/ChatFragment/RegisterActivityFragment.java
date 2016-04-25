package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.R;


public class RegisterActivityFragment extends BaseFragment {
    private static final String ARG_EMAIL = "param1";
    private static final String ARG_PASS = "param2";
    private static final String TAG = "RegisterFragment";

    private String mEmail;
    private String mPass;


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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText emailEdt = (EditText) getActivity().findViewById(R.id.email_register);
        final EditText passEdt = (EditText) getActivity().findViewById(R.id.pass_register);
        final EditText nickEdt = (EditText) getActivity().findViewById(R.id.nick_register);

        emailEdt.setText(mEmail);
        passEdt.setText(mPass);

        // dang ky tai khoan
        getActivity().findViewById(R.id.button_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nickEdt.getText().toString().equals("")) {
                    Snackbar.make(getActivity().findViewById(R.id.register_coordinator), "Please enter nickname", Snackbar.LENGTH_LONG).show();
                } else {

                    // dk voi email va pass
                    ((MainActivity) getActivity()).getFirebaseRefer().createUser(emailEdt.getText().toString(), passEdt.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        // dk thành công
                        public void onSuccess(Map<String, Object> result) {
                            Map<String, Object> nickMap = new HashMap<>();
                            // root/$email$/nick
                            nickMap.put("nick", nickEdt.getText().toString());
                            String childSubString = retrieveSubString(emailEdt.getText().toString());
                            ((MainActivity)getActivity()).getFirebaseRefer().child(childSubString).updateChildren(nickMap);
                            // root/nickList/@@@@/
                            Map<String, String> nickList = new HashMap<>();
                            nickList.put("nick", nickEdt.getText().toString());
                            ((MainActivity)getActivity()).getFirebaseRefer().child("nickList").push().setValue(nickList);


                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.root_layout, ChooseChatTypeFragment.newInstance(nickEdt.getText().toString()))
                                    .commit();
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

            }
        });
    }
}