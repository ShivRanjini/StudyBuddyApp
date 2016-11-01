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
        TextView title = (TextView)vi.findViewById(R.id.groupname); // title
        TextView artist = (TextView)vi.findViewById(R.id.subject); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        ImageView like_image=(ImageView)vi.findViewById(R.id.star); // thumb image

        // Setting all values in listview
        title.setText(field.get("group"));
        artist.setText(field.get("subject"));
        duration.setText(field.get("duration"));
        if(position == 0)
        {
            thumb_image.setImageResource(R.drawable.geo);
            like_image.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else if(position == 2)
        {
            thumb_image.setImageResource(R.drawable.data);
            like_image.setImageResource(android.R.drawable.btn_star_big_off);
        }
        else if(position==1 || position == 4){
        thumb_image.setImageResource(R.drawable.ml);
        like_image.setImageResource(android.R.drawable.btn_star_big_off);}
        else
        {
                thumb_image.setImageResource(R.drawable.network);
            like_image.setImageResource(android.R.drawable.btn_star_big_on);
        }

        return vi;
    }
}