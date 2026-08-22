package com.nova.nova_assistant;

import org.springframework.stereotype.Service;

@Service
public class NovaService {

	public String welcome() {
		return "Ola, eu sou a NOVA. Pode me fazer uma pergunta.";
	}

	public String replyTo(String message) {
		if (message == null || message.isBlank()) {
			return "Nao consegui entender sua pergunta. Pode repetir?";
		}

		return "Voce perguntou: " + message + ". Em breve vou responder usando inteligencia artificial.";
	}

	public String fallback() {
		return "Ainda estou aprendendo a lidar com esse tipo de pedido.";
	}
}
