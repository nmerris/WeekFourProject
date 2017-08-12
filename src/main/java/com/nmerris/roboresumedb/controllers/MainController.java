package com.nmerris.roboresumedb.controllers;

import com.nmerris.roboresumedb.models.EducationAchievement;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Iterator;

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
        // need this so that the tables resets every time we go back to index
        // this is necessary so that the data is correct if the user chooses to 'start over'
        personRepo.deleteAll();
        educationRepo.deleteAll();
        skillsRepo.deleteAll();
        workExperienceRepo.deleteAll();

        return "index";
    }



    @GetMapping("/addperson")
    public String addPersonGet(Model model) {
        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addperson GET route ++++++++++++++++++");


        model.addAttribute("newPerson", new Person());

        return "addperson";
    }

    @PostMapping("/addperson")
    public String addPersonPost(@ModelAttribute("newPerson") Person person, BindingResult bindingResult) {
        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addperson POST route ++++++++++++++++++");


        if(bindingResult.hasErrors()) {
            // return addperson after adding validation stuff

            return "addperson";
        }

        // person should have first and last names, and email at this point
        // the collections in Person are null at this point, which shows up as a BLOB in the db!  ...blob is you uncle
        personRepo.save(person);

        return "redirect:/addeducation";
    }



    @GetMapping("/addeducation")
    public String addEdGet(Model model) {
        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addeducation GET route ++++++++++++++++++");

        Person p = personRepo.findAll().iterator().next();
//        System.out.println("################################################ inside /addecuation GET, person fn from db is: " + p.getNameFirst());

        model.addAttribute("newEdAchievement", new EducationAchievement());
        model.addAttribute("firstAndLastName", p.getNameFirst() + " " + p.getNameLast());

        return "addeducation";
    }

    @PostMapping("/addeducation")
    public String addEdPost(@ModelAttribute("newEdAchievement") EducationAchievement educationAchievement,
                            BindingResult bindingResult, Model model) {
        System.out.println("++++++++++++++++++++++++++++++ JUST ENTERED /addeducation POST route ++++++++++++++++++ ");

        if(bindingResult.hasErrors()) {
            return "addeducation";
        }

        Person p = personRepo.findAll().iterator().next();

        model.addAttribute("edAchievementJustAdded", educationAchievement);
        model.addAttribute("firstAndLastName", p.getNameFirst() + " " + p.getNameLast());

        educationRepo.save(educationAchievement);
        return "addeducationconfirmation";
    }



}
