package dhbk.android.gps_osm_fragment.Fragment.ShareFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dhbk.android.gps_osm_fragment.Help.DBConnection;
import dhbk.android.gps_osm_fragment.Help.RecyclerViewItemClickListner;
import dhbk.android.gps_osm_fragment.Help.RouteInfo;
import dhbk.android.gps_osm_fragment.Help.SavedListAdapter;
import dhbk.android.gps_osm_fragment.R;

public class SavedListTripActivityFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    RecyclerView recyclerView;
    int itemId;
    List<RouteInfo> savedList;

    public SavedListTripActivityFragment() {
        // Required empty public constructor
    }


    public static SavedListTripActivityFragment newInstance() {
        SavedListTripActivityFragment fragment = new SavedListTripActivityFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_list_trip_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.saved_list);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        DBConnection db = new DBConnection(getContext());
        savedList = db.getAllList();
        RecyclerView.Adapter savedListAdapter = new SavedListAdapter(savedList);
        recyclerView.setAdapter(savedListAdapter);
        // Toast.makeText(SavedListTripActivity.this,  savedList.get(2).getTime(), Toast.LENGTH_SHORT).show();

        recyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListner(getContext(), new RecyclerViewItemClickListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Toast.makeText(SavedListTripActivity.this, action, Toast.LENGTH_SHORT).show();
                        itemId = position;
                    }



                })
        );

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getItemId();

        if (position == 0) {
            //Show map
            String route = savedList.get(position).getRoute();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, MapSampleActivity.newInstance(route))
                    .addToBackStack(null)
                    .commit();
//            Intent sampleMapActivity = new Intent(getContext(), MapSampleActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("route", route);
//            sampleMapActivity.putExtras(bundle);
//            startActivity(sampleMapActivity);
        } else {
            // TODO: 5/4/16
            //edit
            String id = savedList.get(itemId).getId();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, EditTripInfoActivity.newInstance(id))
                    .addToBackStack(null)
                    .commit();
//            Intent editActivity = new Intent(this, EditTripInfoActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("id", id);
//            editActivity.putExtras(bundle);
//            startActivity(editActivity);
        }
        return super.onContextItemSelected(item);
    }
}
