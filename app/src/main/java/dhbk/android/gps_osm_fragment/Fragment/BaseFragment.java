package dhbk.android.gps_osm_fragment.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dhbk.android.gps_osm_fragment.Activity.MainActivity;
import dhbk.android.gps_osm_fragment.Help.Constant;
import dhbk.android.gps_osm_fragment.R;

public abstract class BaseFragment extends Fragment implements MapEventsReceiver {
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

    //phong - add marker at a location
    public void setMarkerAtLocation(Location userCurrentLocation, int icon) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            mIMapController.setCenter(userCurrentPoint);
            mIMapController.zoomTo(mMapView.getMaxZoomLevel());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));
//                        hereMarker.setTitle("You here");
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();
        } else {
            Toast.makeText(getContext(), "Not determine your current location", Toast.LENGTH_SHORT).show();
        }
    }

    // phong - add marker with title
    public void setMarkerAtLocation(Location userCurrentLocation, int icon, String title) {
        if (userCurrentLocation != null) {
            GeoPoint userCurrentPoint = new GeoPoint(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
            Marker hereMarker = new Marker(mMapView);
            hereMarker.setPosition(userCurrentPoint);
            hereMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            hereMarker.setIcon(ContextCompat.getDrawable(getContext(), icon));
            final String instructionNeedRemove = "" + Html.fromHtml(title);

            // remove a part of string
            String instruction = instructionNeedRemove;
            if (instruction.indexOf("\n\n") != -1) {
                // it contains world
                instruction = instructionNeedRemove.substring(0, instructionNeedRemove.indexOf("\n\n"));
            }

//            final String instructionKhongDau = new AccentRemover().toUrlFriendly(instruction);
//            Log.i(TAG, "setMarkerAtLocation: " + Html.toHtml(Html.fromHtml(title)));
            hereMarker.setTitle(instruction);
            mMapView.getOverlays().add(hereMarker);
            mMapView.invalidate();

            final String instructionWhenClickMarker = instruction;

//             when click marker, speak instruction
            hereMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    marker.showInfoWindow();
                    mapView.getController().animateTo(marker.getPosition());
                    new GetLanguageDetect2().execute(instructionWhenClickMarker);
                    return true;
                }
            });


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

    @NonNull
    public String retrieveSubString(String s) {
        return s.substring(0, s.lastIndexOf("@"));
    }

    public String mergeNick(String a, String b) {
        // merge 2 string nhưng theo abc
        int compareString = a.compareTo(b);
        if (compareString < 1) {
            // la a đứng trước b
            return a + b;
        } else if (compareString > 1) {
            return b + a;
        } else {
            return a + b;
        }
    }

    public void drawPathOSMWithInstruction(Location startPoint, Location destPoint, String travelMode, float width, String language) {
        this.language = language;
        String url = makeURL(startPoint.getLatitude(), startPoint.getLongitude(), destPoint.getLatitude(), destPoint.getLongitude(), travelMode, language);
        new GetDirectionInstruction(startPoint, destPoint, url, width).execute();
    }

    @NonNull
    private String makeURL(double sourcelat, double sourcelog, double destlat, double destlog, String travelMode, String language) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&mode=" + travelMode);
        urlString.append("&language=" + language);
        urlString.append("&key=" + Constant.GOOGLE_SERVER_KEY);
        Log.i("BaseFragment", "makeURL: " + urlString.toString());
        return urlString.toString();
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
            String json = getJSONFromUrl(this.url);
            return json;
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
            // TODO: 5/5/16 speak vietname + english
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


    // phong - get JSON reponse from a URL
    @NonNull
    private String getJSONFromUrl(String url) {
        StringBuilder stringBuilder = new StringBuilder();

        // request
        URL url1 = null;
        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            assert url1 != null;
            connection = (HttpURLConnection) url1.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert connection != null;
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        // read the response
        try {
            if (connection.getResponseCode() == 201 || connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Error in request");
        }

        return stringBuilder.toString();
    }


    // phong - draw path from JSON reponse
    private void drawPathWithInstruction(String result, Location startPlace, Location destPlace, float width) {
        ArrayList<GeoPoint> waypoints = new ArrayList<>(); // tao 1 array cac toạ dộ
        GeoPoint startPoint = new GeoPoint(startPlace.getLatitude(), startPlace.getLongitude());
        waypoints.add(startPoint);
        ArrayList<JSONObject> stepsArrayObject = null;
        boolean isReturnOK = true;

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
                    // get lat/long of a step
                    JSONObject startLocation = step.getJSONObject("start_location");
                    double lat = Double.parseDouble(startLocation.getString("lat"));
                    double lng = Double.parseDouble(startLocation.getString("lng"));
                    Location stepLocation = new Location("stepLocation");
                    stepLocation.setLatitude(lat);
                    stepLocation.setLongitude(lng);
                    // get instruction
                    String instruction = step.getString("html_instructions");
                    Log.i("BaseFragment", "drawPathWithInstruction: " + instruction);
                    // add marker
                    setMarkerAtLocation(stepLocation, Constant.ICON_INSTRUCTION, instruction);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        mMapView.invalidate();

    }


    // phong - method to return a list of point from JSON.
    private List<GeoPoint> decodePoly(String encoded) {
        List<GeoPoint> poly = new ArrayList<GeoPoint>(); // list geopoint
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            GeoPoint p = new GeoPoint((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void centerMap(Location startPoint) {
        mMapView.getController().setCenter(new GeoPoint(startPoint.getLatitude(), startPoint.getLongitude()));
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


    // draw path depends on arrayList of GeoPoint
    public void drawPathDependGeoPoint(ArrayList<GeoPoint> waypoints) {
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
}
