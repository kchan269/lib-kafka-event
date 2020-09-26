package com.kafka.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibKafkaEventApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibKafkaEventApplication.class, args);
		System.out.println("hello");
	}

}
