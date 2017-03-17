package com.pictures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Same as @Configuration @EnableAutoConfiguration @ComponentScan
public class PictureMvcApplication {
	public static void main(String[] args) {
        SpringApplication.run(PictureMvcApplication.class, args);
    }
}
