package com.nmerris.roboresumedb.controllers;

import com.nmerris.roboresumedb.Utilities;
import com.nmerris.roboresumedb.models.EducationAchievement;
import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.models.Skill;
import com.nmerris.roboresumedb.models.WorkExperience;
import com.nmerris.roboresumedb.repositories.EducationRepo;
import com.nmerris.roboresumedb.repositories.PersonRepo;
import com.nmerris.roboresumedb.repositories.SkillRepo;
import com.nmerris.roboresumedb.repositories.WorkExperienceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    PersonRepo personRepo;
    @Autowired
    EducationRepo educationRepo;
    @Autowired
    SkillRepo skillRepo;
    @Autowired
    WorkExperienceRepo workExperienceRepo;


    // home page
    @GetMapping(value = {"/index", "/"})
    public String indexPageGet(@RequestParam("startover") Optional<String> deleteAll) {
        // need this so that the tables resets every time we go back to index
        // this is necessary so that the data is correct if the user chooses to 'start over'

        // wipe everything depending on request param
        if(deleteAll.equals("true")) {
            personRepo.deleteAll();
            educationRepo.deleteAll();
            skillRepo.deleteAll();
            workExperienceRepo.deleteAll();
        }
        return "index";
    }




    @GetMapping("/resumenavigation")
    public String addResumeGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /resumenavigation GET route ++++++++++++++++++");
        model.addAttribute("numPersons", personRepo.count());
        model.addAttribute("numEdAchievements", educationRepo.count());
        model.addAttribute("numSkills", skillRepo.count());
        model.addAttribute("numWorkExperiences", workExperienceRepo.count());
        addPersonNameToModel(model);
        setLinkEnabledBooleans(model);

        return "resumenavigation";
    }



    @GetMapping("/addperson")
    public String addPersonGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addperson GET route ++++++++++++++++++");
        model.addAttribute("currentNumRecords", personRepo.count());
        model.addAttribute("newPerson", new Person());

        return "addperson";
    }

    @PostMapping("/addperson")
    public String addPersonPost(@Valid @ModelAttribute("newPerson") Person person,
                                BindingResult bindingResult, Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addperson POST route ++++++++++++++++++");
        model.addAttribute("currentNumRecords", personRepo.count());

        if(bindingResult.hasErrors()) {
            return "addperson";
        }

        // set the resume creation date to right now
        person.setResumeCreationDate(new Date());

        // person should have first and last names, and email at this point
        // the collections in Person are null at this point, which shows up as a BLOB in the db!  ...blob is you uncle
        personRepo.save(person);

        // go back to navi, which will show the users name at the top, no need for comfirmation here
        return "redirect:/resumenavigation";
    }



    @GetMapping("/addeducation")
    public String addEdGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addeducation GET route ++++++++++++++++++");

        if(educationRepo.count() >= 1) {
            model.addAttribute("disableSubmit", true);
        } else {
            model.addAttribute("disableSubmit", false);
        }

        model.addAttribute("currentNumRecords", educationRepo.count());
        model.addAttribute("newEdAchievement", new EducationAchievement());
        addPersonNameToModel(model);

        return "addeducation";
    }

    @PostMapping("/addeducation")
    public String addEdPost(@Valid @ModelAttribute("newEdAchievement") EducationAchievement educationAchievement,
                            BindingResult bindingResult, Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addeducation POST route ++++++++++++++++++ ");

        model.addAttribute("edAchievementJustAdded", educationAchievement);
        addPersonNameToModel(model);

        if(bindingResult.hasErrors()) {
            // need to get the count even if an error, because we always show the count
            // it is also needed to know if we should allow the user to exit the education section, since they
            // must enter at least one educational achievement
            model.addAttribute("currentNumRecords", educationRepo.count());
            return "addeducation";
        }

        educationRepo.save(educationAchievement);

        // need to get the count AFTER successfully adding to db, so it is up to date
        model.addAttribute("currentNumRecords", educationRepo.count());

        return "addeducationconfirmation";
    }



    @GetMapping("/addworkexperience")
    public String addWorkGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addworkexperience GET route ++++++++++++++++++");

        // it would be nice to show todays date as placeholder text for end date
        model.addAttribute("todaysDate", Utilities.getTodaysDateString());

        addPersonNameToModel(model);
        model.addAttribute("currentNumRecords", workExperienceRepo.count());
        model.addAttribute("newWorkExperience", new WorkExperience());

        return "addworkexperience";
    }

    @PostMapping("/addworkexperience")
    public String addWorkPost(@Valid @ModelAttribute("newWorkExperience") WorkExperience workExperience,
                            BindingResult bindingResult, Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addworkexperience POST route ++++++++++++++++++ ");

        addPersonNameToModel(model);

        // add a placeholder for end date that is todays date, because why not?
        model.addAttribute("todaysDate", Utilities.getTodaysDateString());
        model.addAttribute("workExperienceJustAdded", workExperience);

        // TODO chop off the timestamp from the date, put it back in the model so it looks nice when page is redisplayed with errors
        // TODO check if end date is later than start date

        if(bindingResult.hasErrors()) {
            model.addAttribute("currentNumRecords", workExperienceRepo.count());

            return "addworkexperience";
        }

        // show end date as 'Present' if user did not enter end date, otherwise show whatever they entered
        if(workExperience.getDateEnd() == null) {
            model.addAttribute("dateEndString", "Present");
        }
        else {
            model.addAttribute("dateEndString", Utilities.getMonthDayYearFromDate(workExperience.getDateEnd()));
        }

        workExperienceRepo.save(workExperience);
        model.addAttribute("currentNumRecords", workExperienceRepo.count());

        return "addworkexperienceconfirmation";
    }
    


    @GetMapping("/addskill")
    public String addSkillGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addskill GET route ++++++++++++++++++");

        addPersonNameToModel(model);
        model.addAttribute("currentNumRecords", skillRepo.count());
        model.addAttribute("newSkill", new Skill());

        return "addskill";
    }

    @PostMapping("/addskill")
    public String addSkillPost(@Valid @ModelAttribute("newSkill") Skill skill,
                              BindingResult bindingResult, Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addskill POST route ++++++++++++++++++ ");

        addPersonNameToModel(model);
        model.addAttribute("skillJustAdded", skill);

        if(bindingResult.hasErrors()) {
            model.addAttribute("currentNumRecords", skillRepo.count());

            return "addskill";
        }

        skillRepo.save(skill);
        model.addAttribute("currentNumRecords", skillRepo.count());

        return "addskillconfirmation";
    }



    @GetMapping("/editdetails")
    public String editDetails(Model model) {

        addPersonNameToModel(model);

        // there is only one person
        model.addAttribute("persons", personRepo.findAll());
        model.addAttribute("edAchievements", educationRepo.findAll());
        model.addAttribute("workExperiences", workExperienceRepo.findAll());
        model.addAttribute("skills", skillRepo.findAll());

        return "editdetails";
    }


    // id is the id to delete
    // type is what table to delete from
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, @RequestParam("type") String type)
    {
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ path var: " + id);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ request param: " + type);

        switch (type) {
            case "ed" : educationRepo.delete(id);
            break;

            case "person" : personRepo.delete(id);
            break;

            case "workexp" : workExperienceRepo.delete(id);
            break;

            case "skill" : skillRepo.delete(id);
        }

        return"redirect:/resumenavigation";
    }



    @GetMapping("/updateeducation/{id}")
    public String updateEdAchievement(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("currentNumRecords", educationRepo.count());
        // grab the record from db with id, send it back to entry form
        model.addAttribute("newEdAchievement",educationRepo.findOne(id));
        // ALWAYS enable the submit button from this route, because even if there are already 10+ records in db
        // user is only UPDATING an existing record here, so always allow submit from this route
        model.addAttribute("disableSubmit", false);
        return"addeducation";
    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable("id") long id, Model model)
//    {
//        productRepo.delete(id);
//        return"redirect:/admin";
//    }


    @GetMapping("/finalresume")
    public String finalResumeGet(Model model) {

        // get the one and only person from the db
        // it is not possible to get here unless a Person exists, and the other resume data has been entered
        Person p = personRepo.findAll().iterator().next();

        // populate the empty ArrayLists in our single Person from data in other tables
        composePerson(p);

        model.addAttribute("person", p);

        return "finalresume";
    }



    private void composePerson(Person p) {
        // get all the records from the db
        ArrayList<EducationAchievement> edsArrayList = new ArrayList<>();
        for(EducationAchievement item : educationRepo.findAll()) {
            edsArrayList.add(item);
        }
        // add it to our Person
        p.setEducationAchievements(edsArrayList);

        ArrayList<WorkExperience> weArrayList = new ArrayList<>();
        for(WorkExperience item : workExperienceRepo.findAll()) {
            weArrayList.add(item);
        }
        p.setWorkExperiences(weArrayList);

        ArrayList<Skill> skillsArrayList = new ArrayList<>();
        for(Skill item : skillRepo.findAll()) {
            skillsArrayList.add(item);
        }
        p.setSkills(skillsArrayList);
    }

    private void addPersonNameToModel(Model model) {
        try {
            // try to get the single Person from the db
            Person p = personRepo.findAll().iterator().next();
            // if there was a Person, add their full name to the model
            model.addAttribute("firstAndLastName", p.getNameFirst() + " " + p.getNameLast());
        } catch (Exception e) {
            // must not have found a Person in the db, so use a placeholder name
            // this is really convenient for testing, but it also makes the app less likely to crash
            // navi page will display below if no personal details entered yet
            model.addAttribute("firstAndLastName", "Please start by entering personal details");
        }
    }

    private void setLinkEnabledBooleans(Model model) {
        // disable link to add Person if already has one in db
        // note a Person can be edited in other page
        if(personRepo.count() > 0) {
            model.addAttribute("disableAddPersonLink", true);
        } else {
            model.addAttribute("disableAddPersonLink", false);
        }

        // do not allow any other details to be added until personal info entered
        if(personRepo.count() == 0 || educationRepo.count() > 10) {
            model.addAttribute("disableAddEdLink", true);
        } else {
            model.addAttribute("disableAddEdLink", false);
        }


        if(personRepo.count() == 0 || skillRepo.count() > 20) {
            model.addAttribute("disableAddSkillLink", true);
        } else {
            model.addAttribute("disableAddSkillLink", false);
        }

        if(personRepo.count() == 0 || workExperienceRepo.count() > 10) {
            model.addAttribute("disableAddWorkExpLink", true);
        } else {
            model.addAttribute("disableAddWorkExpLink", false);
        }

        if(personRepo.count() == 0 && skillRepo.count() == 0 && educationRepo.count() == 0
                && workExperienceRepo.count() == 0) {
            model.addAttribute("disableEditDetailsLink", true);
        } else {
            model.addAttribute("disableEditDetailsLink", false);
        }

        if(personRepo.count() == 0 || skillRepo.count() == 0 || educationRepo.count() == 0) {
            model.addAttribute("disableShowFinalResumeLink", true);
        } else {
            model.addAttribute("disableShowFinalResumeLink", false);
        }
    }


}
