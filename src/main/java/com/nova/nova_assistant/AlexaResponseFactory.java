package com.nova.nova_assistant;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class AlexaResponseFactory {

	public Map<String, Object> say(String text) {
		return response(text, false);
	}

	public Map<String, Object> end(String text) {
		return response(text, true);
	}

	public Map<String, Object> emptyEndedSession() {
		return Map.of(
			"version", "1.0",
			"response", Map.of("shouldEndSession", true)
		);
	}

	private Map<String, Object> response(String text, boolean shouldEndSession) {
		return Map.of(
			"version", "1.0",
			"sessionAttributes", Map.of(),
			"response", Map.of(
				"outputSpeech", Map.of(
					"type", "PlainText",
					"text", text
				),
				"shouldEndSession", shouldEndSession
			)
		);
	}
}
