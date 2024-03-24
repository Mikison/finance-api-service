package pl.sonmiike.financeapiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pl.sonmiike")
public class FinanceApiServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(FinanceApiServiceApplication.class, args);
    }

}
