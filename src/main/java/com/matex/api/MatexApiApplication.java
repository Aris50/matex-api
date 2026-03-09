package com.matex.api;

import com.matex.api.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class MatexApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatexApiApplication.class, args);
	}

}
