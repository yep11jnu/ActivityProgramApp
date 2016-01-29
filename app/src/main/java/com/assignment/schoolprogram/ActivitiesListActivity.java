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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 6266991
 */
public class ActivitiesListActivity  extends Activity {
    int day;
    int month;
    int year;
    String date;
    static HashMap<String, String> alarmTimes = new HashMap<>();
    AlarmReceiver alarmReceiver = new AlarmReceiver();

    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);

        Intent intent = getIntent();
        day = intent.getIntExtra(DateListActivity.DAY, 1);
        month = intent.getIntExtra(DateListActivity.MONTH, 1);
        year = intent.getIntExtra(DateListActivity.YEAR, 2016);
        date = intent.getStringExtra(DateListActivity.DATE);

        //Generate list View from ArrayList
        displayListView();
        checkButtonClick();
        settingsButtonClick();
    }

    private void settingsButtonClick() {
        Button myButton = (Button) findViewById(R.id.settings);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPreferenceActivity();
            }
        });

    }

    private void showPreferenceActivity() {
        Intent intent = new Intent(ActivitiesListActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayListView() {

        //Array list of countries
        ArrayList<SchoolActivity> activityList = getActivities();;
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.activity_item, activityList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                SchoolActivity activity = (SchoolActivity) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(),
//                        "Clicked on Row: " + activity.getName(),
//                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setAlarmForActivity(String start, String name) {
        alarmTimes.put(name, start);
    }

    private void cancelAlarmForActivity(String name) {
        alarmTimes.remove(name);
    }

    private ArrayList<SchoolActivity> getActivities () {
        ArrayList<SchoolActivity> activities = new ArrayList<>();
        InputStream instream = null;
        try {
            // open the file for reading
            instream = SchoolListActivity.getProgramFromFile();
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
                    String[] tokens = line.split(",");
                    if (tokens.length == 5) {
                        // SchoolActivity(String code, String name, String start, String end, boolean selected)
                        Log.d("SA", line);
                        Log.d("DA", date);
                        SchoolActivity school = new SchoolActivity(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], false);
                        if (date.equals(tokens[4].trim())) {
                            activities.add(school);
                        }
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

    private class MyCustomAdapter extends ArrayAdapter<SchoolActivity> {

        private ArrayList<SchoolActivity> activityList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<SchoolActivity> activityList) {
            super(context, textViewResourceId, activityList);
            this.activityList = new ArrayList<SchoolActivity>();
            this.activityList.addAll(activityList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));
            final SchoolActivity activity = activityList.get(position);

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.activity_item, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        //SchoolActivity activity = (SchoolActivity) cb.getTag();
                        String start = activity.getStart();
                        String name = activity.getName();

//                        Toast.makeText(getApplicationContext(),
//                                "Clicked on Checkbox: " + cb.getText() +
//                                        " is " + name,
//                                Toast.LENGTH_LONG).show();
                        activity.setSelected(cb.isChecked());
                        if (cb.isChecked()) {
                            setAlarmForActivity(start, name);
                        } else {
                            cancelAlarmForActivity(name);
                        }
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.code.setText(" (" + activity.getStart() + ")");
            holder.name.setText(activity.getName());
            holder.name.setChecked(activity.isSelected());
            holder.name.setTag(activity);

            return convertView;

        }
    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAlarms();
                Toast.makeText(getApplicationContext(),
                        "Alarms for the selected activities have been set", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setAlarms() {
        alarmReceiver.setAlarm(this, alarmTimes, day, month, year);
    }

    public void onBackPressed() {
        Intent intent = new Intent(ActivitiesListActivity.this, DateListActivity.class);
        intent.putExtra(DateListActivity.DAY, day);
        intent.putExtra(DateListActivity.MONTH, month);
        intent.putExtra(DateListActivity.YEAR, year);
        startActivity(intent);
        finish();
    }
}
