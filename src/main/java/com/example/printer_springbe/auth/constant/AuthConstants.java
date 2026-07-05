package com.example.printer_springbe.auth.constant;

public final class AuthConstants {

    public static final String REGISTRATION_TOKEN_HEADER = "X-Registration-Token";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTH_USER_REQUEST_ATTR = "authenticatedUser";
    public static final String TOKEN_TYPE_REGISTRATION = "Registration";
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public static final String BEARER_PREFIX = "Bearer ";

    private AuthConstants() {
    }
}
