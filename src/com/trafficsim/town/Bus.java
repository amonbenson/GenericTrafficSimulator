package com.trafficsim.town;

import java.util.ArrayList;

public class Bus extends Entity {

	private int maxPersons; //maximale Anzahl der Personen des Busses
	private double speedX, speedY; //Geschwindigkeit des Busses
	private ArrayList<Person> persons; //Liste der momentanen Personen im Bus
	private ArrayList<Waypoint> waypoints; //Wegpunkte des Busses, sortiert nach Reihenfolge ( das 1. Element wird als erstes angesteuert ) 
	
	private boolean nextWaypointSmaller;
	
	public double getSpeedX() {
		return speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}

	public ArrayList<Waypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(ArrayList<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	public Bus(double x, double y, int maxPersons) {
		super(x, y);
		
		this.maxPersons = maxPersons;
		this.persons = new ArrayList<Person>(maxPersons);
		this.waypoints = new ArrayList<Waypoint>();
		
	}

	@Override
	public void update() {
		calcSpeed();
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
		if (waypoints.size() != 0) {
			if (isNextWaypointReached()) {
				x = waypoints.get(0).getX();
				y = waypoints.get(0).getY();
				waypoints.remove(0);
				calcSpeed();
			}
		}
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde ( Vergleich mithilfe genauer Koordinaten )
	 */
	private boolean isNextWaypointReached() {
		if (nextWaypointSmaller) {
			return (waypoints.get(0).getX() >= getX() && waypoints.get(0).getY() >= getY());
		} else { //next Waypoint bigger
			return (waypoints.get(0).getX() <= getX() && waypoints.get(0).getY() <= getY());			
		}
	}
	
	/**
	 * Setzt die Geschwindigkeit anhand der Richtung des nächsten Wegpunktes.
	 */
	private void calcSpeed() {
		speedX = 0;
		speedY = 0;
		if (waypoints.size() != 0) {
			if (x == waypoints.get(0).getX()) { //Bewegung auf der Y-Achse
				if (y < waypoints.get(0).getY()) { //Bewegung nach oben
					speedY=getDefaultSpeed();
					nextWaypointSmaller = true;
				} else { //Bewegung nach unten
					speedY=-getDefaultSpeed();
					nextWaypointSmaller = false;
				}
			} else { //Bewegung auf der X-Achse
				if (x < waypoints.get(0).getX()) { //Bewegung nach rechts
					speedX=getDefaultSpeed();
					nextWaypointSmaller = false;
				} else { //Bewegung nach links
					speedX=-getDefaultSpeed();
					nextWaypointSmaller = true;
				}
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
		return 0.1d;
	}

}
