package com.Smart.Controller;

import com.Smart.dao.UserRepository;
import com.Smart.entities.User;
import com.Smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Autowired
    private UserRepository userRepository;

    // creating home here
    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart contact manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-Smart contact manager");

        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register-Smart contact manager");
        model.addAttribute("user", new User());  /// sending blank user fields
        return "signup";
    }


    //handling form registry

    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model, HttpSession session) {
        try {

            if (!agreement) {
                System.out.println("You have not agreed to terms and condition");
                throw new Exception();  // if agreement not clicked then throwing exception to catch block

            }
            if (result1.hasErrors()) {
                System.out.println("error: " + result1);
                model.addAttribute("user", user);
                return "signup";
            }
            // if no exception then pursuing further
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");

            user.setPassword(passwordEncoder.encode(user.getPassword()));  // encoding and setting the password
            System.out.println("Agreement" + agreement);
            System.out.println(user);


            User result = this.userRepository.save(user);


            model.addAttribute("user", new User());// sending blank new user
            session.setAttribute("message", new Message("Successfully registered!!", "alert-success"));
            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);  // incase of error ending half filled user form
            session.setAttribute("message", new Message("Something went wrong", "alert-danger"));
            return "signup";

        }
    }


    /// custom login
    @GetMapping("/signin")
    public String CustomLogin(Model model) {
        model.addAttribute("title", "Login-Smart contact manager");

        return "login";
    }

}
