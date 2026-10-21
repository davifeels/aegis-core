
package io.aegiscore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${AEGIS_ADMIN_USERNAME}") String adminUsername,
            @Value("${AEGIS_ADMIN_PASSWORD}") String adminPassword,
            @Value("${AEGIS_USER_USERNAME:}") String userUsername,
            @Value("${AEGIS_USER_PASSWORD:}") String userPassword) {

        UserDetails admin = User.withUsername(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        if (userUsername.isBlank() && userPassword.isBlank()) {
            return new InMemoryUserDetailsManager(admin);
        }

        if (userUsername.isBlank() || userPassword.isBlank()) {
            throw new IllegalStateException(
                    "Both AEGIS_USER_USERNAME and AEGIS_USER_PASSWORD "
                            + "must be configured together"
            );
        }

        UserDetails user = User.withUsername(userUsername)
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/assets",
                                "/api/v1/assets/**"
                        ).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/assets",
                                "/api/v1/assets/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/assets/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/assets/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
