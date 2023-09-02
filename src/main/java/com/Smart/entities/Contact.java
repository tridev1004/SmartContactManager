package com.Smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    private String name;
    private String secondName;
    private String work;
    private String email;

//    @Override
//    public String toString() {
//        return "Contact{" +
//                "cId=" + cId +
//                ", name='" + name + '\'' +
//                ", secondName='" + secondName + '\'' +
//                ", work='" + work + '\'' +
//                ", email='" + email + '\'' +
//                ", Phone='" + Phone + '\'' +
//                ", image='" + image + '\'' +
//                ", user=" + user +
//                ", description='" + description + '\'' +
//                '}';
//    }
@Column(unique = true)
@NotBlank(message = "enter a valid number")
@Size(min = 2, max = 20, message = "type b/w the range of 2-20!!")
    private String Phone;

    @Override
    public boolean equals(Object obj) {
        return this.cId==((Contact)obj).getcId();
    }

    private String image;
    @ManyToOne
    @JsonIgnore
    private User user;
    @Column(length = 1000)
    private String description;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
