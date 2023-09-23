package com.example.NoSQLDecentralizedDatabase;

import com.example.NoSQLDecentralizedDatabase.services.BootstrapperNodeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class NoSqlDecentralizedDatabaseApplication {

	public static void main(String[] args) {
		if (System.getenv("BOOTSTRAPPER") != null) {
			BootstrapperNodeService.start();
		}
		SpringApplication.run(NoSqlDecentralizedDatabaseApplication.class, args);
	}

}
