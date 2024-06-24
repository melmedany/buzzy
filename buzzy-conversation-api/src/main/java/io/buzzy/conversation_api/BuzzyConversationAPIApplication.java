package io.buzzy.conversation_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BuzzyConversationAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuzzyConversationAPIApplication.class, args);
	}

}
