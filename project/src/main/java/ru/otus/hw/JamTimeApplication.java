package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JamTimeApplication {
    public static void main(String[] args) {
        SpringApplication.run(JamTimeApplication.class, args);
        System.out.printf("Чтобы перейти на страницу сайта: %n%s%n",
                "http://localhost:8080");
    }
}