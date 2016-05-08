package dhbk.android.gps_osm_fragment.Help;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dhbk.android.gps_osm_fragment.R;

public class SavedListAdapter extends RecyclerView.Adapter<SavedListAdapter.ViewHolder> {
    private static final String root = "file:///sdcard/" + Environment.DIRECTORY_DCIM + "/Camera/tripGallery/";

    private List<RouteInfo> mListRoute;
    private Context mContext;

    public SavedListAdapter(List<RouteInfo> listRoute) {
        mListRoute = listRoute;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String imgList = mListRoute.get(position).getImg();
        String imgName = root + getFirstImage(imgList);
        String name = mListRoute.get(position).getName();
        String date = mListRoute.get(position).getTime();
        String descript = mListRoute.get(position).getDescription();


        ImageView imgView = holder.getImageView();
        TextView nameView = holder.getTxtNameView();
        TextView dateView = holder.getTxtDateView();
        TextView descriptView = holder.getTxtDescriptView();
        RecyclerView listImgView = holder.getListImgView();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 1, LinearLayoutManager.HORIZONTAL, false);
        listImgView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new ListImageAdapter(getFile(imgList), null);
        listImgView.setAdapter(imageAdapter);


        Picasso.with(imgView.getContext()).load(imgName).resize(120, 120).into(imgView);
        nameView.setText(name);
        dateView.setText(date);
        descriptView.setText(descript);

    }

    private List<File> getFile(String imgList) {
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

    private String getFirstImage(String imgList) {
        Gson gson = new Gson();
        String[] img = gson.fromJson(imgList, String[].class);
        return img[0];
    }

    @Override
    public int getItemCount() {
        return mListRoute.size();
    }

    //associate imageview and recycleview
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView imageView;
        private TextView txtName, txtDate, txtDescript;
        private RecyclerView listImg;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.avatar);
            txtName = (TextView) v.findViewById(R.id.name);
            txtDate = (TextView) v.findViewById(R.id.date);
            txtDescript = (TextView) v.findViewById(R.id.description);
            listImg = (RecyclerView) v.findViewById(R.id.listImg);
            v.setOnCreateContextMenuListener(this);

        }

        public ImageView getImageView() {
            return imageView;
        }

        public RecyclerView getListImgView() {
            return listImg;
        }

        public TextView getTxtNameView() {
            return txtName;
        }

        public TextView getTxtDateView() {
            return txtDate;
        }

        public TextView getTxtDescriptView() {
            return txtDescript;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select action");
            menu.add(0, 0, 0, " Show map ");
            menu.add(0, 1, 0, " Edit info ");
        }
    }
}