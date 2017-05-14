package com.trafficsim.town;

/**
 * Repräsentiert einen Umsteigeort. Die Station ist durch den Waypoint dargestellt,
 * die Linie, mit welcher die Person fahren soll, durch den SpecificSchedule.
 * @author Luca
 *
 */
public class ChangeStation {
	private Waypoint station;
	private SpecificSchedule schedule;
	
	public ChangeStation(Waypoint station, SpecificSchedule schedule) {
		if (station == null) throw new NullPointerException("Station can't be null.");
		if (schedule == null) throw new NullPointerException("Schedule can't be null.");
		this.station = station;
		this.schedule = schedule;
	}
	
	public Waypoint getStation() {
		return station;
	}
	public SpecificSchedule getSchedule() {
		return schedule;
	}
	
	public void setStation(Waypoint station) {
		if (station == null) throw new NullPointerException("Station can't be null.");
		this.station = station;
	}
	public void setSchedule(SpecificSchedule schedule) {
		if (schedule == null) throw new NullPointerException("Schedule can't be null.");
		this.schedule = schedule;
	}
}
