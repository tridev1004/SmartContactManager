package com.Smart.dao;

import com.Smart.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository < User,Integer> {
    @Query("SELECT u FROM User u WHERE u.email = :email")
     User getUserByUserName(@Param("email")String email); // need to pass email in string email to get the user
}
