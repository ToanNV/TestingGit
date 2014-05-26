package com.publicsolution.psmaproute;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity
{
//    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    // static final LatLng KIEL = new LatLng(53.551, 9.993);
//    static final LatLng KIEL = new LatLng(48.09281503, 11.8225047);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

//        Marker kiel = map.addMarker(new MarkerOptions().position(KIEL).icon(
//            BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_red)));
//
//        // Move the camera instantly to hamburg with a zoom of 15.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(KIEL, 16));
//        Log.d("Toan", "distance:" + computeDistanceUseHaversine(48.09337727, 11.8359761, 48.09336061, 11.83604819));
//        Log.d("Toan", "distance:" + computeDistanceUseHaversine(21.0346433, 105.8292631, 21.0346613, 105.8293126));
//        new LoadDataAsyncTask().execute("20140122175653_tour_data.json");
        new LoadDataAsyncTask().execute("20140313070305_tour_data.json");
    }
    
    private class LoadDataAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            return loadData(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            BitmapDescriptor pinBlue = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_blue);
            BitmapDescriptor pinGreen = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_green);
            BitmapDescriptor pinOrange = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_orange);
            BitmapDescriptor pinYellow = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_yellow);
            LatLng startLocation = null;
            try
            {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("activities");
                Log.d("Toan", "jsonArray:" + jsonArray);
                int totalActivities = jsonArray.length();
                int totalLocations = 0;
                JSONArray jsonArrayLocations = null;
                String activityId = null;
                LatLng location = null;
                StringBuilder title = new StringBuilder();
                String activityTitle = null;
                
                for (int i = 0; i < totalActivities; i++)
                {
                    activityId = jsonArray.getJSONObject(i).getString("activityid");
                    jsonArrayLocations = jsonArray.getJSONObject(i).getJSONArray("locations");
                    totalLocations = jsonArrayLocations.length();
                    if ("11".equalsIgnoreCase(activityId))
                    {
                        activityTitle = "An- u. Abfahrt";
                    }
                    else if ("3".equalsIgnoreCase(activityId))
                    {
                        activityTitle = "R�umen + Streuen";
                    }
                    else if ("1".equalsIgnoreCase(activityId))
                    {
                        activityTitle = "R�umen";
                    }
                    else
                    {
                        activityTitle = "Streuen";
                    }
                       
                    for (int j = 0; j < totalLocations; j++)
                    {
                        title.setLength(0);
                        location = new LatLng(jsonArrayLocations.getJSONObject(j).getDouble("lat"), jsonArrayLocations.getJSONObject(j).getDouble("lon"));
//                        title.append(activityTitle).append("\nlatitude:").append(location.latitude).append(" longitude:").append(location.longitude);
                        title.append(activityTitle).append(" --- Time:" + jsonArrayLocations.getJSONObject(j).getString("time"));
                        if (i == 0 && j == 0)
                        {
                            startLocation = new LatLng(jsonArrayLocations.getJSONObject(j).getDouble("lat"), jsonArrayLocations.getJSONObject(j).getDouble("lon"));
                            map.addMarker(new MarkerOptions().position(startLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_start))).setTitle(title.toString());
                        }
                        else if (i == totalActivities - 1 && j == totalLocations - 1)
                        {
                            map.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_red))).setTitle(title.toString());
                        }
                        else if ("11".equalsIgnoreCase(activityId))
                        {
                            map.addMarker(new MarkerOptions().position(location).icon(pinBlue)).setTitle(title.toString());
                        }
                        else if ("3".equalsIgnoreCase(activityId))
                        {
                            map.addMarker(new MarkerOptions().position(location).icon(pinGreen)).setTitle(title.toString());
                        }
                        else if ("1".equalsIgnoreCase(activityId))
                        {
                            map.addMarker(new MarkerOptions().position(location).icon(pinOrange)).setTitle(title.toString());
                        }
                        else
                        {
                            map.addMarker(new MarkerOptions().position(location).icon(pinYellow)).setTitle(title.toString());
                        }
                    }
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 16));
            }
            catch (Throwable e)
            {
                Log.d("Toan", "e:" + e);
            }
        }
        
    }

    public String loadData(String inFile)
    {
        String tContents = "";
        try
        {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        }
        catch (IOException e)
        {
        }
        return tContents;
    }
    
    /**
     * Calculate the distance between 2 gps locations 
     * 
     * @param startLat : The latitude of start point in degree
     * @param startLon : The longitude of start point in degree
     * @param endLat : The latitude of end point in degree
     * @param endLon : The longitude of end point in degree
     * 
     * @return The distance between start point and end point in meterss
     */
    public static double computeDistanceUseHaversine(double startLat, double startLon, double endLat, double endLon)
    {
        final double EARTH_RADIUS = 6378137.0d;

        // Compute delta latitude and delta longitude.
        double deltaLatInRadian = Math.toRadians(endLat - startLat);
        double deltaLonInRadian = Math.toRadians(endLon - startLon);

        // Convert start latitude and end latitude to radian
        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double haversine = Math.sin(deltaLatInRadian / 2) * Math.sin(deltaLatInRadian / 2) + Math.sin(deltaLonInRadian / 2) * Math.sin(deltaLonInRadian / 2) * Math.cos(startLat) * Math.cos(endLat);
        double c = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

        return EARTH_RADIUS * c;
    }
}
