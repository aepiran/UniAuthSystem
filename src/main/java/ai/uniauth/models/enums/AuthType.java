package ai.uniauth.models.enums;

/**
 * Loại hình xác thực được hỗ trợ bởi hệ thống
 */
public enum AuthType {
    JWT("JWT Token"),
    API_KEY("API Key"),
    OAUTH2("OAuth 2.0"),
    SAML("SAML 2.0"),
    LDAP("LDAP/Active Directory"),
    BASIC("Basic Authentication"),
    API_KEY_WITH_SECRET("API Key with Secret"),
    HMAC("HMAC Signature"),
    CUSTOM("Custom Authentication"),
    HYBRID("Hybrid Authentication"),
    SOCIAL("Social Login"),
    CERTIFICATE("Certificate-based"),
    BIOMETRIC("Biometric Authentication"),
    PASSWORDLESS("Passwordless"),
    MAGIC_LINK("Magic Link"),
    TOTP("Time-based OTP"),
    HOTP("HMAC-based OTP"),
    U2F("Universal 2nd Factor"),
    FIDO2("FIDO2/WebAuthn");

    private final String description;

    AuthType(String description) {
        this.description = description;
    }

    public static AuthType fromString(String value) {
        for (AuthType type : AuthType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AuthType: " + value);
    }

    public String getDescription() {
        return description;
    }

    public boolean isTokenBased() {
        return this == JWT || this == OAUTH2 || this == SAML;
    }

    public boolean isKeyBased() {
        return this == API_KEY || this == API_KEY_WITH_SECRET || this == HMAC;
    }

    public boolean isPasswordBased() {
        return this == BASIC || this == LDAP;
    }

    public boolean isModern() {
        return this == FIDO2 || this == PASSWORDLESS || this == BIOMETRIC;
    }
}