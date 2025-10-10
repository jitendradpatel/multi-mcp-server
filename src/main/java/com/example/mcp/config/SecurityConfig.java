package com.example.mcp.config;

import com.example.mcp.jwe.JweAuthFilter;
import com.example.mcp.jwe.JweUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JweUtils jweUtils) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/mcp/puiblic/**").hasRole("admin")
                    .requestMatchers("/mcp/user/**").hasRole("user")
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JweAuthFilter(jweUtils), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JweUtils jweUtils() throws Exception {
        return JweUtils.getInstance();
    }
}
