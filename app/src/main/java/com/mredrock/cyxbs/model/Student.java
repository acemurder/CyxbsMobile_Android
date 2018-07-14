package com.mredrock.cyxbs.model;

import com.redrock.common.network.RedRockApiWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by skylineTan on 2016/4/13 16:24.
 */
public class Student implements Serializable {

    public String stunum;
    public String name;
    public String gender;
    public String classnum;
    public String major;
    public String depart;
    public String grade;

    public static class StudentWrapper extends RedRockApiWrapper<List<Student>> {
        public int state;
    }
}
