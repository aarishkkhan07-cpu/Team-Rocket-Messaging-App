package com.example.team_rocket.config;


import com.example.team_rocket.messaging.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    )
            throws Exception {


        http

                /*
                 * JWT authentication is stateless.
                 */
                .csrf(
                        csrf ->
                                csrf.disable()
                )


                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )


                .authorizeHttpRequests(
                        auth ->
                                auth

                                        /*
                                         * Authentication APIs
                                         */
                                        .requestMatchers(
                                                "/api/auth/register",
                                                "/api/auth/login"
                                        )
                                        .permitAll()


                                        /*
                                         * Frontend
                                         */
                                        .requestMatchers(
                                                "/",
                                                "/index.html",
                                                "/register.html",
                                                "/users.html",
                                                "/css/**",
                                                "/js/**"
                                        )
                                        .permitAll()


                                        /*
                                         * H2 Console
                                         */
                                        .requestMatchers(
                                                "/h2-console/**"
                                        )
                                        .permitAll()


                                        /*
                                         * Everything else
                                         * requires JWT.
                                         */
                                        .anyRequest()
                                        .authenticated()
                )


                /*
                 * Allow H2 console iframe.
                 */
                .headers(
                        headers ->
                                headers.frameOptions(
                                        frame ->
                                                frame.sameOrigin()
                                )
                )


                /*
                 * Execute JWT filter before
                 * Spring's username/password filter.
                 */
                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}