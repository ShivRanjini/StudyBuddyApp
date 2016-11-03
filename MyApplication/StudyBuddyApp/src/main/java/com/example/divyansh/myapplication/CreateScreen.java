package com.example.divyansh.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateScreen extends AppCompatActivity implements View.OnClickListener {
    TextView textView;
    SeekBar seekBar;
    private EditText startdate;
    private EditText enddate;
    private  EditText starttime;
    private EditText endtime;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;
    private SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_screen);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        seekBar = (SeekBar) findViewById(R.id.Capacity);
        textView = (TextView) findViewById(R.id.CapacityDescription);
        textView.setText("Capacity is  " + seekBar.getProgress() + "/" + seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                textView.setText("Capacity is  " + seekBar.getProgress() + "/" + seekBar.getMax());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textView.setText("Capacity is  " + seekBar.getProgress() + "/" + seekBar.getMax());
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Capacity is  " + seekBar.getProgress() + "/" + seekBar.getMax());
            }
        });
        setViews();
        setDate();
        setTime();
        populateSubjectDropDown();
    }
    private void setViews() {
        startdate = (EditText) findViewById(R.id.start_date);
        startdate.setInputType(InputType.TYPE_NULL);
        startdate.requestFocus();

        enddate = (EditText) findViewById(R.id.end_date);
        enddate.setInputType(InputType.TYPE_NULL);
        enddate.requestFocus();

        starttime = (EditText) findViewById(R.id.start_time);
        starttime.setInputType(InputType.TYPE_NULL);
        starttime.requestFocus();

        endtime = (EditText) findViewById(R.id.end_time);
        endtime.setInputType(InputType.TYPE_NULL);
        endtime.requestFocus();
    }

    private void setTime() {
        starttime.setOnClickListener(this);
        endtime.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
                starttime.setText( Hour + ":" + Minute);
            }
        }, newCalendar.HOUR_OF_DAY, newCalendar.MINUTE, true);//Yes 24 hour time

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
        endtime.setText( Hour + ":" + Minute);
        }
        }, newCalendar.HOUR_OF_DAY, newCalendar.MINUTE, true);//Yes 24 hour time


    }
    private void setDate() {
        startdate.setOnClickListener(this);
        enddate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startdate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                enddate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    protected void populateSubjectDropDown()
    {
        ArrayList<String> subjects=new ArrayList<String>();
        subjects.add("Enter Subject Name");
        subjects.add("Geospatial Information System");
        subjects.add("Machine Learning");
        Spinner mSpinner = (Spinner) findViewById(R.id.Subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,subjects);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        if(view == startdate) {
            fromDatePickerDialog.show();
        } else if(view == enddate) {
            toDatePickerDialog.show();
        }
        else if(view == starttime) {
            fromTimePickerDialog.show();
        } else if(view == endtime) {
            toTimePickerDialog.show();
        }
    }
}
