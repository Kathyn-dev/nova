package com.nova.nova_assistant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.nova.nova_assistant.agent.AgentRouter;
import com.nova.nova_assistant.agent.DefaultAgent;
import org.junit.jupiter.api.Test;

class NovaServiceTests {

	private final NovaService novaService = new NovaService(new AgentRouter(List.of(new DefaultAgent())));

	@Test
	void repliesUsingDefaultAgent() {
		String response = novaService.replyTo("como funciona a lua");

		assertThat(response).contains("Voce perguntou: como funciona a lua");
	}

	@Test
	void asksUserToRepeatWhenMessageIsBlank() {
		String response = novaService.replyTo(" ");

		assertThat(response).isEqualTo("Nao consegui entender sua pergunta. Pode repetir?");
	}
}
