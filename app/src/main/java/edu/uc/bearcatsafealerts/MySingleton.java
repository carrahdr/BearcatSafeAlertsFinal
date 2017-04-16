package edu.uc.bearcatsafealerts;

import android.icu.text.StringPrepParseException;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A62085 on 4/8/2017.
 */

class MySingleton {
    private static final MySingleton ourInstance = new MySingleton();
    private static String mCrimePage;
    public static List<String[]> mCrimeList = new ArrayList<String[]>();
    private static int mCount=0;
    private static String mCrime;

    static MySingleton getInstance() {
        return ourInstance;
    }

    private MySingleton() {
    }

    public static void setmCrimePage(String mCrimePage) {
        MySingleton.mCrimePage = mCrimePage;
    }

    public static int parseCrimePage()
    {
        // Create variables to find table of crime alerts
        int tableStart;
        int tableEnd;
        String tableData;
        // Find the table by looking for the words "Results For", then the first table row tag (<tr>) after that
        tableStart = mCrimePage.indexOf("Results For");
        tableStart = mCrimePage.indexOf("<tr>",tableStart);
        // Find the end of the table by looking for the </table> tag
        tableEnd = mCrimePage.indexOf("</table>",tableStart);
        // Check to make sure the table was found, first
        if((tableStart<=0)||(tableEnd<=0)||(tableEnd<=tableStart))return 0;
        // Once we have the table, extract just the table string for parsing
        tableData = mCrimePage.substring(tableStart,tableEnd+8);
        // First, get the "columns" of our data
        int ptr = 0;
        int nxtptr = 0;
        String[] headers = new String[8];
        String checkString;
        int column = 0;
        int numColumns = 0;
        int numRows = 0;
        // Comb through the tags <> until we find the untagged text, which will be the data of the table cell
        ptr = tableData.indexOf(">");
        while(ptr >= 0)
        {
            if(tableData.charAt(ptr+1)!='<')
            {
                nxtptr = tableData.indexOf("<",ptr+1);
                headers[column] = tableData.substring(ptr+1,nxtptr);
                column++;
            }
            ptr = tableData.indexOf(">",ptr+1);
            checkString = tableData.substring(ptr+1,ptr+6);
            if(checkString.equals("</tr>"))break;
        }
        // Make sure we got valid list of columns
        if(column==0)return 0;
        numColumns = column;
        mCrimeList.add(headers);
        // Now, repeat process until we've read all rows into the List
        while((!tableData.substring(ptr+6,ptr+14).equals("</table>"))&&(ptr>=0))
        {
            String[] rowData = new String[8];
            column = 0;
            ptr = tableData.indexOf(">",ptr+1);
            while(ptr >= 0)
            {
                if(tableData.charAt(ptr+1)!='<')
                {
                    nxtptr = tableData.indexOf("<",ptr+1);
                    rowData[column] = tableData.substring(ptr+1,nxtptr);
                    column++;
                }
                ptr = tableData.indexOf(">",ptr+1);
                if(ptr+6>tableData.length())break;
                checkString = tableData.substring(ptr+1,ptr+6);
                if(checkString.equals("</tr>"))break;
            }
            // Make sure we got valid columns of data
            if(column==0)break;
            numRows++;
            mCrimeList.add(rowData);
        }
        mCount = numRows;
        return numRows;
    }

    public static int getmCount() {
        return mCount;
    }

    public static String getmCrime() {
        return mCrime;
    }

    public static void setmCrime(String mCrime) {
        MySingleton.mCrime = mCrime;
    }
}
