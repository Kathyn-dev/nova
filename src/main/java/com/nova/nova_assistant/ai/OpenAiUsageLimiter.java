package com.nova.nova_assistant.ai;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenAiUsageLimiter {

	private final int maxRequests;
	private final Duration window;
	private final Clock clock;
	private final AtomicInteger requestCount = new AtomicInteger();
	private Instant windowStartedAt;

	@Autowired
	public OpenAiUsageLimiter(
		@Value("${nova.openai.rate-limit.requests:20}") int maxRequests,
		@Value("${nova.openai.rate-limit.window-minutes:60}") long windowMinutes
	) {
		this(maxRequests, Duration.ofMinutes(windowMinutes), Clock.systemUTC());
	}

	OpenAiUsageLimiter(int maxRequests, Duration window, Clock clock) {
		this.maxRequests = maxRequests;
		this.window = window;
		this.clock = clock;
		this.windowStartedAt = clock.instant();
	}

	public synchronized void checkAndConsume() {
		Instant now = clock.instant();
		if (Duration.between(windowStartedAt, now).compareTo(window) >= 0) {
			windowStartedAt = now;
			requestCount.set(0);
		}

		// This in-memory guard limits accidental spend; account-level API budgets remain the hard limit.
		if (requestCount.incrementAndGet() > maxRequests) {
			requestCount.decrementAndGet();
			throw new OpenAiUsageLimitException("OpenAI request limit reached for current window");
		}
	}
}
