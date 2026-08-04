package onon1101.lendingsystem.auth.resetPassword;

import onon1101.lendingsystem.sharedkernel.IResult;

public record ResetPasswordResult(String email) implements IResult {
}
