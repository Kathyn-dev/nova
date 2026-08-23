package com.nova.nova_assistant.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class AgentRouterTests {

	@Test
	void returnsFirstSupportingAgent() {
		NovaAgent first = agent(false, "first");
		NovaAgent second = agent(true, "second");
		NovaAgent third = agent(true, "third");

		NovaAgent selected = new AgentRouter(List.of(first, second, third)).route("hello");

		assertThat(selected.respond("hello")).isEqualTo("second");
	}

	@Test
	void throwsWhenNoAgentSupportsMessage() {
		AgentRouter router = new AgentRouter(List.of(agent(false, "unused")));

		assertThatThrownBy(() -> router.route("hello"))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("No agent available to handle message");
	}

	private NovaAgent agent(boolean supports, String response) {
		return new NovaAgent() {
			@Override
			public boolean supports(String message) {
				return supports;
			}

			@Override
			public String respond(String message) {
				return response;
			}
		};
	}
}
