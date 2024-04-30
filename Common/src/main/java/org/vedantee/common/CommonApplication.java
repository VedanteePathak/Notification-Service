package org.vedantee.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EntityScan(basePackages = {"org.vedantee.common.Models.Entities"})
//@EnableJpaRepositories(basePackages = {"org.vedantee.common.Repositories"})
public class CommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class, args);
    }
}
