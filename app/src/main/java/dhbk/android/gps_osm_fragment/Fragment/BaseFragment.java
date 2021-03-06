package dhbk.android.gps_osm_fragment.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Help.ChooseLocationFragment;
import dhbk.android.gps_osm_fragment.Help.ChooseLocationFragmentWithStart;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

// TODO: 5/11/16 when choose vi/en , remove old paths and draw new path immediately
public abstract class BaseFragment extends BaseFragmentHelper implements MapEventsReceiver {
    private static final String TAG = "BaseFragment";
    private MapView mMapView;
    private IMapController mIMapController;

    // text to speech
    HashMap<String, String> map = new HashMap<String, String>();
    TextToSpeech t1;
    TextToSpeech t2;

    private String language = Constant.LAN_VI;

    public MapView getMapView() {
        return mMapView;
    }

    public IMapController getIMapController() {
        return mIMapController;
    }

    public void makeMapDefaultSetting() {
        mMapView = (MapView) getActivity().findViewById(R.id.map); // map
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setMultiTouchControls(true);
        mIMapController = mMapView.getController(); // map controller
        mIMapController.setZoom(Constant.ZOOM);
        GeoPoint startPoint = new GeoPoint(10.772241, 106.657676);
        mIMapController.setCenter(startPoint);
        // add listen when tap the map.
        clearMap();

        // TODO - en - use android speak, vi - use different app (change this)
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        }, "com.google.android.tts");
        t2 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                }
            }
        }, "com.vnspeak.ttsengine.vitts");

    }

    // clear map, but add eventlocation
    public void clearMap() {
        mMapView.getOverlays().clear();
        // add event overlay
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getContext(), this);
        mMapView.getOverlays().add(0, mapEventsOverlay);
    }

    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(((MainActivity) getActivity()).getGoogleApiClient());
    }

    private void centerMap(Location startPoint) {
        mMapView.getController().setCenter(new GeoPoint(startPoint.getLatitude(), startPoint.getLongitude()));
    }

    //phong - add marker at a location
    protected void setMarkerAtLocation(Location userCurrentLocation, int icon) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            mIMapController.setCenter(userCurrentPoint);
            mIMapController.zoomTo(mMapView.getMaxZoomLevel());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    // phong - add marker at a location with instruction + speak
    protected void setMarkerAtLocation(Location userCurrentLocation, int icon, String title) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));

            final String instAfterRemove = changeInstructionFromGoogle(title, language);

            hereMarker.setTitle(instAfterRemove);
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();

//            final String instructionWhenClickMarker = instruction;

//             when click marker, speak instruction
//            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker, MapView mapView) {
//                    marker.showInfoWindow();
//                    mapView.getController().animateTo(marker.getPosition());
//                    new GetLanguageDetect2().execute(instructionWhenClickMarker);
//                    return true;
//                }
//            });


        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    // phong - add marker at a location with instruction + speak
    protected void setMarkerAtLocation(Location userCurrentLocation, int icon, String title, String distance) {
        if (userCurrentLocation != null) {
            Log.i(TAG, "setMarkerAtLocation: " + title);
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));

//            String instAfterRemove = changeInstructionFromGoogle(title);

            // FIXME: 5/25/16 remove var instAfterRemove
            String instAfterRemove = title;

            // if "go straight" -> not add mets
            if (distance != null) {
                if (language.equals(Constant.LAN_EN)) {
                    if (title.contains("Merge") || title.contains("merge")) {
                        instAfterRemove = "go straight";
                    } else if (title.contains("Keep") || title.contains("keep")) {
                        instAfterRemove = "go straight";
                    } else {
                        instAfterRemove = "go straight " + distance + ", after that " + instAfterRemove;
                    }
                } else {
                    instAfterRemove = "đi thẳng " + distance + ", sau đó " + instAfterRemove;
                }

            }

            Log.i(TAG, "setMarkerAtLocation: " + instAfterRemove);

            hereMarker.setTitle(instAfterRemove);
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();

            final String instClick;
            if (language.equals(Constant.LAN_EN)) {
                instClick = "<e>" + instAfterRemove;
            } else {
                instClick = "<v>" + instAfterRemove;
            }
            // TODO: 5/26/16 speak
            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    marker.showInfoWindow();
                    mapView.getController().animateTo(marker.getPosition());
                    String[] instArray = new String[1];
                    instArray[0] = instClick;
                    if (language.equals(Constant.LAN_EN)) {
                        speakoutENG(instArray, 0);
                    } else {
                        speakoutVN(instArray, 0);
                    }
                    return true;
                }
            });

//
//            if (arr[i].startsWith("<e>")) {
//                speakoutENG(arr, i);
//            }
//            if (arr[i].startsWith("<v>")) {
//                speakoutVN(arr, i);
//            }

//            final String instructionWhenClickMarker = instruction;

//             when click marker, speak instruction
//            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker, MapView mapView) {
//                    marker.showInfoWindow();
//                    mapView.getController().animateTo(marker.getPosition());
//                    new GetLanguageDetect2().execute(instructionWhenClickMarker);
//                    return true;
//                }
//            });


        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    //phong - add marker at a location + dialog (des place)
    protected void setMarkerAtLocationWithDialog(Location userCurrentLocation, int icon, final FragmentManager fragmentManager) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
//            mIMapController.setCenter(userCurrentPoint);
//            mIMapController.zoomTo(mMapView.getMaxZoomLevel());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));

            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    new ChooseLocationFragment().show(fragmentManager, Constant.DIALOG_FRAG);
                    return true;
                }
            });
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    //phong - add marker at a location + dialog (start + des place)
    protected void setMarkerAtLocationWithDialogWithStartPlace(Location userCurrentLocation, int icon, final FragmentManager fragmentManager) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
//            mIMapController.setCenter(userCurrentPoint);
//            mIMapController.zoomTo(mMapView.getMaxZoomLevel());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));

            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    new ChooseLocationFragmentWithStart().show(fragmentManager, Constant.DIALOG_FRAG);
                    return true;
                }
            });
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }


    protected void drawPathOSMWithInstruction(Location startPoint, Location destPoint, String travelMode, float width, String language) {
        this.language = language;
        String url = makeURL(startPoint.getLatitude(), startPoint.getLongitude(), destPoint.getLatitude(), destPoint.getLongitude(), travelMode, language);
        new GetDirectionInstruction(startPoint, destPoint, url, width).execute();
    }

    // phong - get json from URL
    private class GetDirectionInstruction extends AsyncTask<Void, Void, String> {
        private final Location startPoint;
        private final Location destPoint;
        private String url;
        private float width;

        public GetDirectionInstruction(Location startPoint, Location destPoint, String url, float width) {
            this.startPoint = startPoint;
            this.destPoint = destPoint;
            this.url = url;
            this.width = width;
        }

        @Override
        protected String doInBackground(Void... params) {
            return getJSONFromUrl(this.url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                drawPathWithInstruction(result, this.startPoint, this.destPoint, this.width);
                centerMap(this.startPoint);
            }
        }
    }


    // phong - draw path from JSON reponse
    private void drawPathWithInstruction(String result, Location startPlace, Location destPlace, float width) {
        ArrayList<GeoPoint> waypoints = new ArrayList<>(); // tao 1 array cac toạ dộ
        GeoPoint startPoint = new GeoPoint(startPlace.getLatitude(), startPlace.getLongitude());

        waypoints.add(startPoint);
        ArrayList<JSONObject> stepsArrayObject = null;
        boolean isReturnOK = true;

        // contains previous location/metric when loop through "steps"
        Location startPlacePrevious = startPlace;
        String metricPrevious = null;
        int distanceInMetIntegerPrevious = 0;

        // biến nhận biết rằng tương lai đang tại roundabout
        boolean futureRoundabout = false;

        // polyline của từng đoạn đường
        ArrayList<GeoPoint> polylineSegment = null; // tao 1 array cac toạ dộ
        ArrayList<GeoPoint> polylineSegmentPrevious = null; // tao 1 array cac toạ dộ


        try {
            final JSONObject json = new JSONObject(result); // lưu JSON mà server trả
            JSONArray routeArray = json.getJSONArray("routes");

            if (json.getString("status").equals("OK")) {

                JSONObject routes = routeArray.getJSONObject(0);

                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline"); // duong di cua google

                // retrieve step
                JSONArray legsArray = routes.getJSONArray("legs");
                JSONObject leg = legsArray.getJSONObject(0);
                JSONArray stepsArray = leg.getJSONArray("steps");
                stepsArrayObject = new ArrayList<>();
                for (int i = 0; i < stepsArray.length(); i++) {
                    stepsArrayObject.add(stepsArray.getJSONObject(i));
                }

                // : 5/24/16 INSTRUCTION POLYLINE
                String encodedString = overviewPolylines.getString("points"); // lấy value với key là "point"
                List<GeoPoint> list = decodePoly(encodedString); // hàm này return 1 list Geopoint doc  đường đi


                for (int z = 0; z < list.size() - 1; z++) {
                    GeoPoint src = list.get(z);
                    waypoints.add(src);
                }
            } else {
                isReturnOK = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isReturnOK) {
            // draw path
            GeoPoint destPoint = new GeoPoint(destPlace.getLatitude(), destPlace.getLongitude());
            waypoints.add(destPoint);


            Road road = new Road(waypoints);
//
//            Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Constant.COLOR, width, getApplicationContext());
//            mMapView.getOverlays().add(roadOverlay);

            //Lấy tọa độ tất cả các điểm neo
            final ArrayList<GeoPoint> list = road.mRouteHigh;

            final PathOverlay myPath = new PathOverlay(Constant.COLOR, getContext());
            Paint paint = myPath.getPaint();
            paint.setStrokeWidth(width);
            paint.setAlpha(150);
            paint.setDither(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            myPath.setPaint(paint);

            // draw path
            for (int i = 1; i < list.size(); i++) {
                GeoPoint g = new GeoPoint(list.get(i).getLatitude(), list.get(i).getLongitude());
                myPath.addPoint(g);
            }
            mMapView.getOverlays().add(myPath);


            // draw marker on the road
            for (JSONObject step : stepsArrayObject) {
                try {
                    // : 5/23/16 ta lấy distance và location từ điểm đằng trước, còn instruction từ điểm hiển tại -> để cho có dạng từ location và đi bn mét từ điểm đằng trước, ta sẽ có hướng dẫn ở điểm hiện tại
                    // get lat/long of a step
                    // 5/11/16 get start_location
                    JSONObject startLocation = step.getJSONObject("start_location"); // contains start of step
                    double latstart = Double.parseDouble(startLocation.getString("lat"));
                    double lngstart = Double.parseDouble(startLocation.getString("lng"));
                    Location stepLocationStart = new Location("stepLocationStart");
                    stepLocationStart.setLatitude(latstart);
                    stepLocationStart.setLongitude(lngstart);

                    // 5/11/16 get distance of step in metric and add this message in startpoint
                    JSONObject distance = step.getJSONObject("distance");
                    String distanceInMet = distance.getString("text");

                    // : 5/26/16 round distance
                    int distanceInMetInteger = distance.getInt("value");
                    // nếu 3 chữ số thì đọc là km
                    if (distanceInMetInteger >= 1000) {
                        if (distanceInMet.contains("km")) {
                            if (this.language.equals(Constant.LAN_EN)) {
                                distanceInMet = distanceInMet.replace("km", "kilometer");
                            } else {
                                distanceInMet = distanceInMet.replace("km", "kí lô mét");
                            }
                        }
                    } // nếu ko thì đọc là met
                    else {
                        // làm tròn hàng chục
                        distanceInMetInteger = distanceInMetInteger - distanceInMetInteger % 10;
                        if (this.language.equals(Constant.LAN_EN)) {
                            distanceInMet = distanceInMetInteger + " " + "metric";
                        } else {
                            distanceInMet = distanceInMetInteger + " " + "mét";
                        }
                    }


                    // instruction after change google instruction
                    String instruction = changeInstructionFromGoogle(step.getString("html_instructions"), language);

//                    setMarkerAtLocation(startPlacePrevious, Constant.ICON_INSTRUCTION, instruction, metricPrevious);

                    // : 5/24/16 lấy polyline của điểm đằng trước
                    JSONObject polylineJson = step.getJSONObject("polyline");
                    String encodedStringSegment = polylineJson.getString("points"); // lấy value với key là "point"
                    polylineSegment = (ArrayList<GeoPoint>) decodePoly(encodedStringSegment); // hàm này return 1 list Geopoint doc  đường đi

                    // FIXME: 5/28/16 chỗ này viết thừa rất nhiều, viết code lại cho đẹp
                    if (polylineSegmentPrevious != null) {
                        if (polylineSegmentPrevious.size() >= 5) {
                            // TODO: 5/26/16 nếu đoạn đường dài thì tùy đoạn đường ta phải chia nhỏ nữa
                            for (int z = 0; z < polylineSegmentPrevious.size() - 1; z++) {
                                // : 5/23/16 ta phải dịch marker sau lên trên chút xúi, z càng cao thì nó càng dịch nhiều
                                if (futureRoundabout) {
                                    // nếu roundabout lớn
                                    if (polylineSegmentPrevious.size() >= 65) {
                                        Log.i(TAG, "drawPathWithInstruction: " + polylineSegmentPrevious.size());
                                        futureRoundabout = false;
                                        GeoPoint src = polylineSegmentPrevious.get(47);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        // vẽ marker màu xanh dương
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_END, instruction, metricPrevious);
                                        break;
                                    } else { // nếu roundabout nhỏ 
                                        Log.i(TAG, "drawPathWithInstruction: " + polylineSegmentPrevious.size());
                                        futureRoundabout = false;
                                        GeoPoint src = polylineSegmentPrevious.get(20);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        // vẽ marker màu xanh dương
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_END, instruction, metricPrevious);
                                        break;
                                    }
                                } else {
                                    if (polylineSegmentPrevious.size() <= 10) {
                                        GeoPoint src = polylineSegmentPrevious.get(1);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        // vẽ marker màu xanh dương
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_END, instruction, metricPrevious);
                                    } else if (polylineSegmentPrevious.size() <= 20) {
                                        GeoPoint src = polylineSegmentPrevious.get(2);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        // vẽ marker màu xanh dương
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_END, instruction, metricPrevious);
                                    } else {
                                        // nếu tại vòng xuyến thì vẽ marker xa hơn
                                        GeoPoint src = polylineSegmentPrevious.get(6);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        // vẽ marker màu xanh dương
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_END, instruction, metricPrevious);
                                    }
                                }


                                // vẽ marker màu xanh lá
                                // : 5/24/16 khi có 2 chữ sau mới vẽ nha, ko thôi đoạn thẳng nó rất dài, dich cái đoạn thông báo xuống 1 chút xíu nữa.
                                // : 5/23/16 nếu trong đoạn co chữ "turn left"/"turn right" thì phai thêm marker là khi nào mới quẹo
                                if ((z == polylineSegmentPrevious.size() - 2) && (distanceInMetIntegerPrevious > 200)) {
                                    String turnInstruction = null;
                                    if (this.language.equals(Constant.LAN_EN)) {
                                        if (instruction.contains("turn left")) {
                                            turnInstruction = "turn left";
                                        } else if (instruction.contains("Turn left")) {
                                            turnInstruction = "turn left";
                                        } else if (instruction.contains("turn right")) {
                                            turnInstruction = "turn right";
                                        } else if (instruction.contains("Turn right")) {
                                            turnInstruction = "turn right";
                                        } // nếu tại vòng xoay cũng phải thông báo
                                        else if (instruction.contains("roundabout") && instruction.contains("take")) {
                                            turnInstruction = instruction.substring(instruction.indexOf("take"), instruction.length());
                                            if (turnInstruction.contains("onto")) {
                                                turnInstruction = turnInstruction.substring(0, turnInstruction.indexOf("onto"));
                                            }
                                        }
                                    } else {
                                        if (instruction.contains("rẽ trái")) {
                                            turnInstruction = "rẽ trái";
                                        } else if (instruction.contains("Rẽ trái")) {
                                            turnInstruction = "rẽ trái";
                                        } else if (instruction.contains("rẽ phải")) {
                                            turnInstruction = "rẽ phải";
                                        } else if (instruction.contains("Rẽ phải")) {
                                            turnInstruction = "rẽ phải";
                                        } else if (instruction.contains("xuyến") && instruction.contains("đi")) {
                                            turnInstruction = instruction.substring(instruction.indexOf("đi", instruction.indexOf("xuyến")), instruction.length());
                                            if (turnInstruction.contains("vào")) {
                                                turnInstruction = turnInstruction.substring(0, turnInstruction.indexOf("vào"));
                                            }
                                        }
                                    }

                                    if (turnInstruction != null) {
                                        GeoPoint src = polylineSegmentPrevious.get(z);
                                        Location segmentLocation = new Location("stepLocationStart");
                                        segmentLocation.setLatitude(src.getLatitude());
                                        segmentLocation.setLongitude(src.getLongitude());
                                        if (this.language.equals(Constant.LAN_EN)) {
                                            turnInstruction = turnInstruction + " now";
                                        } else {
                                            turnInstruction = turnInstruction + " ngay bây giờ";
                                        }
                                        setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION_GREEN, turnInstruction, null);
                                    }
                                }
                            }
                        }
                        // khi doan dường quá ngắn thì lấy điểm 1
                        else if (polylineSegmentPrevious.size() == 2) {
                            // nếu như đoạn đó ngắn quá, thì chỉ lấy bằn0g 1
                            GeoPoint src = polylineSegmentPrevious.get(1);
                            Location segmentLocation = new Location("stepLocationStart");
                            segmentLocation.setLatitude(src.getLatitude());
                            segmentLocation.setLongitude(src.getLongitude());
                            // vẽ marker màu xanh dương
                            setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION, instruction, null);
                        }
                        // nếu đoạn dài hơn xíu thì lấy diếm 2
                        else if (polylineSegmentPrevious.size() > 2) {
                            // nếu như đoạn đó ngắn quá, thì chỉ lấy bằn0g 1
                            GeoPoint src = polylineSegmentPrevious.get(2);
                            Location segmentLocation = new Location("stepLocationStart");
                            segmentLocation.setLatitude(src.getLatitude());
                            segmentLocation.setLongitude(src.getLongitude());
                            // vẽ marker màu xanh dương
                            setMarkerAtLocation(segmentLocation, Constant.ICON_INSTRUCTION, instruction, null);
                        }
                    }


                    startPlacePrevious = stepLocationStart;
                    metricPrevious = distanceInMet;
                    distanceInMetIntegerPrevious = distanceInMetInteger;
                    polylineSegmentPrevious = polylineSegment;

                    Log.i(TAG, "drawPathWithInstruction: " + instruction);
                    if (language.equals(Constant.LAN_EN)) {
                        if (instruction.contains("roundabout")) {
                            futureRoundabout = true;
                        }
                    } else {
                        if (instruction.contains("xuyến")) {
                            futureRoundabout = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // 5/11/16 add "Đã đến đích" vào marker cuối cùng
            if (this.language.equals(Constant.LAN_EN)) {
                setMarkerAtLocation(startPlacePrevious, Constant.ICON_INSTRUCTION, "near destination", null);
                setMarkerAtLocation(destPlace, Constant.ICON_INSTRUCTION, "destination", null);
            } else {
                setMarkerAtLocation(startPlacePrevious, Constant.ICON_INSTRUCTION, "gần đến đích", null);
                setMarkerAtLocation(destPlace, Constant.ICON_INSTRUCTION, "đã đến đích", null);
            }

        }

        mMapView.invalidate();

    }

    // draw path depends on arrayList of GeoPoint
    protected void drawPathDependGeoPoint(ArrayList<GeoPoint> waypoints) {
        Road road = new Road(waypoints);
        final ArrayList<GeoPoint> list = road.mRouteHigh;

        final PathOverlay myPath = new PathOverlay(Constant.COLOR, getContext());
        Paint paint = myPath.getPaint();
        paint.setStrokeWidth(Constant.WIDTH_LINE);
        paint.setAlpha(150);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        myPath.setPaint(paint);

        // draw path
        for (int i = 1; i < list.size(); i++) {
            GeoPoint g = new GeoPoint(list.get(i).getLatitude(), list.get(i).getLongitude());
            myPath.addPoint(g);
        }

        mMapView.getOverlays().add(myPath);
    }

    //    xac định xem ngôn ngữ mình upload lên là english hay vietnam
    private class GetLanguageDetect2 extends AsyncTask<String, Void, StringBuffer> {
        @Override
        protected StringBuffer doInBackground(String... params) {
            String mPreviousLanguage = "en";
            String mCurrentLanguage = "en";

            StringBuffer instructionBuffer = new StringBuffer(params[0]);
            if (language.equals(Constant.LAN_EN)) {
                instructionBuffer.insert(0, "<en>");
                // retrieve each word from string
                String[] arr = params[0].split(" ");

                // connect to network to retrieve json for each word
                for (String eachWord : arr) {
                    // gui len mang để lấy json về
                    final String FORECAST_BASE_URL =
                            "http://ws.detectlanguage.com/0.2/detect?";
                    final String QUERY_PARAM = "q";
                    final String KEY_PARAM = "key";
                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, eachWord)
                            .appendQueryParameter(KEY_PARAM, "acd8f06a54e981b3077bac8d3c4756c6")
                            .build();
                    String s = getJSONFromUrl(builtUri.toString());

                    try {
                        final JSONObject json = new JSONObject(s);
                        JSONObject data = json.getJSONObject("data");
                        JSONArray detections = data.getJSONArray("detections");
                        JSONObject detectionsBegin = detections.getJSONObject(0);
                        mCurrentLanguage = detectionsBegin.getString("language");
                        // nếu khác ngôn ngữ tiếng anh thì cho nó tiếng việt
                        if (!mCurrentLanguage.equals("en")) {
                            mCurrentLanguage = "vi";
                        }

                        // so sanh
                        if (!mPreviousLanguage.equals(mCurrentLanguage)) {
                            // lấy index chữ eachword trong buffer nha
                            int indexWord = instructionBuffer.indexOf(eachWord);

                            // chèn # + "vi"/"en" tùy previous
                            if (mPreviousLanguage.equals("en")) {
                                instructionBuffer.insert(indexWord, "#<vi>");
                            } else {
                                instructionBuffer.insert(indexWord, "#<en>");
                            }

                        }
                        mPreviousLanguage = mCurrentLanguage;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                instructionBuffer.insert(0, "<vi>");
            }


            return instructionBuffer;
        }

        @Override
        protected void onPostExecute(StringBuffer instructionWithTag) {
            super.onPostExecute(instructionWithTag);
            String stringFormat = instructionWithTag.toString();
            stringFormat = stringFormat.replaceAll("<en>", "<e>");
            stringFormat = stringFormat.replaceAll("<vi>", "<v>");

            Log.i(TAG, "onPostExecute: " + stringFormat);
            String[] arr = stringFormat.split("#");
            int i = 0;
            if (arr[i].startsWith("<e>")) {
                speakoutENG(arr, i);
            }
            if (arr[i].startsWith("<v>")) {
                speakoutVN(arr, i);
            }
        }

    }

    //    speak
    private void speakoutENG(final String[] text, final int i) {
        t1.speak(text[i].replace("<e>", ""), TextToSpeech.QUEUE_FLUSH, map);
        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                if (i + 1 < text.length) {
                    if (text[i + 1].startsWith("<v>")) {
                        speakoutVN(text, i + 1);
                    }
                    if (text[i + 1].startsWith("<e>")) {
                        speakoutENG(text, i + 1);
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    private void speakoutVN(final String[] text, final int i) {

        t2.speak(text[i].replace("<v>", ""), TextToSpeech.QUEUE_FLUSH, map);
        t2.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                if (i + 1 < text.length) {
                    if (text[i + 1].startsWith("<v>")) {
                        speakoutVN(text, i + 1);
                    }
                    if (text[i + 1].startsWith("<e>")) {
                        speakoutENG(text, i + 1);
                    }
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if (t2 != null) {
            t2.stop();
            t2.shutdown();
        }
        super.onDestroy();
    }
}
