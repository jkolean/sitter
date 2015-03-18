package com.acme;

import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;

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
		int chargeTotal = 0;
		chargeTotal += calculateChargeToMidnight(interval);
		chargeTotal += calculateChargeAfterMidnight(interval);

		return chargeTotal;
	}

	private int calculateChargeAfterMidnight(final Interval interval) {

		final MutableDateTime mutableStartDateTime = new MutableDateTime(interval.getStart());

		if (mutableStartDateTime.getHourOfDay() > 17) {
			mutableStartDateTime.addHours(23 - mutableStartDateTime.getHourOfDay());
		}
		final MutableDateTime mutableEndDateTime = new MutableDateTime(interval.getEnd());
		if (interval.getEnd().getMinuteOfHour() > 0 || interval.getEnd().getSecondOfMinute() > 0) {
			mutableEndDateTime.addHours(1);
		}
		if (mutableEndDateTime.getHourOfDay() > 4 && mutableEndDateTime.getHourOfDay() < 23) {
			// there are no hours after midnight
			return 0;
		}

		final int hours = Hours.hoursBetween(mutableStartDateTime, mutableEndDateTime).getHours();
		return hours * 1600;
	}

	private int calculateChargeToMidnight(final Interval interval) {
		final int startHour = interval.getStart().getHourOfDay();
		if (startHour < 15) {
			// we started aftermidnight
			return 0;
		}
		int endHour = interval.getEnd().getHourOfDay();
		if (interval.getEnd().getMinuteOfHour() > 0 || interval.getEnd().getSecondOfMinute() > 0) {
			endHour++;
		}
		if (endHour <= 4 || endHour > 23) {
			// we ended after midnight
			endHour = 23;
		}
		int chargeTotal = 0;
		for (int currentHour = startHour; currentHour < endHour; currentHour++) {
			if (currentHour < BEDTIME_HOUR) {
				chargeTotal += 1200;
			} else {
				chargeTotal += 800;
			}
		}
		return chargeTotal;
	}

	private void validateInterval(final Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException("An interval is required");
		}
		if (interval.toDuration().isLongerThan(Duration.standardHours(13))) {
			throw new IllegalArgumentException("An interval must be less than one day");
		}
		if (interval.getStart().getHourOfDay() > 4 && interval.getStart().getHourOfDay() < 17) {
			throw new IllegalArgumentException("Start Time must be after 5PM");
		}
		if (interval.getEnd().getHourOfDay() > 4 && interval.getEnd().getHourOfDay() < 17) {
			throw new IllegalArgumentException("End Time must be before 4AM");
		}
		if (interval.getEnd().getHourOfDay() == 4) {
			if (interval.getEnd().getMinuteOfHour() > 0 || interval.getEnd().getSecondOfMinute() > 0) {
				throw new IllegalArgumentException("End Time must be before 4AM");
			}
		}
	}
}
