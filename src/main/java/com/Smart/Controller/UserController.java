package com.Smart.Controller;

import com.Smart.dao.UserRepository;
import com.Smart.entities.Contact;
import com.Smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
                                 Principal principal ){

      try{






          String name =principal.getName();
          User user=this.userRepository.getUserByUserName(name);


          // processing thew file

          if(file.isEmpty())
          {
              System.out.println("file is emptry");


          }else{

              contact.setImage(file.getOriginalFilename());
                File savefile= new ClassPathResource("/static/img").getFile();


           Path path=   Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());


              Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
              System.out.println("file uploaded");

          }






          user.getContacts().add(contact);   //////a
          contact.setUser(user);          // bidirectional mapping here

          this.userRepository.save(user);






          System.out.println("added to database");




          System.out.println(contact);
      }catch (Exception e){

      e.printStackTrace();
          System.out.println("error:"+ e.getMessage());






      }



        return "normal/add_contact_form";

    }


}
