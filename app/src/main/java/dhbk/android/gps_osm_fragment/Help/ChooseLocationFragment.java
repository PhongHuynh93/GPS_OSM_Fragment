package dhbk.android.gps_osm_fragment.Help;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import dhbk.android.gps_osm_fragment.Fragment.DirectionFragment.MainActivityFragment;
import dhbk.android.gps_osm_fragment.R;

public class ChooseLocationFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_action)
                .setIcon(R.drawable.ic_place_blue_24dp)
                .setItems(R.array.pick_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.root_layout);
                                if (fragment instanceof MainActivityFragment) {
                                    MainActivityFragment mainActivityFragment = (MainActivityFragment) fragment;
                                    mainActivityFragment.goToDirectionFragment();
                                }
                                break;
                            default:
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
