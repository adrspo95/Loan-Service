package app.configuration.security.global;

/**
 * Created by GW95IB on 2017-05-25.
 */
public enum SecurityProps {
    BASIC_REALM_NAME("BASIC_REALM_NAME"),
    ROLE_PREFIX("ROLE_");

    private final String displayName;

    SecurityProps(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
