package br.edu.ifrs.tcc.backend;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.edu.ifrs.tcc.backend.config.CreateDataBase;

@SpringBootApplication
public class BackendApplication {
	public static void main(String[] args) {
		new CreateDataBase().createPmelDB();
		SpringApplication.run(BackendApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}