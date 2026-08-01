package onon1101.lendingsystem.auth.user;

import onon1101.lendingsystem.sharedkernel.domain.Enumeration;

public final class UserStatus extends Enumeration<Integer, String> {

    public static final UserStatus ACTIVE =
            new UserStatus(
                    1,
                    "ACTIVE");
    public static final UserStatus SUSPENDED =
            new UserStatus(
                    2,
                    "SUSPENDED");
    public static final UserStatus DELETED =
            new UserStatus(
                    3,
                    "DELETED");

    private UserStatus(
            Integer key,
            String value) {
        super(
                key,
                value);
    }
}
