package com.interviewpro.interviewpro.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.interviewpro.interviewpro.auth.jwt.JwtFilter;
import com.interviewpro.interviewpro.auth.service.EntryPointService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private EntryPointService userDetails;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
        		.authorizeHttpRequests(req -> req
        			    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        			    // 🔓 PUBLIC AUTH APIs
        			    .requestMatchers(
        			    		"/interviewpro/entry/v1/register/request-otp",
        			    		"/interviewpro/entry/v1/register/verify-otp",
        			    	"/interviewpro/entry/v1/forgot-password",
        			        "/interviewpro/entry/v1/login",
        			        "/interviewpro/entry/v1/register",
        			        "/interviewpro/entry/v1/password-change/verify-otp"
        			    ).permitAll()

        			    // 🔓 PUBLIC: View stats and tests (GET only)
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/home/stats").permitAll()
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/home/tests/by-type").permitAll()
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/mcq/v1/common/**").permitAll()
        			    
        			    // 🔓 COMMUNICATION: Public GET access to view tests and questions
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/communication/tests").permitAll()
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/communication/tests/**").permitAll()
        			    .requestMatchers(HttpMethod.GET, "/api/resume/**").permitAll()
        			    
        			    // 🔐 PROTECTED: Resume analysis and test operations require auth
        			    .requestMatchers(HttpMethod.POST, "/api/resume/analyze").authenticated()
        			    .requestMatchers(HttpMethod.POST, "/interviewpro/coding/v1/start-attempt/**").authenticated()
        			    .requestMatchers(HttpMethod.POST, "/interviewpro/coding/submit").authenticated()
        			    
        			    // 🔓 PUBLIC but specific endpoints
        			    .requestMatchers("/interviewpro/coding/run").permitAll()
        			    .requestMatchers("/interviewpro/coding/languages").permitAll()
        			    
        			    // � My results endpoint - allow public access
        			    .requestMatchers(HttpMethod.GET, "/interviewpro/coding/v1/my-results").permitAll()
        			    
        			    // 🔓 Allow AssemblyAI webhook
        	            .requestMatchers("/interviewpro/communication/webhook/assemblyai").permitAll()
        	            
        			    // 🔓 Allow contact form submission without auth
        			    .requestMatchers("/interviewpro/api/contact").permitAll()
        			    
        			    // 🔓 Static files
        			    .requestMatchers(
        			        "/",
        			        "/login",
        			        "/index.html",
        			        "/assets/**"
        			    ).permitAll()

        			    // 🔐 Everything else requires authentication
        			    .anyRequest().authenticated()
        			)            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(BCryptPasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetails);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
    
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_CREATOR > ROLE_STUDENT
        """);
        return hierarchy;
    }

}

