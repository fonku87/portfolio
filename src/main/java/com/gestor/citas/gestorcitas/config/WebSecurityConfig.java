package com.gestor.citas.gestorcitas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/assets/**").permitAll()
                                .requestMatchers("/appointments/**").authenticated()
                                .requestMatchers("/users").authenticated()
                                .requestMatchers("/products/**").authenticated()
                                .requestMatchers("/appointment/**").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(form -> form
                                    .usernameParameter("email")
                                    .defaultSuccessUrl("/appointments")
                                    .permitAll()
                )
                .logout(logout -> logout
                                    .logoutSuccessUrl("/")
                                    .permitAll()
                )
        ;

        return http.build();
    }

}