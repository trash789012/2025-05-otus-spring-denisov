package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
		System.out.printf("Чтобы перейти на страницу сайта: %n%s%n",
				"http://localhost:8080");
		System.out.printf("Actuator: %n%s%n",
				"http://localhost:8080/actuator");
		System.out.printf("HAL Explorer: %n%s%n",
				"http://localhost:8080/datarest");
	}

}