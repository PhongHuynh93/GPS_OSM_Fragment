package dhbk.android.gps_osm_fragment.Help;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import dhbk.android.gps_osm_fragment.R;

public class ListImageAdapter extends RecyclerView.Adapter<ListImageAdapter.ViewHolder> {
    private List<File> mImageFile;
    private List<File> mSelectedImage;

    public ListImageAdapter(List<File> holderFile, List<File> selected) {
        mImageFile = holderFile;
        mSelectedImage = selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File image = mImageFile.get(position);
        ImageView imgView = holder.getImageView();

        if (mSelectedImage != null) {
            if (mSelectedImage.contains(image)) {
                imgView.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            } else {
                imgView.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            }
        }
        Picasso.with(imgView.getContext()).load(image).resize(195, 195).into(imgView);

    }


    @Override
    public int getItemCount() {
        return mImageFile.size();
    }

    //associate imageview and recycleview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageItem);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}
