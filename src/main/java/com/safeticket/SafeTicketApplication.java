package com.safeticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SafeTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafeTicketApplication.class, args);
    }

}
