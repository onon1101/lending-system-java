package onon1101.lendingsystem.auth.login;

import onon1101.lendingsystem.sharedkernel.IResult;

public record LoginResult(String accessToken, long expiresIn) implements IResult {}
