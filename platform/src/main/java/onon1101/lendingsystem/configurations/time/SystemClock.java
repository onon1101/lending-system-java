package onon1101.lendingsystem.configurations.time;

import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class SystemClock implements IClock {

    private final Clock clock;

    public SystemClock() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public Instant now() {
        return clock.instant();
    }
}
