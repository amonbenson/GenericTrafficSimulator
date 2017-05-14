package com.trafficsim.town;

/**
 * Repräsentiert eine spezifische Buslinie, d.h. eine Buslinie mit Richtungsangabe.
 * @author Luca
 *
 */
public class SpecificSchedule {
	private Schedule schedule;
	private BusDirection direction;
	
	public SpecificSchedule(Schedule schedule, BusDirection direction) {
		if (schedule == null) throw new NullPointerException("Schedule can't be null.");
		if (direction == null) throw new NullPointerException("Direction can't be null.");
		this.schedule = schedule;
		this.direction = direction;
	}
	
	public Schedule getSchedule() {
		return schedule;
	}
	public BusDirection getDirection() {
		return direction;
	}
	
	public void setSchedule(Schedule schedule) {
		if (schedule == null) throw new NullPointerException("Schedule can't be null.");
		this.schedule = schedule;
	}
	public void setDirection(BusDirection direction) {
		if (direction == null) throw new NullPointerException("Direction can't be null.");
		this.direction = direction;
	}
	
	public boolean isReverse() {
		return !isNormal();
	}
	public boolean isNormal() {
		return (direction == BusDirection.NORMAL)?true:false;
	}
	@Override
	public String toString() {
		return "Schedule: "+schedule+"\n"+
				"Direction: "+direction+"\n";
	}
}
