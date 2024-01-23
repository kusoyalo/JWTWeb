package com.kuso.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_token")
public class UserToken{
	@Id
	@Column(name = "token_uuid",nullable = false)
    private String tokenUUID;
	
	@Column(name = "secret_key",nullable = false)
    private String secretKey;
	
	@Column(name = "token",nullable = false)
    private String token;
	
	@Column(name = "create_date",nullable = false)
    private Date createDate;
	
	@Column(name = "expirate_date")
    private Date expirateDate;
}
