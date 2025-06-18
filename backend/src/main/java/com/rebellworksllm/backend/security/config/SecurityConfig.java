package com.rebellworksllm.backend.security.config;

import com.rebellworksllm.backend.security.HubSpotSecurityFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final HubSpotSecurityFilter hubSpotSecurityFilter;

    public SecurityConfig(HubSpotSecurityFilter hubSpotSecurityFilter) {
        this.hubSpotSecurityFilter = hubSpotSecurityFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(@NonNull HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/whatsapp/**").permitAll()
                        .requestMatchers("/api/v1/hubspot/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(hubSpotSecurityFilter, UsernamePasswordAuthenticationFilter.class);
//                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS));
        return http.build();
    }
}