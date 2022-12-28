package com.prajekpro.api.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        log.debug("Configuring Security");

        http
                .authorizeRequests()
                .antMatchers("/oauth/token**", "/api/pp/users/login", "/notification**", "/oauth/authorize**", "/public**", "/master**")
//                .antMatchers("/oauth/token**", "/api/pp/users/login", "/notification**", "/oauth/authorize**", "/public**")
                .permitAll()
                .anyRequest()
                .authenticated();

//		http
//			.oauth2ResourceServer()
//			.bearerTokenResolver(this::tokenResolver);
    }

//	private BearerTokenResolver tokenResolver(HttpServletRequest request) {

//		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//	    
//		if (header != null)
//	      return header.replace("Bearer ", "");
//	    
//		Cookie cookie = WebUtils.getCookie(request, "auth.access_token");
//		if (cookie != null)
//	      return cookie.getValue();

//		return null;
//	}


    @Override
    public void configure(WebSecurity web) {

        web.ignoring().antMatchers(
                "/download/**",
                "/api/pp/public/**",
                "/api/pp/master/**",
                "/swagger-ui.html/**",
                "/configuration/**",
                "/swagger-resources/**",
                "/v2/api-docs",
                "/webjars/**",
                "/api/pp/payments/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}