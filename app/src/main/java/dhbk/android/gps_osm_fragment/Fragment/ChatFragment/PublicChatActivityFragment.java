package dhbk.android.gps_osm_fragment.Fragment.ChatFragment;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import dhbk.android.gps_osm_fragment.Help.Chat;
import dhbk.android.gps_osm_fragment.Help.ChatAdapter;
import dhbk.android.gps_osm_fragment.Help.Config;
import dhbk.android.gps_osm_fragment.R;


public class PublicChatActivityFragment extends Fragment {
    private static final String ARG_NICK = "nick";

    private String mNick;
    private ArrayList<Chat> mChatList;
    private String mIdDevice;
    private Firebase mRefFB;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

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
        Config.getFirebaseInitialize(getActivity());
        // di vao phong chat public
        mRefFB = Config.getFirebaseReference().child(Config.FIREBASE_CHILD_PUBLIC_CHAT);
        mChatList = new ArrayList<>();
        mIdDevice = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public_chat_activity, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Public Chat");

        chatAdapter = new ChatAdapter(mChatList, mIdDevice);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(chatAdapter);

        getActivity().findViewById(R.id.button_sent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMessageToSent();
            }
        });
        getChatMessages();

    }

    private void getMessageToSent(){
        EditText editText = (EditText) getActivity().findViewById(R.id.edit_txt_message);
        String message = editText.getText().toString();
        if(!message.isEmpty())
            mRefFB.push().setValue(new Chat(message, mIdDevice));
        editText.setText("");
    }

    private void getChatMessages(){

        mRefFB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    //Firebase - Convierte una respuesta en un objeto de tipo Chat
                    Chat model = dataSnapshot.getValue(Chat.class);
                    mChatList.add(model);
                    recyclerView.scrollToPosition(mChatList.size() - 1);
                    chatAdapter.notifyItemInserted(mChatList.size() - 1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                firebaseError.getMessage();
            }
        });
    }
}
