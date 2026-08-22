package com.nova.nova_assistant;

import com.nova.nova_assistant.agent.AgentRouter;
import org.springframework.stereotype.Service;

@Service
public class NovaService {

	private final AgentRouter agentRouter;

	public NovaService(AgentRouter agentRouter) {
		this.agentRouter = agentRouter;
	}

	public String welcome() {
		return "Ola, eu sou a NOVA. Pode me fazer uma pergunta.";
	}

	public String replyTo(String message) {
		if (message == null || message.isBlank()) {
			return "Nao consegui entender sua pergunta. Pode repetir?";
		}

		return agentRouter.route(message).respond(message);
	}

	public String help() {
		return "Voce pode me fazer uma pergunta dizendo, pergunte, seguido do assunto.";
	}

	public String goodbye() {
		return "Ate logo.";
	}

	public String fallback() {
		return "Ainda estou aprendendo a lidar com esse tipo de pedido.";
	}
}
