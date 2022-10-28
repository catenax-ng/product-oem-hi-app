package net.catena_x.btp.hi.oem.frontend;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan
@OpenAPIDefinition(info = @Info(title = "Health indicator frontend", version = "0.0.99"))
public class OemHiFrontendApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder()
		.sources(OemHiFrontendApplication.class)
		.profiles("hifrontend")
		.run(args);
	}
}
