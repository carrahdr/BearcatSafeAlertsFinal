package edu.uc.bearcatsafealerts;

import android.icu.text.StringPrepParseException;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave Carraher on 4/8/2017.
 * This Singleton holds all of the data that needs to be shared by multiple activities
 * In this case, it holds all of the crime alert data, and the methods for parsing that data
 */

class MySingleton {
    // Make sure this is a static singleton object with only one instance per application
    private static final MySingleton ourInstance = new MySingleton();
    private static String mCrimePage;
    public static List<String[]> mCrimeList = new ArrayList<String[]>();
    private static int mCount=0;
    private static String mCrime;

    // getter for this single instance
    static MySingleton getInstance() {
        return ourInstance;
    }

    private MySingleton() {
    }

    // setter for the crime alert web page html response string
    public static void setmCrimePage(String mCrimePage) {
        MySingleton.mCrimePage = mCrimePage;
    }

    // This method parses the web page string to pull out the table of crime alert data and store it
    // in an ArrayList of Strings
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
        // Check to make sure the table was found, first.  If not, use stored string map data
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
            // If the next character isn't a tag, it must be the string data we want
            if(tableData.charAt(ptr+1)!='<')
            {
                // Get the pointer to the next html tag, and store this string in our array of header strings
                nxtptr = tableData.indexOf("<",ptr+1);
                headers[column] = tableData.substring(ptr+1,nxtptr);
                column++;
            }
            // move past the current html tag
            ptr = tableData.indexOf(">",ptr+1);
            // check to see if we've reached the end of the table row
            checkString = tableData.substring(ptr+1,ptr+6);
            if(checkString.equals("</tr>"))break;
        }
        // Make sure we got valid list of columns
        if(column==0)return 0;
        numColumns = column;
        // Store our columns in the string array list
        mCrimeList.add(headers);
        // Now, repeat process above for the actual data of each table row, until we've read all rows into the List
        // Check for the end of table html tag to know when we are done
        while((!tableData.substring(ptr+6,ptr+14).equals("</table>"))&&(ptr>=0))
        {
            // Set up our array of strings for this crime alert row
            String[] rowData = new String[8];
            column = 0;
            // Go to next html tag
            ptr = tableData.indexOf(">",ptr+1);
            while(ptr >= 0)
            {
                // if the next character isn't another tag, it is the string data we want
                if(tableData.charAt(ptr+1)!='<')
                {
                    nxtptr = tableData.indexOf("<",ptr+1);
                    rowData[column] = tableData.substring(ptr+1,nxtptr);
                    column++;
                }
                // Go to next tag, and check for end of row
                ptr = tableData.indexOf(">",ptr+1);
                if(ptr+6>tableData.length())break;
                checkString = tableData.substring(ptr+1,ptr+6);
                if(checkString.equals("</tr>"))break;
            }
            // Make sure we got valid columns of data.  If so, add this row to our list
            if(column==0)break;
            numRows++;
            mCrimeList.add(rowData);
        }
        mCount = numRows;
        // Return the # of alerts that we found
        return numRows;
    }

    // getter for the count of alerts
    public static int getmCount() {
        return mCount;
    }

    // getter for the string holding the current selected crime
    public static String getmCrime() {
        return mCrime;
    }

    // setter for the string holding the current selected crime
    public static void setmCrime(String mCrime) {
        MySingleton.mCrime = mCrime;
    }
}
