package com.nmerris.roboresumedb.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EducationAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String major;
    private String school;

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
