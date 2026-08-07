package onon1101.lendingsystem.auth.commons;

import onon1101.lendingsystem.sharedkernel.domain.Enumeration;

public final class IdentityProvider extends Enumeration<Integer, String> {

    public static final IdentityProvider PASSWORD = new IdentityProvider(1, "password");

    public static final IdentityProvider GOOGLE = new IdentityProvider(2, "google");

    public static final IdentityProvider APPLE = new IdentityProvider(3, "apple");

    public static final IdentityProvider GITHUB = new IdentityProvider(4, "github");

    private IdentityProvider(Integer key, String value) {
        super(key, value);
    }
}
