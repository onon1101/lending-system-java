package onon1101.lendingsystem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "Lending System API",
                        version = "v1",
                        description = "借貸系統後端 API 文件"))
@SpringBootApplication
@ConfigurationPropertiesScan
public class LendingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LendingSystemApplication.class, args);
    }
}
