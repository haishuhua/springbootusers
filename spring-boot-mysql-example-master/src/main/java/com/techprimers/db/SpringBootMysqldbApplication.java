package com.techprimers.db;

import com.techprimers.db.kafka.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.techprimers.db.repository")
@EnableCaching
@SpringBootApplication
public class SpringBootMysqldbApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMysqldbApplication.class, args);
	}

	@Autowired
	private Sender sender;

	@Override
	public void run(String... strings) throws Exception {
		sender.send("Spring Kafka Producer and Consumer Example");
	}


}
