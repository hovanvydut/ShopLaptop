package com.shoplaptop.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shoplaptop.common.entity", "com.shoplaptop.common.user"})
public class ShopLaptopBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopLaptopBackendApplication.class, args);
	}

}
