package com.trafficsim.town;

import java.util.ArrayList;

public class Bus extends Entity {

	private int maxPersons; //maximale Anzahl der Personen des Busses
	private double speedX, speedY; //Geschwindigkeit des Busses
	private ArrayList<Person> persons; //Liste der momentanen Personen im Bus
	private ArrayList<Waypoint> waypoints; //Wegpunkte des Busses, sortiert nach Reihenfolge ( das 1. Element wird als erstes angesteuert ) 
	
	
	public Bus(double x, double y, int maxPersons) {
		super(x, y);
		
		this.maxPersons = maxPersons;
		this.persons = new ArrayList<Person>(maxPersons);
		this.waypoints = null;
	}

	@Override
	public void update() {
		checkIfWaypointIsReached();
		//Fahre zum nächsten Wegpunkt:
		x+=speedX;
		y+=speedY;
		checkIfWaypointIsReached();
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde und entfernt diesen dann aus der Liste der Wegpunkte.
	 * Außerdem wird die Geschwindigkeit neu berechnet.
	 */
	private void checkIfWaypointIsReached() {
		if (isNextWaypointReached()) {
			waypoints.remove(0);
			calcSpeed();
		}
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde ( Vergleich mithilfe genauer Koordinaten )
	 */
	private boolean isNextWaypointReached() {
		return (waypoints.get(0).getX() == getX() && waypoints.get(0).getY() == getY());
	}
	
	/**
	 * Setzt die Geschwindigkeit anhand der Richtung des nächsten Wegpunktes.
	 */
	private void calcSpeed() {
		speedX = 0;
		speedY = 0;
		if (x == waypoints.get(0).getX()) { //Bewegung auf der Y-Achse
			if (y < waypoints.get(0).getY()) { //Bewegung nach unten
				speedY=-getDefaultSpeed();
			} else { //Bewegung nach oben
				speedY=getDefaultSpeed();
			}
		} else { //Bewegung auf der X-Achse
			if (x < waypoints.get(0).getX()) { //Bewegung nach rechts
				speedX=getDefaultSpeed();
			} else { //Bewegung nach links
				speedX=-getDefaultSpeed();
			}
		}
	}
	
	public int getMaxPersons() {
		return maxPersons;
	}

	public void setMaxPersons(int maxPersons) {
		this.maxPersons = maxPersons;
	}

	public ArrayList<Person> getPersons() {
		return persons;
	}

	public void setPersons(ArrayList<Person> persons) {
		this.persons = persons;
	}
	
	public static double getDefaultSpeed() {
		return 0.5;
	}

}
