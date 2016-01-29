package com.assignment.schoolprogram;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * 6266991
 */
public class OpenDayPicker extends Activity {

    private TextView openDayDate;
    private DatePicker datePicker;
    private Button setDate;
    private Button toActivities;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;
    public static final String DAY = "day";
    public static final String MONTH = "month";
    public static final String YEAR = "year";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datepicker);

        setCurrentDateOnView();
        addListenerOnButton();

    }

    // display current date
    public void setCurrentDateOnView() {

        openDayDate = (TextView) findViewById(R.id.tvDate);
        datePicker = (DatePicker) findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        year = getIntent().getIntExtra(YEAR, year);
        month = getIntent().getIntExtra(MONTH, month);
        day = getIntent().getIntExtra(DAY, day);

        // set current date into textview
        openDayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
        datePicker.init(year, month, day, null);

    }

    public void addListenerOnButton() {

        setDate = (Button) findViewById(R.id.btnChangeDate);
        toActivities = (Button) findViewById(R.id.btnActivities);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        toActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToActivities();
            }
        });
    }

    private void moveToActivities() {
        Intent intent = new Intent(OpenDayPicker.this, ActivitiesListActivity.class);
        intent.putExtra(DAY, day);
        intent.putExtra(MONTH, month);
        intent.putExtra(YEAR, year);
        startActivity(intent);
        finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    public void onBackPressed() {
        Intent intent = new Intent(OpenDayPicker.this, SchoolListActivity.class);
        startActivity(intent);
        finish();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            openDayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            datePicker.init(year, month, day, null);

        }
    };

}
