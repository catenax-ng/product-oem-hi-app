package net.catena_x.btp.hi.oem.backend.hi_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
		"net.catena_x.btp.hi.oem.backend.hi_service",
		"net.catena_x.btp.libraries.notification",
		"net.catena_x.btp.libraries.oem.backend.model.dto",
		"net.catena_x.btp.libraries.oem.backend.cloud",
		"net.catena_x.btp.libraries.oem.backend.edc",
		"net.catena_x.btp.libraries.util",
		"net.catena_x.btp.libraries.edc",
		"net.catena_x.btp.libraries.oem.backend.database.rawdata"
})
@OpenAPIDefinition(info = @Info(title = "OEM hi backend service", version = "0.0.99"))
public class OemHiBackendServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder()
		.sources(OemHiBackendServiceApplication.class)
		.profiles("hibackendservice")
		.run(args);
	}
}
