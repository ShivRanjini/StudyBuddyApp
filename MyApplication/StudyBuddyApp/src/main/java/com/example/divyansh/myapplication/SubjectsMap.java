package com.example.divyansh.myapplication;

import java.util.HashMap;

/**
 * Created by Divyansh on 11/1/2016.
 */

public class SubjectsMap {

    HashMap<String, String> subjectMap;

    public SubjectsMap(){
        subjectMap = new HashMap<>();
        subjectMap.put("1","GeoSpatial Database");
        subjectMap.put("2","Computer Networks");
        subjectMap.put("3","Machine Learning");
        subjectMap.put("4","Artificial Intelligence");
        subjectMap.put("5","Algorithms");
    }

    public HashMap<String, String> getSubjectMap(){
        return subjectMap;
    }

}
