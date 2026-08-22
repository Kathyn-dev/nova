package com.nova.nova_assistant.agent;

public interface NovaAgent {

	boolean supports(String message);

	String respond(String message);
}
