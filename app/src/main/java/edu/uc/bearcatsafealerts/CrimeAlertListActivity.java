package edu.uc.bearcatsafealerts;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import static edu.uc.bearcatsafealerts.R.attr.height;

public class CrimeAlertListActivity extends AppCompatActivity {

    private MySingleton mMySingleton;
    private List<String[]> mAlertList;

    // On Creation of this activity, get the singleton object that has the shared crime alert data
    // and then load the alerts to our scrollable page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_alert_list);
        mMySingleton = MySingleton.getInstance();
        loadAlerts();
    }

    // This method loads the alerts from the list of alerts loaded by the singleton object
    private void loadAlerts() {
        // Get the number of alerts and the list from the singleton
        int nNumAlerts = mMySingleton.getmCount();
        mAlertList = mMySingleton.mCrimeList;
        TableLayout tlTable;
        TableRow trRow;
        TextView tv;
        tlTable = (TableLayout)findViewById(R.id.LOGTBLID);
        // Clear the table layout so that we start with a blank page
        tlTable.removeAllViews();
        // If there aren't any crime alerts, display a message to that effect
        if(nNumAlerts == 0)
        {
            tlTable.addView(new TableRow(this));
            trRow = (TableRow)tlTable.getChildAt(0);
            trRow.addView(new TextView(this));
            tv = (TextView)trRow.getChildAt(0);
            tv.setText("No Crime Alerts Available for Display");
            return;
        }
        // Get the "headers" of each column of data (e.g. Campus, Date, Address, etc.)
        String[] strarrHeaders = mAlertList.get(0);
        int nCurrentRow = 0;
        // Step through the list of alerts one at a time, populating the Table
        for(int i = 0; i<nNumAlerts; i++)
        {
            String[] nxtAlert = mAlertList.get(i+1);
            int nNumFields = nxtAlert.length;
            // For each Alert, create a separate TextView line to hold the text for that header/text line of the alert
            for(int j = 0; j < nNumFields; j++) {
                // Add the next row/textview to the tablelayout
                tlTable.addView(new TableRow(this));
                trRow = (TableRow) tlTable.getChildAt(nCurrentRow++);
                trRow.addView(new TextView(this));
                tv = (TextView) trRow.getChildAt(0);
                tv.setTextColor(Color.BLACK);
                // set the text of the textview = header + alert string (e.g. "Campus: WEST CAMPUS")
                if (strarrHeaders[j] != null)
                    tv.setText(strarrHeaders[j] + ": " + nxtAlert[j]);
                // If this is the Crime column (column 4), change the color and set a click listener for the user to tap
                if (j == 4) {
                    tv.setTextColor(Color.BLUE);
                    final TextView finalTv = tv;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Jump to Map view at this location.  We do this using the shared singleton - we set the crime text value that was clicked
                            String str = finalTv.getText().toString();
                            mMySingleton.setmCrime(str);
                            Intent intent = new Intent(v.getContext(), MapsActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
            // Add a new row for a blank line between alerts
            tlTable.addView(new TableRow(this));
            trRow = (TableRow)tlTable.getChildAt(nCurrentRow++);
        }
    }
}
