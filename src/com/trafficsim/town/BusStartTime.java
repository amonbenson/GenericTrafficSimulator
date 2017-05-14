package com.trafficsim.town;

/**
 * Repräsentiert den Zeitpunkt und Richtung, ab welcher ein Bus losfahren soll.
 *
 */
public class BusStartTime {
	private long startTime; //Zeitpunkt des Starts
	private BusDirection direction; //Richtung des Busses
	
	public BusStartTime(long startTime, BusDirection direction) {
		this.startTime = startTime;
		this.direction = direction;
	}

	public long getStartTime() {
		return startTime;
	}

	public BusDirection getDirection() {
		return direction;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public void setDirection(BusDirection direction) {
		this.direction = direction;
	}
	
	@Override
	public String toString() {
		return "StartTime: "+startTime+"\n"+
				"Direction: "+direction+"\n";
	}

	
}
