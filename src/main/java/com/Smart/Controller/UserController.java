package com.Smart.Controller;

import com.Smart.dao.UserRepository;
import com.Smart.entities.Contact;
import com.Smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @ModelAttribute  // method for adding common data to response
    public void addcommonData(Model model,Principal principal){
        String userName = principal.getName();   // fetching the username in who is logged in
        System.out.println(userName);

        // get the user using username which is email

        User user = userRepository.getUserByUserName(userName);
        System.out.println(user);
        model.addAttribute("user",user);
    }


    // dashboard_home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){  // using model to send the data to normal/user_dashboard
        model.addAttribute("title","User Dashboard -Smart contact manager");


        return  "normal/user_dashboard";
    }


    // adding form for contacts
    @GetMapping("/add-contact")
    public String OpenAddContactForm(Model model){
        model.addAttribute("title","Add Contact-Smart contact manager");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }

// processing add contact form
    @PostMapping("/user/process-contact")
    public String processContact(@ModelAttribute Contact contact){
        System.out.println(contact);
        return "normal/add_contact_form";
    }


}
