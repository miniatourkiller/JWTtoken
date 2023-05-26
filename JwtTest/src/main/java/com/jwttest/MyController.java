package com.jwttest;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwtservices.TokenService;


@RestController
public class MyController {
@RequestMapping("/")
public String Home(Principal principal) {
	return "hello "+principal.getName();
}

@Autowired
private TokenService tokenService;


@RequestMapping(value="token", method= RequestMethod.POST)
public String Token(Authentication auth) {
	Logger log = LoggerFactory.getLogger(MyController.class);
	log.debug("token requested for user:'{}'", auth.getName());
	String token= this.tokenService.generateToken(auth);
	log.debug("The token granted:'{}'", token);
	return token;
}
}
