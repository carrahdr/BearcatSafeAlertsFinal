package edu.uc.bearcatsafealerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MySingleton mMySingleton;

    // On creation of this activity, automatically go refresh the crime alert list from the UC webpage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshCrimeList();
        mMySingleton = MySingleton.getInstance();
    }

    /** Called when the user clicks the Crime Map button */
    public void CrimeMapClicked(View view) {
        // Clear the "current crime selected" value so that the map will center on our current location
        mMySingleton.setmCrime("");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Crime Log button */
    public void CrimeLogClicked(View view) {
        // Launch the crime log activity
        Intent intent = new Intent(this, CrimeAlertListActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Refresh button */
    public void RefreshClicked(View view) {
        // Refresh the crime alert list
        refreshCrimeList();
    }

    /** Called when the user clicks the Contact UC button */
    public void ContactClicked(View view) {
        // Launch a browser with the UC Public Safety webpage
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.uc.edu/publicsafety.html"));
        startActivity(browserIntent);
    }

    // Refresh the list of crime alerts by querying the UC police crime log webpage
    private void refreshCrimeList()
    {
        // This method uses the "Volley" library to send/receive an http request
        final TextView mTextView = (TextView) findViewById(R.id.textView2);
        mTextView.setText("Updating Alerts");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.uc.edu/webapps/publicsafety/policelog2.aspx";

        // Request a string response from the provided URL.  This is the html text of the UC crime log webpage
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // If the response does not contain the magic words "Results for", it doesn't have a valid crime log
                        // In that case, use the stored list of crime alerts for demo purposes.
                        if(response.indexOf("Results For")<0)response=getResources().getString(R.string.stored_alerts);
                        mMySingleton.setmCrimePage(response);
                        // This is the key method call to parse the webpage into a list of crime alerts
                        int numAlerts = mMySingleton.parseCrimePage();
                        mTextView.setText("Number of Alerts: " + numAlerts);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("Unable to retrieve Crime Data");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
