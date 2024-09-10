package com.TaiNguyen.EurekaSerice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaSericeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaSericeApplication.class, args);
	}

}
