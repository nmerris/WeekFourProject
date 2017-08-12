package com.nmerris.roboresumedb.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Entity
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    // fields to save in the db
    @NotNull
    private String nameFirst;
    private String nameLast;
    private String email;
    
    // data to store temporarily for this project
    private ArrayList<EducationAchievement> educationAchievements = new ArrayList<EducationAchievement>();
    private ArrayList<WorkExperience> workExperiences = new ArrayList<WorkExperience>();
    private ArrayList<Skill> skills = new ArrayList<Skill>();

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<EducationAchievement> getEducationAchievements() {
        return educationAchievements;
    }

    public void setEducationAchievements(ArrayList<EducationAchievement> educationAchievements) {
        this.educationAchievements = educationAchievements;
    }

    public ArrayList<WorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(ArrayList<WorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }
}
