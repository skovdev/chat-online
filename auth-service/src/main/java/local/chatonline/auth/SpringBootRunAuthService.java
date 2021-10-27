package local.chatonline.auth;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication
public class SpringBootRunAuthService {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRunAuthService.class, args);
    }
}