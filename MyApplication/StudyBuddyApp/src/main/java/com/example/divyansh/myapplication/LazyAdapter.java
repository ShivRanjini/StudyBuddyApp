package com.example.divyansh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.security.AccessController.getContext;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
   // public ImageLoader imageLoader;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);
        HashMap<String,String> field= data.get(position);
        TextView groupName = (TextView)vi.findViewById(R.id.groupname);
        TextView subject = (TextView)vi.findViewById(R.id.subject);
        TextView duration = (TextView)vi.findViewById(R.id.duration);
        ImageView subjectImage=(ImageView)vi.findViewById(R.id.list_image);
        ImageView like_image=(ImageView)vi.findViewById(R.id.star);
        TextView start_time = (TextView)vi.findViewById(R.id.start_time);
        TextView location = (TextView)vi.findViewById(R.id.location);

        location.setText(field.get("location"));
        start_time.setText(field.get("start_time"));
        groupName.setText(field.get("group"));
        subject.setText(field.get("subject"));
        duration.setText(field.get("duration"));
        if(Integer.parseInt(field.get("isfav")) == 1)
        {
            like_image.setImageResource(R.drawable.fav_on);
            like_image.setTag("star_on");
        }
        else if(Integer.parseInt(field.get("isfav")) == 0)

        {
            like_image.setImageResource(R.drawable.fav_off);
            like_image.setTag("star_off");
        }
        else if(Integer.parseInt(field.get("isfav")) == 3)
        {
            like_image.setVisibility(View.INVISIBLE);
        }
        else if(Integer.parseInt(field.get("isfav")) == 2)
        {
            like_image.setImageResource(android.R.drawable.ic_menu_delete);
            like_image.setTag("delete");
        }
        String subid="2";
        HashMap<String, String> mSubjectsMap = new SubjectsMap().getSubjectMap();
        Iterator it = mSubjectsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry subidpair = (Map.Entry) it.next();
            if(subidpair.getValue().equals(field.get("subject"))){
                subid = (String)subidpair.getKey();
                break;
            }

        }
        int Rid = activity.getApplicationContext().getResources().getIdentifier("gimg"+subid, "drawable", activity.getApplicationContext().getPackageName());
        subjectImage.setImageResource(Rid);

        return vi;
    }
}