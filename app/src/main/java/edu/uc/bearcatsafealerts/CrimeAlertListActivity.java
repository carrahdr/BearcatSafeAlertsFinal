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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_alert_list);
        mMySingleton = MySingleton.getInstance();
        loadAlerts();
    }
    /** Called when the user clicks the Send button */
    public void RefreshClicked(View view) {
        // Do something in response to button
        // Go to Map at this location();
    }

    private void loadAlerts() {
        int nNumAlerts = mMySingleton.getmCount();
        mAlertList = mMySingleton.mCrimeList;
        TableLayout tlTable;
        TableRow trRow;
        TextView tv;
        tlTable = (TableLayout)findViewById(R.id.LOGTBLID);
        tlTable.removeAllViews();
        if(nNumAlerts == 0)
        {
            tlTable.addView(new TableRow(this));
            trRow = (TableRow)tlTable.getChildAt(0);
            trRow.addView(new TextView(this));
            tv = (TextView)trRow.getChildAt(0);
            tv.setText("No Crime Alerts Available for Display");
        }
        String[] strarrHeaders = mAlertList.get(0);
        int nCurrentRow = 0;
        for(int i = 0; i<nNumAlerts; i++)
        {
            String[] nxtAlert = mAlertList.get(i+1);
            int nNumFields = nxtAlert.length;
            for(int j = 0; j < nNumFields; j++) {
                tlTable.addView(new TableRow(this));
                trRow = (TableRow) tlTable.getChildAt(nCurrentRow++);
                trRow.addView(new TextView(this));
                tv = (TextView) trRow.getChildAt(0);
                tv.setTextColor(Color.BLACK);
                if (strarrHeaders[j] != null)
                    tv.setText(strarrHeaders[j] + ": " + nxtAlert[j]);
                if (j == 4) {
                    tv.setTextColor(Color.BLUE);
                    final TextView finalTv = tv;
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Add code to jump to Map view at this location
                            String str = finalTv.getText().toString();
                            mMySingleton.setmCrime(str);
                            Intent intent = new Intent(v.getContext(), MapsActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
            tlTable.addView(new TableRow(this));
            trRow = (TableRow)tlTable.getChildAt(nCurrentRow++);
        }
    }
}
