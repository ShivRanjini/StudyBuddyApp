package com.example.divyansh.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class CreateScreen extends AppCompatActivity implements View.OnClickListener {
    TextView textView;
    Spinner mSpinner;
    SeekBar seekBar;
    Button createButton;
    HashMap<String,String> mSubjectsMap;
    private EditText startdate;
    private EditText enddate;
    private  EditText starttime;
    private EditText endtime;
    private  EditText groupName;
    private EditText topicDetails;
    private  EditText LocationDetails;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ImageButton startdtbtn;
    private ImageButton starttimbtn;
    private  ImageButton enddtbtn;
    private  ImageButton endtimbtn;
    private StudyGroups mStudyGroup;
    private double latitude;
    private  double longitude;
    String user = "divyansh";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_screen);
        //user = getIntent().getStringExtra("username");
        user = getUser();
        latitude =(double) getIntent().getDoubleExtra("Latitude",0.0);
        longitude =(double) getIntent().getDoubleExtra("Longitude",0.0);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        seekBar = (SeekBar) findViewById(R.id.Capacity);
        textView = (TextView) findViewById(R.id.CapacityDescription);
        createButton = (Button) findViewById(R.id.create);
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
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName = (EditText) findViewById(R.id.GroupName);
                topicDetails = (EditText) findViewById(R.id.Topic);
                LocationDetails = (EditText) findViewById(R.id.LocationDetails);
                String grpName = groupName.getText().toString();
                String subid="";
                Iterator it = mSubjectsMap.entrySet().iterator();
                String selected_sub = (String)mSpinner.getSelectedItem();
                while (it.hasNext()) {
                    Map.Entry subidpair = (Map.Entry) it.next();
                    String sub = (String) subidpair.getValue();
                    if(selected_sub.equals(sub))
                    {
                        subid = subidpair.getKey().toString();
                    }

                }
                String topic = topicDetails.getText().toString();
                String locdet = LocationDetails.getText().toString();
                String cap = Integer.toString(seekBar.getProgress());
                String starttimestamp="";
                String endtimestamp="";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                try {
                    Date startdateandtime = dateFormat.parse(startdate.getText()+" "+starttime.getText());
                    Date enddateandtime = dateFormat.parse(enddate.getText()+" "+endtime.getText());
                    starttimestamp = Long.toString(startdateandtime.getTime());
                    endtimestamp = Long.toString(enddateandtime.getTime());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CreateGroup postReq = new CreateGroup();
                postReq.execute(subid,grpName,user,starttimestamp,endtimestamp,cap,topic,locdet,Double.toString(latitude),Double.toString(longitude));
                Intent MapIntent = new Intent(CreateScreen.this,MapsActivity.class);
                startActivity(MapIntent);



            }
        });
        setViews();
        setDate();
        setTime();
        populateSubjectDropDown();

    }
    private void setViews() {
        startdate = (EditText) findViewById(R.id.start_date);
        enddate = (EditText) findViewById(R.id.end_date);
        starttime = (EditText) findViewById(R.id.start_time);
        endtime = (EditText) findViewById(R.id.end_time);
        startdtbtn = (ImageButton) findViewById(R.id.startdtbtn);
        starttimbtn = (ImageButton) findViewById(R.id.starttimbtn);
        enddtbtn = (ImageButton) findViewById(R.id.enddtbtn);
        endtimbtn = (ImageButton) findViewById(R.id.endtimbtn);

    }

    private void setTime() {
        starttimbtn.setOnClickListener(this);
        endtimbtn.setOnClickListener(this);

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
        startdtbtn.setOnClickListener(this);
        enddtbtn.setOnClickListener(this);

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
    public String getUser(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String uname = sharedpreferences.getString("username", "divyansh");//"No name defined" is the default value.
        return uname;
    }
    protected void populateSubjectDropDown()
    {
        ArrayList<String> subjects=new ArrayList<String>();
        mSubjectsMap = new SubjectsMap().getSubjectMap();
        Iterator it = mSubjectsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry subidpair = (Map.Entry) it.next();
            subjects.add((String) subidpair.getValue());

        }
        mSpinner = (Spinner) findViewById(R.id.Subject);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,subjects);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        if(view == startdtbtn) {
            fromDatePickerDialog.show();
        } else if(view == enddtbtn) {
            toDatePickerDialog.show();
        }
        else if(view == starttimbtn) {
            fromTimePickerDialog.show();
        } else if(view == endtimbtn) {
            toTimePickerDialog.show();
        }
    }
}


class CreateGroup extends AsyncTask<String,String,String> {
    @Override
    protected String doInBackground(String... params) {
        StringBuffer response = new StringBuffer();
        try {
            String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/createGroup";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"subjectId\": \""+params[0]+"\",\"groupName\": \""+params[1]+"\",\"adminId\": \""+params[2]+"\",\"startTimestamp\": "+params[3]+",\"endTimestamp\": "+params[4]+",\"capacity\": "+params[5]+",\"numMembers\": 1,\"topic\": \""+params[6]+"\",\"locationName\": \""+params[7]+"\",\"latitude\": "+params[8]+",\"longitude\": "+params[9]+"}";
            Log.d("bjbj",input);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return "group created";
    }
}

