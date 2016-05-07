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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import dhbk.android.gps_osm_fragment.Fragment.BaseFragment;
import dhbk.android.gps_osm_fragment.Help.DBConnection;
import dhbk.android.gps_osm_fragment.Help.ListImageAdapter;
import dhbk.android.gps_osm_fragment.Help.RecyclerViewItemClickListner;
import dhbk.android.gps_osm_fragment.Help.RouteInfo;
import dhbk.android.gps_osm_fragment.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTripInfoActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTripInfoActivity extends BaseFragment {
    private static final String ARG_ID = "param1";

    private String id;
    private static final String GALLERY_FOLDER = "tripGallery";
    Button btnSave;
    EditText nameRoute, descript;
    RecyclerView recyclerView;
    ArrayList<String> arraySelectedFile;
    String route;
    RouteInfo routeInfo;


    public EditTripInfoActivity() {
        // Required empty public constructor
    }


    public static EditTripInfoActivity newInstance(String id) {
        EditTripInfoActivity fragment = new EditTripInfoActivity();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_save_route, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnSave = (Button) getActivity().findViewById(R.id.btnSave);
        nameRoute = (EditText) getActivity().findViewById(R.id.edtNameRoute);
        descript = (EditText) getActivity().findViewById(R.id.edtDescription);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycleView);
        arraySelectedFile = new ArrayList<String>();

        routeInfo = getTripInfo(id);
        nameRoute.setText(routeInfo.getName());
        descript.setText(routeInfo.getDescription());
        initRecycleView();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
                getActivity().getSupportFragmentManager().popBackStack();

//                Intent shareAcvitivy = new Intent(getApplicationContext(), SavedListTripActivity.class);
//                startActivity(shareAcvitivy);
            }
        });
    }


    private RouteInfo getTripInfo(String id) {
        DBConnection dbConnection = new DBConnection(getContext());
        RouteInfo routeInfo = dbConnection.getTripInfo(id);
        return routeInfo;
    }

    private List<File> getSavedImage() {
        // lay danh sach hinh đã chọn
        String imgList = routeInfo.getImg();
        Gson gson = new Gson();
        String[] img = gson.fromJson(imgList, String[].class);
        List<File> list = new ArrayList<File>();
        for (String each : img) {
            String path = "/storage/emulated/0/DCIM/Camera/tripGallery/";
            File file = new File(path + each);
            list.add(file);
        }
        return list;
    }

    private void initRecycleView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new ListImageAdapter(sortFile(), getSavedImage());
        recyclerView.setAdapter(imageAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListner(getContext(), new RecyclerViewItemClickListner.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                        if (viewHolder.itemView.getAlpha() == (float) 0.5) {
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

    private void update() {
        String name = nameRoute.getText().toString();
        String description = descript.getText().toString();
        if (arraySelectedFile.size() > 0) {
            Gson gson = new Gson();
            String img = gson.toJson(arraySelectedFile);
            routeInfo.setImg(img);
        }

        DBConnection dbConnection = new DBConnection(getContext());
        routeInfo.setName(name);
        routeInfo.setDescription(description);


        dbConnection.updateInfO(routeInfo);


    }
}
