package com.jwttest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class MyConfiguration {

	@Bean
	public InMemoryUserDetailsManager user() {
		return new InMemoryUserDetailsManager(
				User.withUsername("karu")
				.password("{noop}password")
				.authorities("read")
				.build()
				);
	}
	
	private final RsaConfig rsaconf;
	public MyConfiguration(RsaConfig rsaconf) {
		this.rsaconf = rsaconf;
	}
	
	@SuppressWarnings({ "deprecation", "removal" })
	@Bean
	//security filter chain has the settings that you need your security extension to do/have
	//in this case, we disable csrf, and by doind so we disable sessions to protect againg csrf attacks
	//Since there are no csrf, we have to tell the system to authenticate every request that comes in
	
	public SecurityFilterChain security(HttpSecurity http) throws Exception {
		return http
				.csrf((csrf) -> csrf.disable())
				.authorizeRequests((authorizeRequests)->
				authorizeRequests.anyRequest().authenticated())
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.sessionManagement((sessionManagement)->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.httpBasic(Customizer.withDefaults())
				.build();
	}
	
	@Bean
	JwtDecoder jwtDecoder(){
		return NimbusJwtDecoder.withPublicKey(rsaconf.publicKey()).build();
	}
	
	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(rsaconf.publicKey()).privateKey(rsaconf.privateKey()).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}
}
