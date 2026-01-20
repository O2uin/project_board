package com.example.board_renewal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = "com.example.board_renewal")
public class BoardRenewalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardRenewalApplication.class, args);
	}

}
