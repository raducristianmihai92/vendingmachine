package net.mvpmatch.vendingmachine.config;

import net.mvpmatch.vendingmachine.security.UsersAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsersAuthenticationProvider usersAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usersAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                   .antMatchers(HttpMethod.POST, "/users").permitAll()
                   .antMatchers(HttpMethod.GET, "/users").authenticated()
                   .antMatchers(HttpMethod.GET, "/users/id/*").authenticated()
                   .antMatchers(HttpMethod.PUT, "/users").authenticated()
                   .antMatchers(HttpMethod.DELETE, "/users/id/*").authenticated()
                   .antMatchers(HttpMethod.GET,"/products").permitAll()
                   .antMatchers("/")
                   .permitAll()
                   .and().httpBasic();
    }
}
