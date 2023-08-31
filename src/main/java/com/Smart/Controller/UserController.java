package com.Smart.Controller;

import com.Smart.dao.ContactRepository;
import com.Smart.dao.UserRepository;
import com.Smart.entities.Contact;
import com.Smart.entities.User;
import com.Smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
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
                                 Principal principal, HttpSession httpSession){

      try{






          String name =principal.getName();
          User user=this.userRepository.getUserByUserName(name);


          // processing thew file

          if(file.isEmpty())
          {
              System.out.println("file is empty");
              contact.setImage("icon.png");


          }else{
           /// saving in system
              contact.setImage(file.getOriginalFilename());
                File savefile= new ClassPathResource("/static/img").getFile();
                Path path=   Paths.get(savefile.getAbsolutePath()+File.separator+file.getOriginalFilename());
           Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
              System.out.println("file uploaded");

          }






          user.getContacts().add(contact);   //////a
          contact.setUser(user);          // bidirectional mapping here

          this.userRepository.save(user);


          System.out.println(contact);

          System.out.println("added to database");

       httpSession.setAttribute("message", new Message("Your contact is added Add more!","success"));


      }catch (Exception e){

      e.printStackTrace();
          System.out.println("error:"+ e.getMessage());
          httpSession.setAttribute("message", new Message("Your contact is  Not added Something Went wrong",
                  "danger"));







      }



        return "normal/add_contact_form";

    }
    // removing message of error
    @GetMapping("/remove-message")
    public String removeMessage(HttpSession session) {
        session.removeAttribute("message");
        return "normal/add_contact_form"; // Redirect back to your original page
    }
    /// showing the contacts

    @GetMapping("/show-contacts/{page}")
    public  String showContact(@PathVariable("page") Integer page,     Model model,Principal principal){
        model.addAttribute("title", "show-user Contacts");

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts= this.contactRepository.findContactsByUser(user.getId(),pageable);

         model.addAttribute("contacts",contacts);
         model.addAttribute("currentPage",page);
         model.addAttribute("totalPages",contacts.getTotalPages());





         return "normal/show_contacts";
    }


//
//     showing particular contact
    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId")Integer cId,Model model){
        System.out.println(cId);


        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact=contactOptional.get();
        model.addAttribute("contact",contact);

        return "normal/contact_detail";
    }

}




