package com.security.security.config;

import com.security.security.domain.UserProvider;
import com.security.security.repository.UserProvidersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserProvidersRepository userProviderRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String registrationId = request.getRequestURI().contains("facebook") ? "FACEBOOK" : "GOOGLE";

        String email = (String) oauthUser.getAttributes().get("email");
        String name = (String) oauthUser.getAttributes().get("name");

        Optional<UserProvider> userOpt = userProviderRepository.findByEmail(email);

        UserProvider user;
        if (userOpt.isEmpty()) {

            user = UserProvider.builder()
                    .email(email)
                    .name(name)
                    .provider(registrationId)
                    .build();
            userProviderRepository.save(user);
        } else {
            user = userOpt.get();
            user.setName(name);
            userProviderRepository.save(user);
        }

        String token = jwtService.generateTokenProvider(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"token\": \"%s\", \"email\": \"%s\", \"name\": \"%s\"}",
                token, user.getEmail(), user.getName()
        );

        response.getWriter().write(jsonResponse);
    }
}


