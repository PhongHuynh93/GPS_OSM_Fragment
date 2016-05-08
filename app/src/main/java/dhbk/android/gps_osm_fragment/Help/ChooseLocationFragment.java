package dhbk.android.gps_osm_fragment.Help;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import dhbk.android.gps_osm_fragment.R;

// TODO: 5/8/16 build dialog fragment
public class ChooseLocationFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_action)
                .setItems(R.array.pick_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // TODO: 5/8/16 go to fragment direction

                                break;
                            default:
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
