package dhbk.android.gps_osm_fragment.Help;

import android.content.Context;

import com.firebase.client.Firebase;

public class Config {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://test-fb-phong093.firebaseio.com";
    private static final String FIREBASE_CHILD = "chat";

    public static void getFirebaseInitialize(Context context) {
        Firebase.setAndroidContext(context);
    }

    public static Firebase getFirebaseReference(){
       return new Firebase(FIREBASE_URL);
    }

}
