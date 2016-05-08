package dhbk.android.gps_osm_fragment.Fragment.ShareFragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dhbk.android.gps_osm_fragment.Help.DBConnection;
import dhbk.android.gps_osm_fragment.Help.ListImageAdapter;
import dhbk.android.gps_osm_fragment.Help.RecyclerViewItemClickListner;
import dhbk.android.gps_osm_fragment.Help.RouteInfo;
import dhbk.android.gps_osm_fragment.R;

public class SaveRouteActivityFragment extends Fragment {
    private static final String ARG_ROUTE = "route";
    private static final String ARG_PARAM2 = "param2";

    private String route;

    private static final String GALLERY_FOLDER = "tripGallery";
    Button btnSave;
    EditText nameRoute, descript;
    RecyclerView recyclerView;
    ArrayList<String> arraySelectedFile;

    public SaveRouteActivityFragment() {
        // Required empty public constructor
    }

    public static SaveRouteActivityFragment newInstance(ArrayList<GeoPoint> route) {
        SaveRouteActivityFragment fragment = new SaveRouteActivityFragment();

        Gson gson = new Gson();
        String routeJSON = gson.toJson(route);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ROUTE, routeJSON);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = getArguments().getString(ARG_ROUTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_route_activity, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnSave = (Button) getActivity().findViewById(R.id.btnSave);
        nameRoute = (EditText) getActivity().findViewById(R.id.edtNameRoute);
        descript = (EditText) getActivity().findViewById(R.id.edtDescription);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycleView);
        arraySelectedFile = new ArrayList<String>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new ListImageAdapter(sortFile(), null);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListner(getContext(), new RecyclerViewItemClickListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                        if (viewHolder.itemView.getAlpha() == 0.5) {
                            String deselectedFileName = getSelectedFileName(position);
                            arraySelectedFile.remove(deselectedFileName);
                            viewHolder.itemView.setAlpha((float) 1.0); // deselect item
                        } else {
                            String selectedFileName = getSelectedFileName(position);
                            arraySelectedFile.add(selectedFileName);
                            viewHolder.itemView.setAlpha((float) 0.5); //select item
                        }

                    }


                })
        );
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
//                Intent shareAcvitivy = new Intent(getContext(), ShareActivity.class);
//                startActivity(shareAcvitivy);

                // pop fragment
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public String getSelectedFileName(int position) {
        List<File> listFile = sortFile();
        String name = listFile.get(position).getName();
        return name;
    }

    private List<File> sortFile() {

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera"); //path to folder gallery
        File galleryFoler = new File(storageDirectory, GALLERY_FOLDER); // prepare create folder
        if (!galleryFoler.exists()) {
            galleryFoler.mkdirs(); //create folder
        }

        File[] files = galleryFoler.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return Long.valueOf(rhs.lastModified()).compareTo(Long.valueOf(lhs.lastModified()));
            }
        });
        List<File> list = Arrays.asList(files);
        return list;
    }

    private void saveData() {

        String name = nameRoute.getText().toString();
        String description = descript.getText().toString();
        Gson gson = new Gson();
        String img = gson.toJson(arraySelectedFile);
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String time = df.format(dateobj);
        DBConnection dbConnection = new DBConnection(getContext());
        dbConnection.addTrip(new RouteInfo(name, description, img, route, time));
    }
}
