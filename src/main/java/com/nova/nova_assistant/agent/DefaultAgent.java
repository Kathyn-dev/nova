package com.nova.nova_assistant.agent;

import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultAgent implements NovaAgent {

	@Override
	public boolean supports(String message) {
		return true;
	}

	@Override
	public String respond(String message) {
		return "Voce perguntou: " + message + ". Em breve vou responder usando inteligencia artificial.";
	}
}
