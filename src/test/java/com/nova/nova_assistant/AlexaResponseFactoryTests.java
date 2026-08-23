package com.nova.nova_assistant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class AlexaResponseFactoryTests {

	private final AlexaResponseFactory factory = new AlexaResponseFactory();

	@Test
	void buildsOpenSpokenResponse() {
		Map<String, Object> response = factory.say("Hello");

		assertThat(response).containsEntry("version", "1.0");
		assertThat(response).extractingByKey("response")
			.asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
			.containsEntry("shouldEndSession", false);
	}

	@Test
	void buildsEndedSpokenResponse() {
		Map<String, Object> response = factory.end("Bye");

		assertThat(response).extractingByKey("response")
			.asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
			.containsEntry("shouldEndSession", true);
	}

	@Test
	void buildsEmptyEndedSessionResponse() {
		Map<String, Object> response = factory.emptyEndedSession();

		assertThat(response).containsEntry("version", "1.0");
		assertThat(response).extractingByKey("response")
			.asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
			.containsEntry("shouldEndSession", true);
	}
}
