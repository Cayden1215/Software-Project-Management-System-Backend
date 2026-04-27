package com.softwareprojectmanagement.backend.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .authorizeHttpRequests(auth -> auth
                // Allow anyone to access the login/register endpoints
                .requestMatchers("/api/auth/**","/v3/api-docs",
                "/v3/api-docs.yaml",
                "/swagger-ui/**",
                "/swagger-ui.html").permitAll()
                
                // Only Project Managers can manage projects
                .requestMatchers("/api/projects/**").hasRole("PROJECT_MANAGER")
                
                // Only Project Managers can access scheduling endpoints
                .requestMatchers("/api/v1/scheduling/**").hasRole("PROJECT_MANAGER")
                
                // Both roles can interact with tasks
                .requestMatchers("/api/tasks/**").hasAnyRole("PROJECT_MANAGER", "TEAM_MEMBER")
                
                // Any other request must be authenticated
                .anyRequest().authenticated()
            )
            // Tell Spring we are using stateless JWTs, not server-side sessions
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            
            // Insert our custom JWT filter BEFORE the standard username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // Allow your frontend origin
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); 
    // Allow specific HTTP methods
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // Allow necessary headers (like Authorization for your JWT)
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
    // Allow credentials if you plan to use cookies/sessions later
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
}
