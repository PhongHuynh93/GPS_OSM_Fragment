package dhbk.android.gps_osm_fragment;

import android.content.Context;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment{

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    // pass layout to parent fragment so that it can render child layout
    public MainActivityFragment() {
        super(R.layout.fragment_main);
    }

    // make interface to activity
    // TODO: 4/22/16 make interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
