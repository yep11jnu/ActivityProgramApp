package com.assignment.schoolprogram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 6266991
 */
public class DateListActivity extends Activity {

    MyCustomAdapter dataAdapter = null;
    private int year;
    private int month;
    private int day;
    private String date;

    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String DATE = "date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_list);

        //Generate list View from ArrayList
        displayListView();

        Intent intent = getIntent();
        String school = intent.getStringExtra("SCHOOL");
    }

    private void displayListView() {

        //Array list of countries
        ArrayList<String> activityList = getDates();;
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.date_item, activityList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                String activity = (String) parent.getItemAtPosition(position);
                String[] toks = activity.split("-");

                day = Integer.parseInt(toks[0]);
                month = Integer.parseInt(toks[1]);
                year = Integer.parseInt(toks[2]);
                date = activity;
                Toast.makeText(getApplicationContext(),
                        "Select: " + activity,
                        Toast.LENGTH_LONG).show();
                moveToActivities();
            }
        });

    }

    private void moveToActivities() {
        Intent intent = new Intent(DateListActivity.this, ActivitiesListActivity.class);
        intent.putExtra(DAY, day);
        intent.putExtra(MONTH, month);
        intent.putExtra(YEAR, year);
        intent.putExtra(DATE, date.trim());
        startActivity(intent);
        finish();
    }

    private ArrayList<String> getDates() {
        ArrayList<String> activities = new ArrayList<>();
        InputStream instream = null;
        try {
            // open the file for reading
            instream = SchoolListActivity.getDatesFromFile();
            if (instream == null) {
                Toast.makeText(this, "Place your program.csv in directory: " + SchoolListActivity.getSchoolDir(), Toast.LENGTH_LONG).show();
            }
            // if file the available for reading
            if (instream != null) {
                // prepare the file for reading
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                String line;

                // read every line of the file into the line-variable, on line at the time
                do {
                    line = buffreader.readLine();
                    if (line.length() > 7) {
                        // String(String code, String name, String start, String end, boolean selected)
                        activities.add(line.trim());
                    }
                    // do something with the line
                } while (line != null);

            }
        } catch (Exception ex) {
            // print stack trace.
        } finally {
            // close the file.
            //instream.close();
        }
        return activities;
    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> activityList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> activityList) {
            super(context, textViewResourceId, activityList);
            this.activityList = new ArrayList<String>();
            this.activityList.addAll(activityList);
        }

        private class ViewHolder {
            TextView code;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            final String activity = activityList.get(position);

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.date_item, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);;
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.code.setText(activity);

            return convertView;

        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(DateListActivity.this, SchoolListActivity.class);
        startActivity(intent);
        finish();
    }
}
