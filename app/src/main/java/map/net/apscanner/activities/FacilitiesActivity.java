package map.net.apscanner.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import map.net.apscanner.R;
import map.net.apscanner.classes.facility.Facility;
import map.net.apscanner.classes.facility.FacilityAdapter;
import map.net.apscanner.helpers.UserInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FacilitiesActivity extends AppCompatActivity {

    ListView facilitiesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);

        facilitiesListView = (ListView) findViewById(R.id.facilitiesListView);


        new getFacilitiesFromServer().execute();
    }

    /**
     * This async task gets a list of User's facilities data from server and put them into a
     * ListView. The user can touch on the facility to access its zones.
     */
    private class getFacilitiesFromServer extends AsyncTask<Void, Void, Response> {

        @Override
        protected Response doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(getResources().getString(R.string.get_facilities_url))
                    .header("Content-Type", "application/json")
                    .header("X-User-Email", UserInfo.getUserEmail())
                    .header("X-User-Token", UserInfo.getUserToken())
                    .build();
            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }


        protected void onPostExecute(Response response) {
            if (response == null) {
                Toast toast = Toast.makeText(FacilitiesActivity.this,
                        "Something went wrong, try refreshing", Toast.LENGTH_LONG);
                toast.show();
            } else if (response.code() >= 200 && response.code() < 300) {

                JSONArray facilitiesJSON = null;
                try {
                    facilitiesJSON = new JSONArray(response.body().string());
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                List<Facility> facilitiesList = new ArrayList<>();

                assert facilitiesJSON != null;
                for (int i = 0; i < facilitiesJSON.length(); i++) {
                    try {
                        JSONObject facilityJSON = facilitiesJSON.getJSONObject(i);
                        Facility facility = new Facility(facilityJSON.get("name").toString(),
                                facilityJSON.getJSONObject("_id").get("$oid").toString());

                        /* Sets up a ISO format and convert servers format to it */
                        DateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        String facilityCreatedAtDate = facilityJSON.get("created_at").toString();
                        Date completeDate = dateFormatISO.parse(facilityCreatedAtDate);

                        /* Setting up days only date*/
                        DateFormat daysOnlyDataFormat = new SimpleDateFormat("dd/MMM/yy");
                        String daysOnlyDate = daysOnlyDataFormat.format(completeDate);
                        facility.setDate(daysOnlyDate);

                        facilitiesList.add(facility);
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }

                FacilityAdapter adapter = new FacilityAdapter(FacilitiesActivity.this, facilitiesList);
                facilitiesListView.setAdapter(adapter);
                facilitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Facility facilityAtPosition = (Facility) facilitiesListView.getItemAtPosition(position);
                        Intent zonesIntent = new Intent(FacilitiesActivity.this, ZonesActivity.class);
                        zonesIntent.putExtra("FACILITY_ID", facilityAtPosition.getId());
                        zonesIntent.putExtra("FACILITY_NAME", facilityAtPosition.getName());
                        startActivity(zonesIntent);
                    }
                });

            } else {

                Toast toast = Toast.makeText(FacilitiesActivity.this,
                        "Something went wrong, try refreshing", Toast.LENGTH_LONG);
                toast.show();
            }
        }


    }
}

