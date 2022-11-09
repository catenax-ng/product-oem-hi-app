package net.catena_x.btp.hi.oem.backend.hi_service;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan(basePackages = {
		"net.catena_x.btp.libraries.oem.backend.database.rawdata"		// TODO later "rawdata" can be removed here for HIdb-libraries
})
public class OemHiBackendServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder()
		.sources(OemHiBackendServiceApplication.class)
		.profiles("hibackendservice")
		.run(args);
	}
}
