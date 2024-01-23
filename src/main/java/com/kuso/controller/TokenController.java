package com.kuso.controller;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.kuso.entity.UserToken;
import com.kuso.repository.UserTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/JWTWeb/Token")
public class TokenController{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserTokenRepository userTokenRepository;
	
	@RequestMapping("/createToken")
	public String createToken(@RequestParam Map<String,String> claimMap){
		logger.info("createToken");
		logger.info("claimMap={}",claimMap);
		
		Map<String,String> returnMap = new HashMap<String,String>();
		returnMap.put("result","FALSE");
		
		Gson gson = new Gson();
		
		try{
			//加密方式與Token類型
			Map<String,Object> headerMap = new HashMap<String,Object>();
			headerMap.put("typ","JWT");
			headerMap.put("alg","HS256");
			
			//儲存的資料
			Claims claims = Jwts.claims();
			claims.putAll(claimMap);
			
		    JwtBuilder jwtBuilder = Jwts.builder();
		    jwtBuilder.setHeader(headerMap);
		    
		    Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		    jwtBuilder.signWith(secretKey);
		    
		    jwtBuilder.setClaims(claims);
		    
			String token = jwtBuilder.compact();
			
			logger.info("token={}",token);
			
			UserToken userToken = new UserToken();
			
			String uuid = UUID.randomUUID().toString();
			logger.info("uuid={}",uuid);
			userToken.setTokenUUID(uuid);
			String secretKeyString = Encoders.BASE64.encode(secretKey.getEncoded());
			userToken.setSecretKey(secretKeyString);
			userToken.setToken(token);
			userToken.setCreateDate(new Date());
			
			userTokenRepository.save(userToken);
			
			returnMap.put("result","TRUE");
			returnMap.put("token",token);
			returnMap.put("tokenUUID",uuid);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			
			returnMap.put("message",e.getMessage());
		}
		
		return gson.toJson(returnMap);
	}
	@RequestMapping("/verifyToken")
	public String verifyToken(@RequestParam Map<String,String> requestParamMap){
		logger.info("verifyToken");
		logger.info("requestParamMap={}",requestParamMap);
		
		Map<String,String> returnMap = new HashMap<String,String>();
		returnMap.put("result","FALSE");
		
		Gson gson = new Gson();
		
		try{
			String tokenUUID = requestParamMap.get("tokenUUID");
			String token = requestParamMap.get("token");
			
			Optional<UserToken> userTokenOptional = userTokenRepository.findById(tokenUUID);
			
			if(!userTokenOptional.isPresent()){
				returnMap.put("message","找不到Token");
				return gson.toJson(returnMap);
			}
			
			UserToken userToken = userTokenOptional.get();
			
			Key secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(userToken.getSecretKey()));
			
			JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder();
			jwtParserBuilder.setSigningKey(secretKey);
			JwtParser jwtParser = jwtParserBuilder.build();
			
			Claims body = jwtParser.parseClaimsJws(token).getBody();
			
			for(String key : body.keySet()){
				logger.info("key={}，value= {}",key,body.get(key));
			}
			
			returnMap.put("result","TRUE");
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			
			returnMap.put("message",e.getMessage());
		}
		
		return gson.toJson(returnMap);
	}
}