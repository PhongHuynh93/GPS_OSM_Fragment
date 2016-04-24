package dhbk.android.gps_osm_fragment.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

/**
 * Created by huynhducthanhphong on 4/22/16.
 */
public abstract class BaseFragment extends Fragment {
    private MapView mMapView;
    private View mRootView;
    private IMapController mIMapController;

    public BaseFragment() {
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        makeMapDefaultSetting();
    }

    public void makeMapDefaultSetting() {
        mMapView = (MapView) mRootView.findViewById(R.id.map); // map
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);
        mIMapController = mMapView.getController(); // map controller
        mIMapController.setZoom(Constant.ZOOM);
        GeoPoint startPoint = new GeoPoint(10.772241, 106.657676);
        mIMapController.setCenter(startPoint);
    }

    public void setRootView(View rootView) {
        this.mRootView = rootView;
    }
}
