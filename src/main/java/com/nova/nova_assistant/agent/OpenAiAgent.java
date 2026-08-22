package com.nova.nova_assistant.agent;

import com.nova.nova_assistant.ai.OpenAiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class OpenAiAgent implements NovaAgent {

	private static final Logger log = LoggerFactory.getLogger(OpenAiAgent.class);

	private final OpenAiClient openAiClient;

	public OpenAiAgent(OpenAiClient openAiClient) {
		this.openAiClient = openAiClient;
	}

	@Override
	public boolean supports(String message) {
		return openAiClient.isConfigured();
	}

	@Override
	public String respond(String message) {
		try {
			return openAiClient.answer(message);
		}
		catch (RuntimeException exception) {
			log.warn("OpenAI request failed: {}", exception.getClass().getSimpleName());
			return "Tive um problema para consultar a inteligencia artificial agora. Pode tentar de novo em alguns instantes?";
		}
	}
}
