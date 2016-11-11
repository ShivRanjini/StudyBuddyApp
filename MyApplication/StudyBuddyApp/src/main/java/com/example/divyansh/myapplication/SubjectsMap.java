package com.example.divyansh.myapplication;

import java.util.HashMap;

/**
 * Created by Divyansh on 11/1/2016.
 */

public class SubjectsMap {

    HashMap<String, String> subjectMap;

    public SubjectsMap(){
        subjectMap = new HashMap<>();
        subjectMap.put("1","Artificial Intelligence");
        subjectMap.put("2","Machine Learning");
        subjectMap.put("3","Algorithms");
        subjectMap.put("4","Computer Networks");
        subjectMap.put("5","GeoSpatial Information Management");
    }

    public HashMap<String, String> getSubjectMap(){
        return subjectMap;
    }

}
