package com.trafficsim.town;

import java.util.ArrayList;

public class Bus extends Entity {

	private int maxPersons; //maximale Anzahl der Personen des Busses
	private double speedX, speedY; //Geschwindigkeit des Busses
	private ArrayList<Person> persons; //Liste der momentanen Personen im Bus
	private ArrayList<Waypoint> waypoints; //Wegpunkte des Busses, sortiert nach Reihenfolge ( das 1. Element wird als erstes angesteuert ) 
	private int currentWaypoint; //gibt an, welcher Wegpunkt gerade als nächstes angefahren werden soll
	
	private boolean nextWaypointSmaller;
	


	public Bus(double x, double y, int maxPersons) {
		this(x, y, maxPersons, new ArrayList<Person>(maxPersons), new ArrayList<Waypoint>());
	}

	public Bus(double x, double y, int maxPersons, ArrayList<Waypoint> waypoints) {
		this(x, y, maxPersons, new ArrayList<Person>(maxPersons), waypoints);
	}
	
	public Bus(double x, double y, int maxPersons, ArrayList<Person> persons, ArrayList<Waypoint> waypoints) {
		super(x, y);
		this.maxPersons = maxPersons;
		this.persons = persons;
		this.waypoints = waypoints;
		this.currentWaypoint = 0;
		
		if (waypoints.size() != 0) checkIfWaypointIsReached(); //Prüft, ob der Bus bereits das nächste Ziel erreicht hat
	}
	
	
	

	public void init() {
		calcSpeed();
		checkIfWaypointIsReached();
	}
	

	public void update(Town t) {

		//Fahre zum nächsten Wegpunkt:
		setX(getX()+speedX);
		setY(getY()+speedY);
		checkIfWaypointIsReached(t);
	}
	
	//TODO revert implementieren
	public void revert(Town t) {
		checkIfWaypointIsReached(t);
		
		setX(getX()-speedX);
		setY(getY()-speedY);
	}
	
	/**
	 * @see #checkIfWaypointIsReached(Town) mit Parameter <code>null</code>
	 */
	private void checkIfWaypointIsReached() {
		checkIfWaypointIsReached(null);
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde. Ist dies der Fall, wird der Index <code>currentWaypoint</code> weitergesetzt.
	 * Außerdem wird die Geschwindigkeit neu berechnet (damit auch die Richtung)
	 * 
	 * @param t gibt den Stadtkontext an. Kann auch <code>null</code> sein, dann kann der Bus aber keine Personen einladen
	 */
	private void checkIfWaypointIsReached(Town t) {
		
		if (isNextWaypointReached()) {
			//Genaue Position setzen:
			setX(waypoints.get(currentWaypoint).getX());
			setY(waypoints.get(currentWaypoint).getY());
			
			//Halte den Wegpunkt immer im richtigen Bereich: (Rotationsprinzip)
			currentWaypoint++;
			if (currentWaypoint>=waypoints.size()) {
				currentWaypoint = 0;
			}
			
			if (t != null) { //Stadtkontext -> Prüfe, ob auf Station gelandet ist
				
				//Es wird immer davon ausgegangen, dass die aktuelle Koordinate eine Straße ist:
				StreetTile st = (StreetTile) t.getTiles()[(int)getX()][(int)getY()];
				if ( st.isStation() ) {
					persons = new ArrayList<Person>(st.getPersons().subList(0, 
							Math.min(maxPersons, st.getPersons().size()))); //Personen setzen
					//Diese Personen aus Station löschen:
					for (int i=0;i<persons.size();i++) {
						st.getPersons().remove(0);
					}
				}
			}
			
			
			calcSpeed();
		}
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde ( Vergleich mithilfe genauer Koordinaten )
	 */
	private boolean isNextWaypointReached() {
		if (nextWaypointSmaller) {
			return (waypoints.get(currentWaypoint).getX() >= getX() && waypoints.get(currentWaypoint).getY() >= getY());
		} else { //next Waypoint bigger
			return (waypoints.get(currentWaypoint).getX() <= getX() && waypoints.get(currentWaypoint).getY() <= getY());			
		}
	}
	
	/**
	 * Setzt die Geschwindigkeit anhand der Richtung des nächsten Wegpunktes.
	 */
	private void calcSpeed() {
		speedX = 0;
		speedY = 0;
		if (waypoints.size() != 0) {
			if (getX() == waypoints.get(currentWaypoint).getX()) { //Bewegung auf der Y-Achse
				if (getY() < waypoints.get(currentWaypoint).getY()) { //Bewegung nach oben
					speedY=getDefaultSpeed();
					nextWaypointSmaller = false;
				} else { //Bewegung nach unten
					speedY=-getDefaultSpeed();
					nextWaypointSmaller = true;
				}
			} else { //Bewegung auf der X-Achse
				if (getX() < waypoints.get(currentWaypoint).getX()) { //Bewegung nach rechts
					speedX=getDefaultSpeed();
					nextWaypointSmaller = false;
				} else { //Bewegung nach links
					speedX=-getDefaultSpeed();
					nextWaypointSmaller = true;
				}
			}
		}
	}
	
	public double getSpeedX() {
		return speedX;
	}

	public double getSpeedY() {
		return speedY;
	}
	
	public ArrayList<Waypoint> getWaypoints() {
		return waypoints;
	}
	
	public ArrayList<Person> getPersons() {
		return persons;
	}
	
	public int getMaxPersons() {
		return maxPersons;
	}
	
	
	
	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}

	public void setWaypoints(ArrayList<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}
	
	public void setPersons(ArrayList<Person> persons) {
		this.persons = persons;
	}
	
	public void setMaxPersons(int maxPersons) {
		this.maxPersons = maxPersons;
	}
	
	
	
	public static double getDefaultSpeed() {
		return 0.3d;
	}



}
