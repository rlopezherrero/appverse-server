package org.appverse.web.framework.backend.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.appverse.web.framework.backend.security.oauth2.login.CustomUserNamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user")
		.password("password").roles("USER");
    }

    /*
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/images/**", "/oauth/uncache_approvals", "/oauth/cache_approvals");
    }
    */

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();

    	CustomUserNamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUserNamePasswordAuthenticationFilter(authenticationManagerBean());
    	customUsernamePasswordAuthenticationFilter.setUserNamePasswordAuthenticationUri("/oauth/authorize");
    	http.addFilterBefore(customUsernamePasswordAuthenticationFilter, BasicAuthenticationFilter.class);    	
    }
}
