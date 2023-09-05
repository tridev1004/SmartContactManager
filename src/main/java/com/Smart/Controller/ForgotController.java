package com.Smart.Controller;

import com.Smart.Service.EmailService;
import com.Smart.dao.UserRepository;
import com.Smart.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.util.Random;

@Controller
public class ForgotController {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    Random random = new Random();
    @Autowired
    private EmailService emailService;

    @GetMapping("/forget")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) throws MessagingException {
        System.out.println(email);


        //generating 4 digit otp

        int otp = random.nextInt(999999);
        System.out.println("otp:" + otp);

        String subject = "OTP_FROM_SCM";
        String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>"
                + "<h1>"
                + "Otp is:" + " "
                + "<b>" + otp
                + "</n>"
                + "<h1>"
                + "</div>";
        String to = email;


        boolean flag = this.emailService.SendEmail(subject, message, to);
        if (flag) {
            session.setAttribute("myotp", otp); // storing otp in session
            session.setAttribute("email", email);
            return "verify_otp";
        } else {

            session.setAttribute("message", "check your message..");


            return "forgot_email_form";


        }


    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {
        int myOtp = (int) session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");
        System.out.println(email);
        if (myOtp == otp) {

            User user = this.userRepository.getUserByUserName(email); // checking whether user exist in the database or not
            if (user == null) {
                // sending it to email page


                session.
                        setAttribute("message", "User does not exist with this email..");
                return "forgot_email_form";

            } else {
                // change password
            }
            return "change_password_form";

        } else {
            session.setAttribute("message", "entered the wrong otp..");
            return "verify_otp";
        }


    }

    // change password
    @PostMapping("/change-password")
    public String ChangingPassword(@RequestParam("password") String password, HttpSession session) {

        String email = (String) session.getAttribute("email");

        User user = this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(password));
        this.userRepository.save(user);


        return "redirect:/signin?change=password Changed successfully..";

    }
}
