package com.Smart.Controller;

import com.Smart.dao.UserRepository;
import com.Smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){  // using model to send the data to normal/user_dashboard
        String userName = principal.getName();   // fetching the username in signin process
        System.out.println(userName);

        // get the user using username which is email

        User user = userRepository.getUserByUserName(userName);
        System.out.println(user);
        model.addAttribute("user",user);

        return  "normal/user_dashboard";
    }
}
