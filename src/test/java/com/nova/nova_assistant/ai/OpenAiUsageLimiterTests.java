package com.nova.nova_assistant.ai;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class OpenAiUsageLimiterTests {

	@Test
	void blocksRequestsAfterConfiguredLimit() {
		MutableClock clock = new MutableClock();
		OpenAiUsageLimiter limiter = new OpenAiUsageLimiter(2, Duration.ofMinutes(10), clock);

		limiter.checkAndConsume();
		limiter.checkAndConsume();

		assertThatThrownBy(limiter::checkAndConsume)
			.isInstanceOf(OpenAiUsageLimitException.class)
			.hasMessage("OpenAI request limit reached for current window");
	}

	@Test
	void resetsLimitAfterWindowPasses() {
		MutableClock clock = new MutableClock();
		OpenAiUsageLimiter limiter = new OpenAiUsageLimiter(1, Duration.ofMinutes(10), clock);

		limiter.checkAndConsume();
		clock.advance(Duration.ofMinutes(10));

		limiter.checkAndConsume();
	}

	private static class MutableClock extends Clock {

		private Instant instant = Instant.parse("2026-08-22T00:00:00Z");

		@Override
		public ZoneId getZone() {
			return ZoneId.of("UTC");
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return instant;
		}

		void advance(Duration duration) {
			instant = instant.plus(duration);
		}
	}
}
