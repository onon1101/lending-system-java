package onon1101.lendingsystem.auth.commons;

import onon1101.lendingsystem.sharedkernel.domain.Enumeration;

public final class UserStatus extends Enumeration<Integer, String> {

    public static final UserStatus ACTIVE = new UserStatus(1, "active");
    public static final UserStatus SUSPENDED = new UserStatus(2, "suspended");
    public static final UserStatus DELETED = new UserStatus(3, "deleted");

    private UserStatus(Integer key, String value) {
        super(key, value);
    }
}
