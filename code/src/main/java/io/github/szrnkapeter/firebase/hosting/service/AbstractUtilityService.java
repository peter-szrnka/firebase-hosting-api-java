package io.github.szrnkapeter.firebase.hosting.service;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

/**
 * @author Peter Szrnka
 * @since 0.8
 */
public abstract class AbstractUtilityService {

    protected final FirebaseHostingApiConfig config;
    protected final String accessToken;

    protected AbstractUtilityService(FirebaseHostingApiConfig config, String accessToken) {
        this.config = config;
        this.accessToken = accessToken;
    }

    protected void responseCallback(String function, Object response) {
        if(config.getServiceResponseListener() == null) {
            return;
        }

        config.getServiceResponseListener().getResponse(function, response);
    }
}
