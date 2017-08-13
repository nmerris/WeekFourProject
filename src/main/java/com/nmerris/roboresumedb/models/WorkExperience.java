package com.nmerris.roboresumedb.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 3, max = 64)
    private String jobTitle;

    @Size(min = 3, max = 64)
    private String company;

    // TemporalType.DATE requires a month, day, and year, but will handle many formats
    // TODO make my own TemporalType annotation?  if only I had infinite time...
//    @NotNull
//    @Past // not working
    @Temporal(TemporalType.DATE)
    private Date dateStart;

    // date end is optional, assume it's todays date if nothing is entered
    // using @Temporal here give a validation error if input box is empty!
    // TODO figure out why
//    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dateEnd;

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

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
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
