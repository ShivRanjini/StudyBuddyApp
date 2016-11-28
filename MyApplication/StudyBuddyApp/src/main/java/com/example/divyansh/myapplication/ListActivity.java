package com.example.divyansh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
import java.security.acl.Group;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ListActivity extends Activity {
    static final String KEY_ID = "id";
    static final String KEY_SUBJECT= "subject";
    static final String KEY_GROUP_ID= "group_id";
    static final String KEY_GROUP = "group";
    static final String KEY_DURATION = "duration";
    static final String START_TIME = "start_time";
    static final String LOCATION = "location";
    static final String ISFAV = "isfav";
    String username = "ranjini";
    ArrayList<HashMap<String, String>> GroupList;
    List<StudyGroups> groupList;
    StudyGroups[] studygroups;
    ListView list;
    LazyAdapter adapter;
    ArrayList<Integer> joinedgrouplist;
    String type;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        type =  getIntent().getStringExtra("CallType");
        //username = getIntent().getStringExtra("username");
        username = getUser();
        list=(ListView)findViewById(R.id.list);
        if(type.equals("ListView")) {
            studygroups = (StudyGroups[]) getIntent().getSerializableExtra("StudyGroups") ;
            new GetFavGroups().execute(username);
        }
        else if(type.equals("JoinedGroup")) {
            new GetJoinedGroups().execute(username);
            new GetAllStudyGroups().execute();

        }
        else if(type.equals("Favorite"))
        {
            new GetAllStudyGroups().execute();
            new GetFavGroups().execute(username);
        }
        else if(type.equals("MyGroup"))
        {
            new GetAllStudyGroups().execute();
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ImageView like_image=(ImageView)view.findViewById(R.id.star);
                if(like_image.getTag().equals("star_off")) {
                    like_image.setImageResource(R.drawable.fav_on);
                    FavGroup postReq = new FavGroup();
                    postReq.execute(username,groupList.get(position).mGroupId);

                    like_image.setTag("star_on");
                }
                else if(like_image.getTag().equals("delete"))
                {
                    DeleteGroup postReq = new DeleteGroup();
                    postReq.execute(GroupList.get(position).get(KEY_GROUP_ID));
                    GroupList.remove(position);
                    adapter.notifyDataSetChanged();
                }
                else if((like_image.getTag().equals("star_on")))
                {
                    like_image.setImageResource(R.drawable.fav_off);
                    like_image.setTag("star_off");
                }

            }
        });

    }
    public String getUser(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String uname = sharedpreferences.getString("username", "divyansh");//"No name defined" is the default value.
        return uname;
    }
    @Override
    public void onBackPressed() {
        if(type.equals("MyGroup")) {

            Intent mapintent = new Intent(ListActivity.this, MapsActivity.class);
            startActivity(mapintent);
            finish();
        }
        else
            finish();
    }
    public void setListOfGroups(StudyGroups[] studygroups,List<Integer> favGroup) {
        GroupList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> groupinfo;
        groupList = new ArrayList<StudyGroups>();
        for (int i = 0; i < studygroups.length; i++){
            groupList.add(studygroups[i]);
        }
        Collections.sort(groupList, new Comparator<StudyGroups>() {
            @Override
            public int compare(StudyGroups o1, StudyGroups o2) {
                return (int)(o1.mStartTime - o2.mStartTime);
            }
        });
        if(studygroups != null) {
            for (int i = 0; i < groupList.size(); i++) {
                if(type.equals("JoinedGroup")||type.equals("Favorite"))
                {   int flag = 0;
                    for(int j=0;j<favGroup.size();j++)
                    {
                        if(favGroup.get(j) == Integer.parseInt(groupList.get(i).mGroupId))
                        {
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 0)
                        continue;
                }
                if(type.equals("MyGroup"))
                {
                   if(!groupList.get(i).mAdminId.equals(username)) {
                       continue;

                    }
                }
               groupinfo = new HashMap<String, String>();
                groupinfo.put(KEY_ID, Integer.toString(i));
                groupinfo.put(KEY_SUBJECT, groupList.get(i).mSubjectId);
                groupinfo.put(KEY_GROUP, groupList.get(i).mGroupName);
                groupinfo.put(KEY_GROUP_ID, groupList.get(i).mGroupId);
                if(type.equals("ListView")) {
                    groupinfo.put(ISFAV, "0");
                    for (int j = 0; j < favGroup.size(); j++) {
                        if (favGroup.get(j) == Integer.parseInt(groupList.get(i).mGroupId)) {
                            groupinfo.put(ISFAV, "1");
                            break;
                        }

                    }
                }
                else if(type.equals("JoinedGroup")||type.equals("Favorite"))
                {
                    groupinfo.put(ISFAV, "3");
                }
                else if(type.equals("MyGroup"))
                {
                    groupinfo.put(ISFAV, "2");
                }
                Date date = new Date(groupList.get(i).mStartTime);
                DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("Etc/PDT"));
                String starttime = format.format(date);
                Date enddate = new Date(groupList.get(i).mEndTime);
                groupinfo.put(KEY_DURATION,  Long.toString(((enddate.getTime()-date.getTime())/(60 * 60 * 1000)))+" hrs");
                groupinfo.put(LOCATION,groupList.get(i).mLocationName);
                groupinfo.put(START_TIME,starttime);
                GroupList.add(groupinfo);


            }
        }
        adapter=new LazyAdapter(this, GroupList);
        list.setAdapter(adapter);
    }


    class FavGroup extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            try {
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/addFavourite?userId="+params[0]+"&groupId="+params[1];
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("GET");
                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }
    }

    class GetFavGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/getFavouriteGroups/"+key[0];
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            List<Integer> favgroup = new ArrayList<Integer>();
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                for(int i=0;i<groupList.length();i++)
                {
                    favgroup.add(groupList.getInt(i));
                }
               setListOfGroups(studygroups,favgroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class GetAllStudyGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/getAllGroups";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                studygroups = new StudyGroups[groupList.length()];
                for(int i=0;i<groupList.length();i++){
                    JSONObject groupInfo = groupList.getJSONObject(i);
                    String subjectName = new SubjectsMap().getSubjectMap().get(groupInfo.getString("subjectId"));
                    studygroups[i] = new StudyGroups(groupInfo.getString("id"),
                            subjectName,
                            groupInfo.getString("groupName"),
                            groupInfo.getString("adminId"),
                            groupInfo.getLong("startTimestamp"),
                            groupInfo.getLong("endTimestamp"),
                            groupInfo.getInt("capacity"),
                            groupInfo.getInt("numMembers"),
                            groupInfo.getString("topic"),
                            groupInfo.getString("locationName"),
                            groupInfo.getDouble("latitude"),
                            groupInfo.getDouble("longitude"));
                    Log.d("GroupId ",groupInfo.getString("id"));
                    Log.d("SubjectId ",groupInfo.getString("subjectId"));
                    Log.d("groupName ",groupInfo.getString("groupName"));
                    Log.d("adminId ",groupInfo.getString("adminId"));
                    Log.d("startTimestamp ",String.valueOf(groupInfo.getLong("startTimestamp")));
                    Log.d("endTimestamp ", String.valueOf(groupInfo.getLong("endTimestamp")));
                    Log.d("capacity ",String.valueOf(groupInfo.getInt("capacity")));
                    Log.d("numMembers ",String.valueOf(groupInfo.getInt("numMembers")));
                    Log.d("locationName ",groupInfo.getString("locationName"));
                    Log.d("latitude ",String.valueOf(groupInfo.getDouble("latitude")));
                    Log.d("longitude ", String.valueOf(groupInfo.getDouble("longitude")));
                }
                if(type.equals("JoinedGroup")||type.equals("MyGroup"))
                    setListOfGroups(studygroups, joinedgrouplist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class DeleteGroup extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuffer response = new StringBuffer();
            try {
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/deleteGroup/"+params[0];
                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("DELETE");
                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }

            return response.toString();
        }
    }
    class GetJoinedGroups extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... key) {
            StringBuffer response = new StringBuffer();
            try{
                String url = "http://studybuddy.tqz3d5cunm.us-west-2.elasticbeanstalk.com/getJoinedGroups/"+key[0];
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String groupResponse) {
            Log.d("My message",groupResponse);
            joinedgrouplist = new ArrayList<>();
            try {
                JSONArray groupList = new JSONArray(groupResponse);
                for(int i=0;i<groupList.length();i++){
                    joinedgrouplist.add(groupList.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}

