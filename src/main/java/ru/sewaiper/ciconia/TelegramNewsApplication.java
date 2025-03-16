package ru.sewaiper.ciconia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "ru.sewaiper.ciconia.configuration.properties")
public class TelegramNewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramNewsApplication.class);
    }
}