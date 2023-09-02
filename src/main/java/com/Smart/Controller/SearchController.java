package com.Smart.Controller;

import com.Smart.dao.ContactRepository;
import com.Smart.dao.UserRepository;
import com.Smart.entities.Contact;
import com.Smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
private UserRepository userRepository;
@Autowired
private ContactRepository contactRepository;

//  @  /searching
    @GetMapping("/search/{query}")

    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
        System.out.println(query);
        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query,user);
return ResponseEntity.ok(contacts);
    }
}
