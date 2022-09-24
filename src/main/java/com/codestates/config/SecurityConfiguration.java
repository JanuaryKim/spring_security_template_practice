package com.codestates.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("kevin@gmail.com")
//                        .password("1111")
//                        .roles("USER")
//                        .build();
//
//        // (1)
//        UserDetails admin =
//                User.withDefaultPasswordEncoder()
//                        .username("admin@gmail.com")
//                        .password("2222")
//                        .roles("ADMIN")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//                .formLogin()
//                .loginPage("/auths/login-form")
//                .loginProcessingUrl("/process_login")
//                .failureUrl("/auths/login-form?error")
//                .and()
//                .exceptionHandling().accessDeniedPage("/auths/access-denied")
//                .and()
//                .authorizeHttpRequests()
//                .anyRequest()
//                .permitAll();

        http.headers().frameOptions().sameOrigin() // (1)
                .and()
                .csrf().disable() // (2)
                .formLogin() //(3)
                .loginPage("/auths/login-form") //(3)
                .loginProcessingUrl("/process_login") // (4)
                .failureUrl("/auths/login-form?error") // (5)
                .and()
                .logout() //(6)
                .logoutUrl("/logout") // (7)
                .logoutSuccessUrl("/") // (8)
                .and()
                .exceptionHandling().accessDeniedPage("/auths/access-denied") // (9)
                .and()
                .authorizeHttpRequests(authorize -> authorize // (10)
                        .antMatchers("/orders/**").hasRole("ADMIN")
                        .antMatchers("/members/my-page").hasRole("USER")
                        .antMatchers("/**").permitAll()
                );


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }
}
