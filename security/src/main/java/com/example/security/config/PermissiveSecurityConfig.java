package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class PermissiveSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Autoriza todas las peticiones sin necesidad de autenticación
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // Deshabilita el formulario de Login por defecto
                .formLogin(form -> form.disable())
                // Deshabilita la protección CSRF para facilitar pruebas en formularios POST
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}

