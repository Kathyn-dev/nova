package com.nova.nova_assistant;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/alexa")
public class AlexaController {

	private static final Logger log = LoggerFactory.getLogger(AlexaController.class);

	private final NovaService novaService;

	public AlexaController(NovaService novaService) {
		this.novaService = novaService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> handleAlexaRequest(@RequestBody JsonNode requestBody) {
		String requestType = requestBody.path("request").path("type").asText();
		log.info("Alexa request received: type={}", requestType);

		if ("LaunchRequest".equals(requestType)) {
			return alexaResponse(novaService.welcome(), false);
		}

		if ("IntentRequest".equals(requestType)) {
			String intentName = requestBody.path("request").path("intent").path("name").asText();
			log.info("Alexa intent received: name={}", intentName);

			if ("ChatIntent".equals(intentName)) {
				String message = requestBody.path("request")
					.path("intent")
					.path("slots")
					.path("mensagem")
					.path("value")
					.asText("");
				log.info("ChatIntent slot mensagem={}", message);

				return alexaResponse(novaService.replyTo(message), false);
			}
		}

		if ("SessionEndedRequest".equals(requestType)) {
			return alexaResponse("", true);
		}

		return alexaResponse(novaService.fallback(), false);
	}

	private Map<String, Object> alexaResponse(String text, boolean shouldEndSession) {
		return Map.of(
			"version", "1.0",
			"sessionAttributes", Map.of(),
			"response", Map.of(
				"outputSpeech", Map.of(
					"type", "PlainText",
					"text", text
				),
				"reprompt", Map.of(
					"outputSpeech", Map.of(
						"type", "PlainText",
						"text", "Pode me fazer uma pergunta."
					)
				),
				"shouldEndSession", shouldEndSession
			)
		);
	}
}
