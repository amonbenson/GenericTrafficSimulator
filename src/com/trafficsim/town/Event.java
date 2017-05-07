package com.trafficsim.town;


/**
 * Repräsentiert ein abstraktes Event, welches einen Startzeitpunkt hat.
 *
 */
public abstract class Event {
	private long startTime;

	public Event(long startTime) {
		this.startTime = startTime;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	@Override
	public String toString() {
		return String.valueOf(startTime);
	}
}
