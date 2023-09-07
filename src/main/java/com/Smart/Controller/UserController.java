package com.Smart.Controller;

import com.Smart.dao.ContactRepository;
import com.Smart.dao.UserRepository;
import com.Smart.dao.myOrderRepository;
import com.Smart.entities.Contact;
import com.Smart.entities.MyOrder;
import com.Smart.entities.User;
import com.Smart.helper.Message;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private myOrderRepository myOrderRepository;

    @ModelAttribute  // method for adding common data to response
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();   // fetching the username in who is logged in
        System.out.println(userName);

        // get the user using username which is email

        User user = userRepository.getUserByUserName(userName);
        System.out.println(user);
        model.addAttribute("user", user);
    }


    // dashboard_home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {  // using model to send the data to normal/user_dashboard
        model.addAttribute("title", "User Dashboard -Smart contact manager");


        return "normal/user_dashboard";
    }


    // adding form for contacts
    @GetMapping("/add-contact")
    public String OpenAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact-Smart contact manager");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
                                 Principal principal, HttpSession httpSession) {

        try {


            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);


            // processing thew file

            if (file.isEmpty()) {
                System.out.println("file is empty");
                contact.setImage("icon.png");


            } else {
                /// saving in system
                contact.setImage(file.getOriginalFilename());
                File savefile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("file uploaded");

            }


            user.getContacts().add(contact);   //////a
            contact.setUser(user);          // bidirectional mapping here

            this.userRepository.save(user);


            System.out.println(contact);

            System.out.println("added to database");

            httpSession.setAttribute("message", new Message("Your contact is added Add more!", "success"));


        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("error:" + e.getMessage());
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
    public String showContact(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "show-user Contacts");

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());


        return "normal/show_contacts";
    }


    //
//     showing particular contact
    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        System.out.println(cId);

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);

        Contact contact = contactOptional.get();

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);   // just a security check to take measure if
            // different user try to hit and fetch the contact of different user
        }


        return "normal/contact_detail";
    }

    // deleting the user now
    @GetMapping("delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Model model, HttpSession httpSession, Principal principal) {
        Optional<Contact> contactOp = this.contactRepository.findById(cId);
        Contact contact = contactOp.get();
        contact.setUser(null);


       User user= this.userRepository.getUserByUserName(principal.getName());
       user.getContacts().remove(contact);


       this.userRepository.save(user);



        httpSession.setAttribute("message", new Message("Contact Deleted Successfully..", "success"));
        return "redirect:/user/show-contacts/0";
    }


    // updating the Contact

    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer cId, Model m) {

        Contact contact = this.contactRepository.findById(cId).get();
        m.addAttribute("contact", contact);


        m.addAttribute("title", "Update Contact");
        return "normal/update_form";
    }

    /// updating the contacts
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
                                Model model, HttpSession session, Principal principal) {
        try {
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();


            if (!file.isEmpty()) {
                //deleting the contact img


                File Deletefile = new ClassPathResource("/static/img").getFile();
                File file1 = new File(Deletefile, oldContactDetail.getImage());
                file1.delete();


                // rewrite the file
                File savefile = new ClassPathResource("/static/img").getFile();
                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            } else {
                contact.setImage(oldContactDetail.getImage());
            }
            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message", new Message("Your contact has been updated!", "success"));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:contact/" + contact.getcId();
    }


    //profile handler
    @GetMapping("/profile")
    public  String yourProfile(Model model){
        model.addAttribute("title", " User profile page");
        return "normal/profile";
    }


    // settings page handler
   @GetMapping("/settings")
    public String openSettings(){
        return "normal/settings";

    }


    // change password handler

    // all are returning a view
@PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword")String oldPassword,
                                 @RequestParam("newPassword")String newPassword,
                                 Principal principal,HttpSession session){
    String userName = principal.getName();// fetching the current logged in user information

    User currentUser = this.userRepository.getUserByUserName(userName);
    System.out.println(currentUser.getPassword());


    System.out.println(oldPassword);
    System.out.println(newPassword);
if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())){
    // change the password
    currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
    this.userRepository.save(currentUser);
    session.setAttribute("message",new Message("Your password is Successfully changed!!!","success"));



}else {
    session.setAttribute("message",new Message("Your Old Password is wrong!","danger"));
    return "redirect:/user/settings";

}

        return "redirect:/user/index";

}
/// handling order
@PostMapping("/create_order")
@ResponseBody
public String CreateOrder(@RequestBody Map<String, Object> data,Principal principal) throws Exception {

    System.out.println(data);

    int amt = Integer.parseInt(data.get("amount").toString());
    RazorpayClient client = new RazorpayClient("rzp_test_YZDWmInzn4UUCx", "JGYufP7IyKNJbroUlDxrlmtX");

    JSONObject ob=new JSONObject();
    ob.put("amount",amt*100);
    ob.put("currency","INR");
    ob.put("receipt","Txn_23432");

    Order order = client.orders.create(ob);
    System.out.println(order);

    MyOrder myOrder=new MyOrder();
    myOrder.setAmount(order.get("amount")+"");
    myOrder.setOrderId(order.get("id"));
    myOrder.setPaymentId(null);
    myOrder.setStatus("created");
    myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
    myOrder.setReceipt(order.get("receipt"));

     this.myOrderRepository.save(myOrder);



    return order.toString();


}
@PostMapping("/update_order")
public ResponseEntity<?> updateOrder(@RequestBody Map<String,Object> data){

    MyOrder myorder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
    myorder.setPaymentId(data.get("payment_id").toString());
    myorder.setStatus(data.get("status").toString());
    this.myOrderRepository.save(myorder);


    System.out.println(data);
        return ResponseEntity.ok("");
}


}




