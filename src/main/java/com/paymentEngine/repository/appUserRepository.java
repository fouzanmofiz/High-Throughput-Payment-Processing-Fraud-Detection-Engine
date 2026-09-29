package com.paymentEngine.repository;

import com.paymentEngine.entity.appUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface appUserRepository extends JpaRepository<appUser, Long> {

    Optional<appUser> findByUsername(String username);



}
