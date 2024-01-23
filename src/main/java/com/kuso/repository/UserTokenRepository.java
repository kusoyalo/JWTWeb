package com.kuso.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kuso.entity.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken,String>{

}
