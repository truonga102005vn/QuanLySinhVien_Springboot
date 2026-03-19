package com.example.sinhvien_api.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.sinhvien_api.model.User;
import com.example.sinhvien_api.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = Logger.getLogger(OAuth2SuccessHandler.class.getName());

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil        = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = token.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId(); // "google" or "facebook"

        String email      = oAuth2User.getAttribute("email");
        String name       = oAuth2User.getAttribute("name");
        String picture    = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getName();

        User.AuthProvider provider = "google".equalsIgnoreCase(registrationId)
                ? User.AuthProvider.GOOGLE
                : User.AuthProvider.FACEBOOK;

        // Upsert user — tạo mới nếu chưa có, cập nhật nếu đã có
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .email(email)
                    .username(email.split("@")[0] + "_" + provider.name().toLowerCase())
                    .fullName(name)
                    .avatarUrl(picture)
                    .provider(provider)
                    .providerId(providerId)
                    .role(User.Role.USER)
                    .enabled(true)
                    .build();
            return userRepository.save(u);
        });

        // Cập nhật avatar và providerId mỗi lần login
        user.setAvatarUrl(picture);
        user.setProviderId(providerId);
        userRepository.save(user);

        // Tạo JWT
        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        // ── FIX: URL redirect đúng, không bị thừa đoạn encode ──────────
        // Bản cũ bị lỗi: có thêm URLEncoder.encode(jwt) thừa không có key
        // → URL sai dạng: ?token=xxx&email=abcEYJ...&name=yyy
        String redirectUrl = frontendUrl + "/sinhvien.html"
                + "?token=" + URLEncoder.encode(jwt,                          StandardCharsets.UTF_8)
                + "&name="  + URLEncoder.encode(name  != null ? name  : "",   StandardCharsets.UTF_8)
                + "&role="  + URLEncoder.encode(user.getRole().name(),         StandardCharsets.UTF_8)
                + "&avatar="+ URLEncoder.encode(picture != null ? picture : "", StandardCharsets.UTF_8);

        log.info("OAuth2 login success: " + email + " (" + provider + ") → redirect to frontend");
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}