package onon1101.lendingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LendingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LendingSystemApplication.class, args);
    }
}
