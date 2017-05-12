package com.trafficsim.town;

import java.util.ArrayList;

/**
 * Repr�sentiert ein Stra�enst�ck.
 * Dieses kann auch eine Station sein, das ist abh�ngig von der gew�hlten Simulation.
 * Auch wenn es nicht OOP ist, dass eine Stra�e auch eine Station sein kann, ist dies hier sinnvoller,
 * da keine neuen Bushaltestellenobjekte erzeugt werden m�ssen und es keine Pointerfehler geben wird.
 * 
 */
public class StreetTile extends Tile {
	
	private double maxSpeed;
	
	private boolean isStation;
	private ArrayList<Person> waitingPersons;
	
	public StreetTile(int x, int y, double maxSpeed) {
		super(x, y);
		this.maxSpeed = maxSpeed;
		this.isStation = false;
		this.waitingPersons = new ArrayList<Person>();
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
	}

	/**
	 * Setzt dieses Objekt zum Typ Stra�e.
	 */
	public void setToStreet() {
		if (isStation) {
			isStation = false;
			waitingPersons.clear();
		}
	}
	
	/**
	 * Gibt an, ob die Stra�e eine Bushaltestelle ist.
	 */
	public boolean isStation() {
		return isStation;
	}
	

	
	public ArrayList<Person> getPersons() {
		return waitingPersons;
	}
	
	public void setPersons(ArrayList<Person> persons) {
		this.waitingPersons = persons;
	}
	
}
