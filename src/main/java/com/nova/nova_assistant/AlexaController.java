package com.nova.nova_assistant;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	private final AlexaResponseFactory alexaResponseFactory;
	private final String expectedApplicationId;

	public AlexaController(
		NovaService novaService,
		AlexaResponseFactory alexaResponseFactory,
		@Value("${nova.alexa.application-id:}") String expectedApplicationId
	) {
		this.novaService = novaService;
		this.alexaResponseFactory = alexaResponseFactory;
		this.expectedApplicationId = expectedApplicationId;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> handleAlexaRequest(@RequestBody JsonNode requestBody) {
		if (!isExpectedSkill(requestBody)) {
			log.warn("Rejected Alexa request with unexpected application id");
			return ResponseEntity.status(403).body(alexaResponseFactory.end("Requisicao nao autorizada."));
		}

		String requestType = requestBody.path("request").path("type").asText();
		log.info("Alexa request received: type={}", requestType);

		if ("LaunchRequest".equals(requestType)) {
			return ResponseEntity.ok(alexaResponseFactory.say(novaService.welcome()));
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
				log.info("ChatIntent received: hasMessage={}", message != null && !message.isBlank());

				return ResponseEntity.ok(alexaResponseFactory.say(novaService.replyTo(message)));
			}

			if ("AMAZON.HelpIntent".equals(intentName)) {
				return ResponseEntity.ok(alexaResponseFactory.say(novaService.help()));
			}

			if ("AMAZON.CancelIntent".equals(intentName) || "AMAZON.StopIntent".equals(intentName)) {
				return ResponseEntity.ok(alexaResponseFactory.end(novaService.goodbye()));
			}
		}

		if ("SessionEndedRequest".equals(requestType)) {
			return ResponseEntity.ok(alexaResponseFactory.emptyEndedSession());
		}

		return ResponseEntity.ok(alexaResponseFactory.say(novaService.fallback()));
	}

	private boolean isExpectedSkill(JsonNode requestBody) {
		if (expectedApplicationId == null || expectedApplicationId.isBlank()) {
			return true;
		}

		String sessionApplicationId = requestBody.path("session")
			.path("application")
			.path("applicationId")
			.asText("");
		String contextApplicationId = requestBody.path("context")
			.path("System")
			.path("application")
			.path("applicationId")
			.asText("");

		return expectedApplicationId.equals(sessionApplicationId) || expectedApplicationId.equals(contextApplicationId);
	}
}
