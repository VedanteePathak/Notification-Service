package org.vedantee.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.vedantee.common", "org.vedantee.web"})
@EntityScan(basePackages = {"org.vedantee.common.Models.Entities"})
@EnableJpaRepositories(basePackages = {"org.vedantee.common.Repositories"})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
