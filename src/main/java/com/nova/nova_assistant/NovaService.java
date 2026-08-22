package com.nova.nova_assistant;

import org.springframework.stereotype.Service;

@Service
public class NovaService {

	public String welcome() {
		return "Olá, eu sou a NOVA. Pode me fazer uma pergunta.";
	}

	public String replyTo(String message) {
		if (message == null || message.isBlank()) {
			return "Não consegui entender sua pergunta. Pode repetir?";
		}

		return "Você perguntou: " + message + ". Em breve vou responder usando inteligência artificial.";
	}

	public String fallback() {
		return "Ainda estou aprendendo a lidar com esse tipo de pedido.";
	}
}
