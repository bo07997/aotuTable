package com.baitian.autotable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;

/**
 * @author liudianbo
 */
@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) throws FileNotFoundException {
		SpringApplication.run(ClientApplication.class, args);
	}
}
