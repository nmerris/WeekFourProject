package com.nmerris.roboresumedb.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 3, max = 64)
    private String jobTitle;

    @Size(min = 3, max = 64)
    private String company;

    // TODO get the damn temporal annotation and error msg to work
    private String dateStart;

    // date end is optional, assume it's todays date if nothing is entered
    private String dateEnd;

    @Size(min = 3, max = 64)
    private String dutyOne;

    // second duty is optional
    private String dutyTwo;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDutyOne() {
        return dutyOne;
    }

    public void setDutyOne(String dutyOne) {
        this.dutyOne = dutyOne;
    }

    public String getDutyTwo() {
        return dutyTwo;
    }

    public void setDutyTwo(String dutyTwo) {
        this.dutyTwo = dutyTwo;
    }
}
