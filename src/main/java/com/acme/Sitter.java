package com.acme;

import org.joda.time.Duration;
import org.joda.time.Interval;

public class Sitter {

	public static final int BEDTIME_HOUR = 20;

	/**
	 * Calculates the charge for the interval in cents. If the interval does not fall into the legal bounds then an exception is thrown.
	 *
	 * @param interval
	 * @return charge for the interval in cents
	 *
	 */
	public Integer calculateCharge(final Interval interval) throws IllegalArgumentException {
		validateInterval(interval);
		return null;
	}

	private void validateInterval(final Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException("An interval is required");
		}
		if (interval.getStart().getHourOfDay() < 17) {
			throw new IllegalArgumentException("Start Time must be after 5PM");
		}
		if (new Duration(interval.getEnd().getMillisOfDay()).isLongerThan(Duration.standardHours(4L))) {
			throw new IllegalArgumentException("End Time must be before 4AM");
		}
	}
}
