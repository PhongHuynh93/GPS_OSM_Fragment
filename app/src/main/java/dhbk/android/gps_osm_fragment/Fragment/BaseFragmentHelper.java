package dhbk.android.gps_osm_fragment.Fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dhbk.android.gps_osm_fragment.Help.Constant;


public class BaseFragmentHelper extends Fragment{
    public static final String TAG = "BaseFragmentHelper";

    // change instruction after getting it from google
    protected String changeInstructionFromGoogle(String title) {
        // change this to remove <div>
        String instRemove = title;

        if (instRemove.contains("</div>")) {
            instRemove = instRemove.substring(0, instRemove.indexOf("<div"));
        }

        Log.i(TAG, "changeInstructionFromGoogle: " + instRemove);


        // TODO: 5/11/16 depends on language, remove words  -> check language
        // remove  string after "At"
        // 1. at -> end
        // 2. at -> , (but not remove at in "at the roundable")
        if (instRemove.contains("at")) {
            String instAfterAt = instRemove.substring(instRemove.indexOf("at"), instRemove.length());

            instRemove = instRemove.substring(0, instRemove.indexOf("at"));

            if (instAfterAt.contains("onto")) {
                instAfterAt = instAfterAt.substring(instAfterAt.indexOf("onto"), instAfterAt.length());
            } else if (instAfterAt.contains("toward")) {
                instAfterAt = instAfterAt.substring(instAfterAt.indexOf("toward"), instAfterAt.length());
            } else {
                instAfterAt = "";
            }

            instRemove = instRemove + instAfterAt;
        }

        if (instRemove.contains("At")) {
            // nếu ko có chữ tại vòng xoay, thì xóa đến dấu ,
            if (!instRemove.contains("roundabout")) {
                // bỏ từ dầu đến dấu , để lấy kỹ tự cách dầu , 2 space (do có khoảng trăngg)
                instRemove = instRemove.substring(instRemove.indexOf(",") + 2, instRemove.length()); // đến length do ký tự cuối là length - 1 mà substring lại ko lấy ký tự length()
            }
        }


        // remove after past - Continue straight past Piaggio SAPA Điện BIên Phủ onto <b>Xa lộ Hà Nội</b>/<b>Điện Biên Phủ</b>/<b>QL52</b>
        if (instRemove.contains("Continue straight")) {
            // nếu có chữ past nữa thì bỏ phần sau past
            if (instRemove.contains("past")) {
                String instAfterPast = instRemove.substring(instRemove.indexOf("past"), instRemove.length());

                instRemove = instRemove.substring(0, instRemove.indexOf("past"));

                if (instAfterPast.contains("onto")) {
                    instAfterPast = instAfterPast.substring(instAfterPast.indexOf("onto"), instAfterPast.length());
                } else if (instAfterPast.contains("toward")) {
                    instAfterPast = instAfterPast.substring(instAfterPast.indexOf("toward"), instAfterPast.length());
                } else {
                    instAfterPast = "";
                }

                instRemove += instAfterPast;
            }
        }
        return "" + Html.fromHtml(instRemove);
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

    // make url to connect to google server
    @NonNull
    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog, String travelMode, String language) {
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
        urlString.append("&mode=").append(travelMode);
        urlString.append("&alternatives=").append("false"); // not allow alternate route
        urlString.append("&units=").append("metric"); // use met/ kilomet
        urlString.append("&language=").append(language);
        urlString.append("&key=").append(Constant.GOOGLE_SERVER_KEY);

        Log.i("BaseFragment", "makeURL: " + urlString.toString());
        return urlString.toString();
    }

    // get JSON reponse from a URL
    @NonNull
    public String getJSONFromUrl(String url) {
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

    // phong - method to return a list of point from encode point JSON.
    public List<GeoPoint> decodePoly(String encoded) {
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
}
