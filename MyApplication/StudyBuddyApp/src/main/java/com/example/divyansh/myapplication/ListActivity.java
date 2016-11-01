package com.example.divyansh.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends Activity {
    // XML node keys
    static final String KEY_ID = "id";
    static final String KEY_SUBJECT= "subject";
    static final String KEY_GROUP = "group";
    static final String KEY_DURATION = "duration";


    ListView list;
    LazyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

        // looping through all song nodes <song>

        // creating new HashMap
        HashMap<String, String> map = new HashMap<String, String>();
        // adding each child node to HashMap key => value
        map.put(KEY_ID, "1");
        map.put(KEY_GROUP,"Nearst Neighbours");
        map.put(KEY_SUBJECT, "Geospatial Information Systems");
        map.put(KEY_DURATION, "3:30 pm - 5:50 pm");

        // adding HashList to ArrayList
        songsList.add(map);
        map = new HashMap<String, String>();
        // adding each child node to HashMap key => value
        map.put(KEY_ID, "2");
        map.put(KEY_GROUP,"Group 2");
        map.put(KEY_SUBJECT, "Machine Learning");
        map.put(KEY_DURATION, "4:30 am - 7:00 am");

        // adding HashList to ArrayList
        songsList.add(map);
        map = new HashMap<String, String>();
        // adding each child node to HashMap key => value
        map.put(KEY_ID, "3");
        map.put(KEY_GROUP,"Star Wars");
        map.put(KEY_SUBJECT, "Algorithm");
        map.put(KEY_DURATION, "2:00 pm - 5:00 pm");

        // adding HashList to ArrayList
        songsList.add(map);
        map = new HashMap<String, String>();
        // adding each child node to HashMap key => value
        map.put(KEY_ID, "4");
        map.put(KEY_GROUP,"Smart Minds");
        map.put(KEY_SUBJECT, "Computer Networks");
        map.put(KEY_DURATION, "1:00 pm - 3:00 pm");

        // adding HashList to ArrayList
        songsList.add(map);
        map = new HashMap<String, String>();
        // adding each child node to HashMap key => value
        map.put(KEY_ID, "5");
        map.put(KEY_GROUP,"Dream Big");
        map.put(KEY_SUBJECT, "Machine Learning");
        map.put(KEY_DURATION, "9:00 am - 10:00 am");

        // adding HashList to ArrayList
        songsList.add(map);

        list=(ListView)findViewById(R.id.list);

        // Getting adapter by passing xml data ArrayList
        adapter=new LazyAdapter(this, songsList);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });
    }
}
