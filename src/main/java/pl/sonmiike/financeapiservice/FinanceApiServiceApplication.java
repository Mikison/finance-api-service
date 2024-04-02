package pl.sonmiike.financeapiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "pl.sonmiike.financeapiservice")
public class FinanceApiServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(FinanceApiServiceApplication.class, args);
    }

}
