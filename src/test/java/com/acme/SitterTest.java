package com.acme;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.fail;

public class SitterTest {

	// The babysitter
	// - starts no earlier than 5:00PM
	// - leaves no later than 4:00AM
	// - gets paid $12/hour from start-time to bedtime
	// - gets paid $8/hour from bedtime to midnight
	// - gets paid $16/hour from midnight to end of job
	// - gets paid for full hours (no fractional hours)
	//
	//
	// Feature:
	// As a babysitter
	// In order to get paid for 1 night of work
	// I want to calculate my nightly charge
	Sitter sitter;

	@Before
	public void setUp() {
		sitter = new Sitter();
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenCalculateChargeIntervalEndsAfterLatestTimeThrowException() {
		final DateTime startTime = new DateTime(2015, 1, 1, 18, 0, 0, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 2, 4, 1, 1, DateTimeZone.forID("US/Eastern"));

		sitter.calculateCharge(new Interval(startTime, endTime));

		fail("Should have seen IllegalArgumentException");
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenCalculateChargeIntervalStartsBeforeEarilestTimeThrowException() {
		final DateTime startTime = new DateTime(2015, 1, 1, 16, 59, 59, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, 18, 00, 00, DateTimeZone.forID("US/Eastern"));

		sitter.calculateCharge(new Interval(startTime, endTime));

		fail("Should have seen IllegalArgumentException");
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenCalculateChargeIsPassedNullIntervalThrowException() {

		sitter.calculateCharge(null);

		fail("Should have seen IllegalArgumentException");
	}

	@Test
	public void whenCalculateIsPassedIntervalOfPartialHourAfterBedtimeReturns800() {
		final DateTime startTime = new DateTime(2015, 1, 1, Sitter.BEDTIME_HOUR, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, Sitter.BEDTIME_HOUR, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(800));
	}

	@Test
	public void whenCalculateIsPassedIntervalOfPartialHourAfterMidnightReturns1600() {
		final DateTime startTime = new DateTime(2015, 1, 1, 12, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, 12, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(1600));
	}

	@Test
	public void whenCalculateIsPassedIntervalOfPartialHourBeforeBedtimeReturns1200() {
		final DateTime startTime = new DateTime(2015, 1, 1, 17, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, 17, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(1200));
	}

	@Test
	public void whenCalculateIsPassedIntervalSpanningBedtimeBoundryReturns2000() {
		final DateTime startTime = new DateTime(2015, 1, 1, Sitter.BEDTIME_HOUR - 1, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, Sitter.BEDTIME_HOUR, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(2000));
	}

	@Test
	public void whenCalculateIsPassedIntervalSpanningDaylightSavingsFallReturns4800() {
		final DateTime startTime = new DateTime(2015, 11, 1, 2, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 11, 1, 4, 0, 0, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(4800));
	}

	@Test
	public void whenCalculateIsPassedIntervalSpanningDaylightSavingsSpringReturns1600() {
		final DateTime startTime = new DateTime(2015, 3, 8, 2, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 3, 8, 4, 0, 0, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(1600));
	}

	@Test
	public void whenCalculateIsPassedIntervalSpanningMidnightBoundryReturns2400() {
		final DateTime startTime = new DateTime(2015, 1, 1, 11, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 2, 12, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(2000));
	}

	@Test
	public void whenCalculateIsPassedIntervalSpanningTwoPartialHourBeforeBedtimeReturns2400() {
		final DateTime startTime = new DateTime(2015, 1, 1, 17, 10, 00, DateTimeZone.forID("US/Eastern"));
		final DateTime endTime = new DateTime(2015, 1, 1, 18, 45, 59, DateTimeZone.forID("US/Eastern"));

		final Integer charge = sitter.calculateCharge(new Interval(startTime, endTime));

		assertThat(charge, equalTo(2400));
	}

}
