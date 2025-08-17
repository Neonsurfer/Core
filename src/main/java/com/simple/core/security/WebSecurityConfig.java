package com.simple.core.security;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class WebSecurityConfig {

    private static String INTERNAL_TOKEN;

    public WebSecurityConfig(@Value("${internal.token:simple}") String internalToken) {
        INTERNAL_TOKEN = internalToken;
    }

    @PostConstruct
    public void init() {
        System.out.println("Loaded internal token for TICKET module: " + INTERNAL_TOKEN);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username("simple")
                .password("simple")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

    static class InternalServiceAuthFilter extends AbstractPreAuthenticatedProcessingFilter {
        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            String token = request.getHeader("X-Internal-Token");
            if (INTERNAL_TOKEN.equals(token)) {
                return "internal-service";
            }
            return null;
        }

        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            return "N/A";
        }
    }
}