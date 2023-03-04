package com.bookSuppliment.server.app.filter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

public class SimpleAuthenticationManager implements AuthenticationManager {

    private List<AuthenticationProvider> authenticationProviders;

    public SimpleAuthenticationManager() {
        authenticationProviders = new ArrayList<>();
    }

    public void setAuthenticationProviders(List<AuthenticationProvider> authenticationProviders) {
        this.authenticationProviders = authenticationProviders;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Authentication result = null;
        for (AuthenticationProvider authenticationProvider : authenticationProviders) {
            try {
                result = authenticationProvider.authenticate(authentication);
                if (result != null) {
                    break;
                }
            } catch (AuthenticationServiceException e) {
                // ignore and try the next provider
            }
        }
        if (result == null) {
            throw new AuthenticationServiceException("Unable to authenticate user");
        }
        return result;
    }

}

