package com.web.service.addmix_store.handlers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.service.addmix_store.models.User;
import com.web.service.addmix_store.services.UserService;
import com.web.service.addmix_store.utils.auth.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


// Entry point for sign in with google
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract user info from Google
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        // Find or create user
        User user = userService.findOrCreateGoogleUser(email, name, firstName, lastName);

        // Generate access token
        String accessToken = jwtUtil.generateAccessToken(user);
        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(user);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("mobile", user.getMobileNumber());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("provider", user.getProvider());

        // Set refresh token as HttpOnly cookie
        ResponseCookie refreshCookie = jwtUtil.buildCookieForRefreshToken(refreshToken);
        
        response.addHeader("Set-Cookie", refreshCookie.toString());

        String userJson = objectMapper.writeValueAsString(userData);

        // Send access token & user info to frontend via redirect (safe in query param or fragment)
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("token", accessToken)
                .queryParam("user", userJson)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
