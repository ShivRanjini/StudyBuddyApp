package com.example.divyansh.myapplication;

import android.util.Log;
import android.widget.Switch;

/**
 * Created by Divyansh on 10/31/2016.
 */

public class StudyGroups {
    String mGroupId;
    String mSubjectId;
    String mGroupName;
    String mAdminId;
    long mStartTime;
    long mEndTime;
    int mGroupCapacity;
    int mNumMembers;
    String mTopic;
    String mLocationName;
    double mGroupLatitude;
    double mGroupLongitude;


    public StudyGroups(String groupId, String subjectId, String groupName,
                       String adminId, long startTime, long endTime,
                       int groupCapacity, int numMembers, String topic,
                       String locationName, double groupLatitide, double groupLongitude){
        mGroupId = groupId;
        mSubjectId = subjectId;
        mGroupName = groupName;
        mAdminId = adminId;
        mStartTime = startTime;
        mEndTime = endTime;
        mGroupCapacity = groupCapacity;
        mNumMembers = numMembers;
        mTopic = topic;
        mLocationName = locationName;
        mGroupLatitude = groupLatitide;
        mGroupLongitude = groupLongitude;
    }

}
