package edu.uc.bearcatsafealerts;

import android.content.Context;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshCrimeList();
        mMySingleton = MySingleton.getInstance();
    }
    /** Called when the user clicks the Send button */
    public void CrimeMapClicked(View view) {
        // Do something in response to button
        mMySingleton.setmCrime("");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Send button */
    public void CrimeLogClicked(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, CrimeAlertListActivity.class);
        startActivity(intent);
    }
    /** Called when the user clicks the Send button */
    public void RefreshClicked(View view) {
        // Do something in response to button
        refreshCrimeList();
    }

    private void refreshCrimeList()
    {
        final TextView mTextView = (TextView) findViewById(R.id.textView2);
        mTextView.setText("Updating Alerts");
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.uc.edu/webapps/publicsafety/policelog2.aspx";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mMySingleton.setmCrimePage(response);
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
