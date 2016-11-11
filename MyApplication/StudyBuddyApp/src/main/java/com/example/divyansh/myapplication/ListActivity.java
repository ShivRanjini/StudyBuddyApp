package com.example.divyansh.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class ListActivity extends Activity {
    static final String KEY_ID = "id";
    static final String KEY_SUBJECT= "subject";
    static final String KEY_GROUP = "group";
    static final String KEY_DURATION = "duration";
    static final String START_TIME = "start_time";
    static final String LOCATION = "location";


    ListView list;
    LazyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        StudyGroups[] studygroups = (StudyGroups[]) getIntent().getSerializableExtra("StudyGroups") ;
        list=(ListView)findViewById(R.id.list);

        adapter=new LazyAdapter(this, getListOfGroups(studygroups));
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ImageView like_image=(ImageView)view.findViewById(R.id.star);
                if(like_image.getTag().equals("star_off")) {
                    like_image.setImageResource(android.R.drawable.btn_star_big_on);
                    like_image.setTag("star_on");
                }
                else
                {
                    like_image.setImageResource(android.R.drawable.btn_star_big_off);
                    like_image.setTag("star_off");
                }

            }
        });
    }

    public ArrayList<HashMap<String,String>> getListOfGroups(StudyGroups[] studygroups) {
        ArrayList<HashMap<String, String>> GroupList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> groupinfo;
        if(studygroups != null) {
            for (int i = 0; i < studygroups.length; i++) {
                groupinfo = new HashMap<String, String>();
                groupinfo.put(KEY_ID, Integer.toString(i));
                groupinfo.put(KEY_SUBJECT, studygroups[i].mSubjectId);
                groupinfo.put(KEY_GROUP, studygroups[i].mGroupName);
                Date date = new Date(studygroups[i].mStartTime);
                DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("Etc/PDT"));
                String starttime = format.format(date);
                Date enddate = new Date(studygroups[i].mEndTime);
                groupinfo.put(KEY_DURATION,  Long.toString(((enddate.getTime()-date.getTime())/(60 * 60 * 1000)))+" hrs");
                groupinfo.put(LOCATION,studygroups[i].mLocationName);
                groupinfo.put(START_TIME,starttime);
                GroupList.add(groupinfo);

            }
        }
        return GroupList;

    }

}
