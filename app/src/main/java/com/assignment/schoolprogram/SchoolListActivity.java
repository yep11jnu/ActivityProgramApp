package com.assignment.schoolprogram;
/**
 * 6266991
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SchoolListActivity extends Activity {

    MyCustomAdapter dataAdapter = null;
    public static final String APP_DIR = "schools";
    Button settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        //Generate list View from ArrayList
        displayListView();
        createSchoolsDir();
        DownloadService.activity = this;
        startService(new Intent(this, DownloadService.class));
        settingsButtonClick();
        // selects first school as default, second option allows user to select different schools
        // can be editted for extensibility
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String mode  = sp.getString("programType", "2");
        if (mode.equals("1")) {
            moveToDateActivity();
        }
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
        Intent intent = new Intent(SchoolListActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private ArrayList<School> getSchools() {
        ArrayList<School> schools = new ArrayList<>();
        InputStream instream = null;
        try {
            // open the file for reading
            instream = getSchoolsFromFile();
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
                    if (tokens.length == 2) {
                        School school = new School(tokens[0], tokens[1]);
                        schools.add(school);
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
        return schools;
    }

    private void displayListView() {
        //Array list of countries
        ArrayList<School> activityList = getSchools();
        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.school_item, activityList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                School activity = (School) parent.getItemAtPosition(position);
                moveToDateActivity();
            }
        });
    }

    private void moveToDateActivity() {
        Intent intent = new Intent(SchoolListActivity.this, DateListActivity.class);
        startActivity(intent);
        finish();
    }

    public InputStream getSchoolsFromFile() throws FileNotFoundException {
        String filename = getSchoolDir() + "/schools.csv";
        File file = new File(filename);
        if (!file.exists()) {
            Toast.makeText(this, "Place your schools.csv in directory: " + getSchoolDir(), Toast.LENGTH_LONG).show();
        }
        return new FileInputStream(file);
    }

    public static InputStream getProgramFromFile() throws FileNotFoundException {
        String filename = getSchoolDir() + "/program.csv";
        File file = new File(filename);
        return new FileInputStream(file);
    }

    public static InputStream getDatesFromFile() throws FileNotFoundException {
        String filename = getSchoolDir() + "/dates.csv";
        File file = new File(filename);
        return new FileInputStream(file);
    }

    public static String getSchoolDir() {
        return Environment.getExternalStorageDirectory() + "/" + APP_DIR;
    }

    public void createSchoolsDir() {
        File externalDir = Environment.getExternalStorageDirectory();
        File appDir = new File(externalDir, APP_DIR);
        if(!appDir.exists()) {
            appDir.mkdirs();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<School> {

        private ArrayList<School> activityList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<School> activityList) {
            super(context, textViewResourceId, activityList);
            this.activityList = new ArrayList<School>();
            this.activityList.addAll(activityList);
        }

        private class ViewHolder {
            ImageView logo;
            TextView name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.school_item, null);

                holder = new ViewHolder();
                holder.logo = (ImageView) convertView.findViewById(R.id.logo);
                holder.name = (TextView) convertView.findViewById(R.id.label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            School activity = activityList.get(position);
            //holder.logo.setImageDrawable(getDrawable(R.drawable.logo));
            holder.name.setText(activity.getName());

            return convertView;

        }
    }
}
