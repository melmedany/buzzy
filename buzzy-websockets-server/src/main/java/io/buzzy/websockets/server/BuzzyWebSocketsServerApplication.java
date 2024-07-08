package io.buzzy.websockets.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BuzzyWebSocketsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuzzyWebSocketsServerApplication.class, args);
	}

}
