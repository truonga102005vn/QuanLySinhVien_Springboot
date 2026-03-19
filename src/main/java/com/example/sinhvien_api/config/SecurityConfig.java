package com.example.sinhvien_api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.sinhvien_api.security.JwtAuthFilter;
import com.example.sinhvien_api.security.OAuth2SuccessHandler;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtAuthFilter        = jwtAuthFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // OAuth2 cần session để lưu state/nonce trong quá trình xác thực
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .authorizeHttpRequests(auth -> auth
                // Tất cả trang HTML tĩnh: public
                .requestMatchers(
                    "/", "/*.html", "/index.html", "/favicon.ico",
                    "/css/**", "/js/**", "/images/**", "/static/**"
                ).permitAll()

                // Auth API: public
                .requestMatchers("/api/auth/**").permitAll()

                // GET sinh viên: public (không cần đăng nhập để xem)
                .requestMatchers(HttpMethod.GET, "/api/sinhvien", "/api/sinhvien/**").permitAll()

                // POST/PUT cần đăng nhập
                .requestMatchers(HttpMethod.POST, "/api/sinhvien").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/api/sinhvien/**").authenticated()

                // DELETE và admin: chỉ ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/sinhvien/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            // ── KEY FIX: OAuth2 login page trỏ về login.html của chúng ta ──
            // Không set loginPage → Spring dùng trang /login mặc định (trang trắng bạn thấy)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login.html")                          // <── QUAN TRỌNG
                .authorizationEndpoint(ep ->
                    ep.baseUri("/api/auth/oauth2/authorize"))
                .redirectionEndpoint(ep ->
                    ep.baseUri("/api/auth/oauth2/callback/*"))
                .successHandler(oAuth2SuccessHandler)
            )

            // Tắt form login mặc định của Spring
            .formLogin(AbstractHttpConfigurer::disable)

            // Tắt HTTP Basic Auth
            .httpBasic(AbstractHttpConfigurer::disable)

            // Khi API bị 401/403: trả JSON thay vì redirect về /login
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    // Chỉ redirect về login.html nếu là request từ browser (không phải API)
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"error\":\"Chưa đăng nhập\"}");
                    } else {
                        response.sendRedirect("/login.html");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"Không có quyền truy cập\"}");
                })
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}