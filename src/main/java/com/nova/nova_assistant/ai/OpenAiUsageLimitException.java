package com.nova.nova_assistant.ai;

public class OpenAiUsageLimitException extends RuntimeException {

	public OpenAiUsageLimitException(String message) {
		super(message);
	}
}
