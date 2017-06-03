package com.trafficsim.town;

import java.util.ArrayList;

/**
 * Repräsentiert ein Straßenstück.
 * Dieses kann auch eine Station sein, das ist abhängig von der gewählten Simulation.
 * Auch wenn es nicht OOP ist, dass eine Straße auch eine Station sein kann, ist dies hier sinnvoller,
 * da keine neuen Bushaltestellenobjekte erzeugt werden müssen und es keine Pointerfehler geben wird.
 * 
 */
public class StreetTile extends Tile {
	
	private double maxSpeed;
	
	private boolean isStation;
	private ArrayList<Person> waitingPersons;
	private ArrayList<Schedule> schedules; //gibt die hier fahrenden Linien an, wie z.B. 187 und 282
	


	public StreetTile(int x, int y, double maxSpeed) {
		super(x, y);
		this.maxSpeed = maxSpeed;
		this.isStation = false;
		this.waitingPersons = new ArrayList<Person>();
		schedules = new ArrayList<Schedule>();
		setToStreet();
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	/**
	 * Setzt dieses Objekt zum Typ Bushaltestelle, hier muss die maximale Anzahl an wartenden Person
	 * @param maxPersons
	 */
	public void setToStation() {
		isStation = true;
		waitingPersons = new ArrayList<Person>();
		schedules = new ArrayList<Schedule>();
	}

	/**
	 * Setzt dieses Objekt zum Typ Straße.
	 */
	public void setToStreet() {
		if (isStation) {
			isStation = false;
			waitingPersons.clear();
			schedules.clear();
		}
	}
	
	/**
	 * Gibt an, ob die Straße eine Bushaltestelle ist.
	 */
	public boolean isStation() {
		return isStation;
	}
	
	/**
	 * Fügt die Person <code>p</code> zu den wartenden Personen hinzu.
	 */
	public void addPerson(Person p) {
		waitingPersons.add(p);
	}
	
	public ArrayList<Person> getPersons() {
		return waitingPersons;
	}
	
	public void setPersons(ArrayList<Person> persons) {
		this.waitingPersons = persons;
	}
	
	
	public ArrayList<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules;
	}
	
	public void addSchedule(Schedule schedule) {
		this.schedules.add(schedule);
	}
	
	public boolean hasSchedule(Schedule s) {
		return schedules.contains(s);
	}
	
}
