package com.nmerris.roboresumedb.controllers;

import com.nmerris.roboresumedb.models.Person;
import com.nmerris.roboresumedb.repositories.EducationRepo;
import com.nmerris.roboresumedb.repositories.PersonRepo;
import com.nmerris.roboresumedb.repositories.SkillsRepo;
import com.nmerris.roboresumedb.repositories.WorkExperienceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @Autowired
    PersonRepo personRepo;
    @Autowired
    EducationRepo educationRepo;
    @Autowired
    SkillsRepo skillsRepo;
    @Autowired
    WorkExperienceRepo workExperienceRepo;


    // home page
    @GetMapping("/index")
    public String indexPageGet() {
//        model.addAttribute("potluckGuest", new PotluckGuest());

        return "index";
    }


    @GetMapping("/addperson")
    public String addPersonGet(Model model) {
        model.addAttribute("newPerson", new Person());

        return "addperson";
    }


    @PostMapping("/addperson")
    public String addPersonPost(@ModelAttribute("newPerson") Person person, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()) {
            // return addperson after adding validation stuff

            return "addperson";
        }


        // person should have first and last names, and email at this point
        // the collections in Person are null at this point, which shows up as a BLOB in the db!  ...blob is you uncle
        personRepo.save(person);
        return "addeducation";
    }


}
