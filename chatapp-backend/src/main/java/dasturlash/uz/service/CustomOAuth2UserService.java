package dasturlash.uz.service;

import dasturlash.uz.entity.User;
import dasturlash.uz.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2UserService oauth2UserService;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2 authentication request received from provider: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oauthUser = super.loadUser(userRequest);
        log.info("OAuth2 user loaded: {}", oauthUser.getAttributes());

        String providerName = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        try {
            String providerId = extractProviderId(oauthUser, providerName);
            String email = extractEmail(oauthUser, providerName);
            String name = extractName(oauthUser, providerName);

            log.info("Extracted OAuth2 user details - provider: {}, providerId: {}, email: {}, name: {}", providerName, providerId, email, name);

            if (providerId == null || (email == null && providerName.equals("GOOGLE"))) {
                log.warn("Missing required attributes for OAuth2 user. Provider: {}, providerId: {}, email: {}", providerName, providerId, email);
                return oauthUser;
            }

            log.info("About to register/update user with provider: {}, providerId: {}", providerName, providerId);
            User user = oauth2UserService.registerOAuthUser(oauthUser, providerId, email, name, providerName);

            log.info("User successfully registered/updated in database. UserID: {}", user.getUserId());
            return oauthUser;
        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            throw e;
        }
    }

    private String extractProviderId(OAuth2User oauthUser, String providerName) {
        log.debug("Extracting provider ID for: {}", providerName);
        switch (providerName) {
            case "GOOGLE":
                return oauthUser.getAttribute("sub");
            case "GITHUB":
                Object id = oauthUser.getAttribute("id");
                return id != null ? id.toString() : null;
            default:
                log.error("Unsupported provider: {}", providerName);
                throw new IllegalArgumentException("Unsupported provider: " + providerName);
        }
    }

    private String extractEmail(OAuth2User oauthUser, String providerName) {
        log.debug("Extracting email for: {}", providerName);
        return oauthUser.getAttribute("email");
    }

    private String extractName(OAuth2User oauthUser, String providerName) {
        log.debug("Extracting name for: {}", providerName);
        return oauthUser.getAttribute("name");
    }
}
