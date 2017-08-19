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


    // home page, start a new resume from here
    @GetMapping(value = {"/index", "/"})
    public String indexPageGet() {
        return "index";
    }


    // any time this route is called, all db tables are wiped out
    // TODO add a simple js popup alert box to avoid accidental data erasure
    @GetMapping("/startover")
    public String startOver() {
        personRepo.deleteAll();
        educationRepo.deleteAll();
        skillRepo.deleteAll();
        workExperienceRepo.deleteAll();

        return "index";
    }


    // TODO remove the js link disabling stuff before turning in project, just use CSS to disable mouse clicks
    // this will require editing the .disabled class in customstyles.css, and removing the .disable-anchor classappend from resumenavigation.html
    @GetMapping("/resumenavigation")
    public String addResumeGet(Model model) {
        // current db table record counts are used in various places in the template
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
        model.addAttribute("currentNumRecords", personRepo.count());
        model.addAttribute("newPerson", new Person());
        model.addAttribute("disableSubmit", personRepo.count() >= 1);

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


        // TODO remove res creation date from Person model, recreate db
        // set the resume creation date to right now
//        person.setResumeCreationDate(new Date());

        // person should have first and last names, and email at this point
        // the collections in Person are null at this point, which shows up as a BLOB in the db!  ...blob is you uncle
        personRepo.save(person);

        // go back to navi, which will show the users name at the top, no need for comfirmation here
        return "redirect:/resumenavigation";
    }



    @GetMapping("/addeducation")
    public String addEdGet(Model model) {
//        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addeducation GET route ++++++++++++++++++");

        // disable the submit button if >= 10 records in db, it would never be possible for the user to click to get
        // here from the navi page if there were already >= 10 records, however they could manually type in the URL
        // so I want to disable the submit button if they do that and there are already 10 records
        model.addAttribute("disableSubmit", educationRepo.count() >= 10);

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
//        model.addAttribute("todaysDate", Utilities.getTodaysDateString());

        addPersonNameToModel(model);
        model.addAttribute("disableSubmit", workExperienceRepo.count() >= 10);
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
        model.addAttribute("disableSubmit", skillRepo.count() >= 20);

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
        addDbContentsToModel(model);

        return "editdetails";
    }


    // id is the id to delete
    // type is what table to delete from
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id, @RequestParam("type") String type, Model model)
    {
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ path var: " + id);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ request param: " + type);

        switch (type) {
            case "ed" :
                educationRepo.delete(id);
                break;
            case "person" :
                personRepo.delete(id);
                break;
            case "workexp" :
                workExperienceRepo.delete(id);
                break;
            case "skill" :
                skillRepo.delete(id);
        }

        addPersonNameToModel(model);
        addDbContentsToModel(model);

        return "editdetails";
    }



    @GetMapping("/update/{id}")
    public String update(@PathVariable("id") long id, @RequestParam("type") String type, Model model)
    {
        model.addAttribute("disableSubmit", false);
        addPersonNameToModel(model);

        switch (type) {
            case "person" :
                model.addAttribute("newPerson",personRepo.findOne(id));
                return "addperson";
            case "ed" :
                model.addAttribute("newEdAchievement",educationRepo.findOne(id));
                model.addAttribute("currentNumRecords", educationRepo.count());
                return "addeducation";
            case "workexp" :
                model.addAttribute("newWorkExperience",workExperienceRepo.findOne(id));
                model.addAttribute("currentNumRecords", workExperienceRepo.count());
                return "addworkexperience";
            case "skill" :
                model.addAttribute("newSkill",skillRepo.findOne(id));
                model.addAttribute("currentNumRecords", skillRepo.count());
                return "addskill";
        }

        // should never happen, but need it to compiles
        return"resumenavigation";
    }



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


    /**
     * Adds the entire contents of each database table to model. Note that the object names used here must match
     * the names in the template being used.
     */
    private void addDbContentsToModel(Model model) {
        // there is only one person
        model.addAttribute("persons", personRepo.findAll());
        model.addAttribute("edAchievements", educationRepo.findAll());
        model.addAttribute("workExperiences", workExperienceRepo.findAll());
        model.addAttribute("skills", skillRepo.findAll());
    }


    /**
     * Composes a person using the data from the tables in the database.  All records are read out and lists are
     * populated in person for educational achievements, work experiences, and skills.  The person itself should already
     * contain a first and last name, and an email address.  After calling this function, person should contain
     * sufficient info to create a resume.
     * @param person the Person to compose
     */
    private void composePerson(Person person) {
        // get all the records from the db
        ArrayList<EducationAchievement> edsArrayList = new ArrayList<>();
        for(EducationAchievement item : educationRepo.findAll()) {
            edsArrayList.add(item);
        }
        // add it to our Person
        person.setEducationAchievements(edsArrayList);

        ArrayList<WorkExperience> weArrayList = new ArrayList<>();
        for(WorkExperience item : workExperienceRepo.findAll()) {
            weArrayList.add(item);
        }
        person.setWorkExperiences(weArrayList);

        ArrayList<Skill> skillsArrayList = new ArrayList<>();
        for(Skill item : skillRepo.findAll()) {
            skillsArrayList.add(item);
        }
        person.setSkills(skillsArrayList);
    }


    /**
     * Adds an object (firstAndLastName) to model, that is a String of the first and last name of the Person for this
     * resume. If the Person table is empty, an appropriate message is added to the model that will indicate to the user
     * that they need to add start the resume by adding personal details.
     */
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


    /**
     * Set boolean flags for the various links in the navigation page.  Links are enabled/disabled based on various
     * project criteria.
     */
    private void setLinkEnabledBooleans(Model model) {
        // disable link to add Person if already has one in db
        // note a Person can be edited in other page
        model.addAttribute("disableAddPersonLink", personRepo.count() > 0);

        // do not allow any other details to be added until personal info entered
        if(personRepo.count() == 0 || educationRepo.count() > 10) {
            model.addAttribute("disableAddEdLink", true);
        } else {
            model.addAttribute("disableAddEdLink", false);
        }

        if(personRepo.count() == 0 || skillRepo.count() >= 20) {
            model.addAttribute("disableAddSkillLink", true);
        } else {
            model.addAttribute("disableAddSkillLink", false);
        }

        if(personRepo.count() == 0 || workExperienceRepo.count() >= 10) {
            model.addAttribute("disableAddWorkExpLink", true);
        } else {
            model.addAttribute("disableAddWorkExpLink", false);
        }

        // disable edit link if no data has been entered yet
        if(personRepo.count() == 0 && skillRepo.count() == 0 && educationRepo.count() == 0
                && workExperienceRepo.count() == 0) {
            model.addAttribute("disableEditDetailsLink", true);
        } else {
            model.addAttribute("disableEditDetailsLink", false);
        }

        // disable show final resume link until at least one ed achievement, skill, and personal info has been entered
        if(personRepo.count() == 0 || skillRepo.count() == 0 || educationRepo.count() == 0) {
            model.addAttribute("disableShowFinalResumeLink", true);
        } else {
            model.addAttribute("disableShowFinalResumeLink", false);
        }
    }


}
