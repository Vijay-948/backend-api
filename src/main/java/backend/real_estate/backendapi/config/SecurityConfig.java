package backend.real_estate.backendapi.config;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//                        .cors().configurationSource(new CorsConfigurationSource() {
//                        @Override
//                        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                            CorsConfiguration cfg = new CorsConfiguration();
//                            cfg.setAllowedOrigins(List.of("http://localhost:3000"));
//                            cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "Authorization", "Content-Type", "Accept"));
//                            return cfg;
//                        }
//                    });

        return http.build();
    }

//    @Bean
//    public FilterRegistrationBean coresFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.setAllowCredentials(true);
//        corsConfiguration.addAllowedOriginPattern("*");
//        corsConfiguration.addAllowedHeader("Authorization");
//        corsConfiguration.addAllowedHeader("Content-Type");
//        corsConfiguration.addAllowedHeader("Accept");
//        corsConfiguration.addAllowedHeader("POST");
//        corsConfiguration.addAllowedHeader("GET");
//        corsConfiguration.addAllowedHeader("PUT");
//        corsConfiguration.addAllowedHeader("DELETE");
//        corsConfiguration.addAllowedHeader("OPTIONS");
//        corsConfiguration.setMaxAge(3600L);
//
//        source.registerCorsConfiguration("/**", corsConfiguration);
//
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//
//        return bean;
//
//    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
//        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }


}
