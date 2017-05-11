package com.trafficsim.generic;

import java.awt.Point;
import java.util.ArrayList;

public class Chromosom {
	private ArrayList<Schedule> schedules;
	private ArrayList<Point> stations;
	
	public Chromosom() {
		
	}
	
	public ArrayList<Schedule> getSchedules() {
		return schedules;
	}
	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules;
	}
	public ArrayList<Point> getStations() {
		return stations;
	}
	public void setStations(ArrayList<Point> stations) {
		this.stations = stations;
	}
	
}
