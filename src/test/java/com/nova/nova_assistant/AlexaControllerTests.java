package com.nova.nova_assistant;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.nova.nova_assistant.agent.AgentRouter;
import com.nova.nova_assistant.agent.DefaultAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AlexaControllerTests {

	private static final String APPLICATION_ID = "amzn1.ask.skill.test";

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		NovaService novaService = new NovaService(new AgentRouter(List.of(new DefaultAgent())));
		AlexaController controller = new AlexaController(novaService, new AlexaResponseFactory(), APPLICATION_ID);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void handlesLaunchRequest() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(launchRequest(APPLICATION_ID)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.response.outputSpeech.text").value("Ola, eu sou a NOVA. Pode me fazer uma pergunta."))
			.andExpect(jsonPath("$.response.shouldEndSession").value(false));
	}

	@Test
	void handlesChatIntent() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(chatIntentRequest(APPLICATION_ID, "como funciona a lua")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.response.outputSpeech.text", containsString("Voce perguntou: como funciona a lua")))
			.andExpect(jsonPath("$.response.shouldEndSession").value(false));
	}

	@Test
	void handlesHelpIntent() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(intentRequest(APPLICATION_ID, "AMAZON.HelpIntent")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.response.outputSpeech.text").value("Voce pode me fazer uma pergunta dizendo, pergunte, seguido do assunto."));
	}

	@Test
	void endsSessionOnStopIntent() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(intentRequest(APPLICATION_ID, "AMAZON.StopIntent")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.response.outputSpeech.text").value("Ate logo."))
			.andExpect(jsonPath("$.response.shouldEndSession").value(true));
	}

	@Test
	void rejectsUnexpectedApplicationId() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(launchRequest("amzn1.ask.skill.other")))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.response.outputSpeech.text").value("Requisicao nao autorizada."));
	}

	@Test
	void handlesSessionEndedRequestWithoutSpeech() throws Exception {
		mockMvc.perform(post("/alexa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(sessionEndedRequest(APPLICATION_ID)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.response.shouldEndSession").value(true));
	}

	private String launchRequest(String applicationId) {
		return """
			{
			  "version": "1.0",
			  "session": {
			    "application": { "applicationId": "%s" }
			  },
			  "request": { "type": "LaunchRequest" }
			}
			""".formatted(applicationId);
	}

	private String chatIntentRequest(String applicationId, String message) {
		return """
			{
			  "version": "1.0",
			  "session": {
			    "application": { "applicationId": "%s" }
			  },
			  "request": {
			    "type": "IntentRequest",
			    "intent": {
			      "name": "ChatIntent",
			      "slots": {
			        "mensagem": { "name": "mensagem", "value": "%s" }
			      }
			    }
			  }
			}
			""".formatted(applicationId, message);
	}

	private String intentRequest(String applicationId, String intentName) {
		return """
			{
			  "version": "1.0",
			  "session": {
			    "application": { "applicationId": "%s" }
			  },
			  "request": {
			    "type": "IntentRequest",
			    "intent": { "name": "%s" }
			  }
			}
			""".formatted(applicationId, intentName);
	}

	private String sessionEndedRequest(String applicationId) {
		return """
			{
			  "version": "1.0",
			  "session": {
			    "application": { "applicationId": "%s" }
			  },
			  "request": { "type": "SessionEndedRequest" }
			}
			""".formatted(applicationId);
	}
}
