package org.example.patients.security;

import org.example.patients.security.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    //@Bean
    public JdbcUserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    //@Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        String encodedPassword = passwordEncoder.encode("1234");
        System.out.println("Mot de passe encodé : " + encodedPassword);

        return new InMemoryUserDetailsManager(
                User.withUsername("user1")
                        .password(encodedPassword)
                        .roles("USER")
                        .build(),
                User.withUsername("user2")
                        .password(encodedPassword)
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password(encodedPassword)
                        .roles("USER", "ADMIN")
                        .build()
        );
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(form -> form
                        .loginPage("/login")  // Page personnalisée
                        .defaultSuccessUrl("/")  // Redirection après login
                        .permitAll() )
                .userDetailsService(userDetailServiceImpl)
                .rememberMe(rm -> rm.key("votreCleSecreteUnique"))
                .exceptionHandling(eh -> eh.accessDeniedPage("/notAuthorized"))
                .authorizeHttpRequests(ar->ar.requestMatchers("/webjars/**").permitAll())
                .authorizeHttpRequests(ar->ar.requestMatchers("/deletePatient/**").hasRole("ADMIN"))
                .authorizeHttpRequests(ar->ar.requestMatchers("/admin/**").hasRole("ADMIN"))
                .authorizeHttpRequests(ar->ar.requestMatchers("/user/**").hasRole("USER"))
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
                .build();
    }
}