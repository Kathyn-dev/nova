package com.nova.nova_assistant.agent;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AgentRouter {

	private final List<NovaAgent> agents;

	public AgentRouter(List<NovaAgent> agents) {
		this.agents = agents;
	}

	public NovaAgent route(String message) {
		// Spring injects agents in @Order priority, allowing specialized agents to win over DefaultAgent.
		return agents.stream()
			.filter(agent -> agent.supports(message))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No agent available to handle message"));
	}
}
