package com.trafficsim.town;

import java.util.ArrayList;

public class Bus extends Entity {

	private int maxPersons; //maximale Anzahl der Personen des Busses
	private double speedX, speedY; //Geschwindigkeit des Busses
	private ArrayList<Person> persons; //Liste der momentanen Personen im Bus
	private SpecificSchedule schedule; //ein Bus hat immer eine Buslinie mit Richtungsangabe
	private int currentWaypoint; //gibt an, welcher Wegpunkt gerade als n�chstes angefahren werden soll
	private Town town; //Town-Kontext
	
	private boolean nextWaypointSmaller;
	

	public Bus(double x, double y, SpecificSchedule schedule, Town town) {
		this(x, y, getDefaultMaxPersons(), new ArrayList<Person>(getDefaultMaxPersons()), schedule, town);
	}

	public Bus(double x, double y, int maxPersons, SpecificSchedule schedule, Town town) {
		this(x, y, maxPersons, new ArrayList<Person>(maxPersons), schedule, town);
	}
	
	public Bus(double x, double y, int maxPersons, ArrayList<Person> persons, SpecificSchedule schedule, Town town) {
		super(x, y);
		
		if (schedule == null) throw new NullPointerException("Schedule can't be null.");
		if (town == null) throw new NullPointerException("Town can't be null.");
		
		this.maxPersons = maxPersons;
		this.persons = persons;
		this.schedule = schedule;
		this.town = town;
		this.currentWaypoint = 0;
		
	}
	
	
	

	public void init() {
		calcSpeed();
		checkIfWaypointIsReached();
	}
	

	public void update() {

		//Fahre zum n�chsten Wegpunkt:
		setX(getX()+speedX);
		setY(getY()+speedY);
		checkIfWaypointIsReached();
	}
	
	//TODO revert implementieren
	public void revert() {
		checkIfWaypointIsReached();
		
		setX(getX()-speedX);
		setY(getY()-speedY);
	}
	
	/**
	 * Pr�ft, ob n�chster Wegpunkt erreicht wurde. Ist dies der Fall, wird der Index <code>currentWaypoint</code> weitergesetzt.
	 * Au�erdem wird die Geschwindigkeit neu berechnet (damit auch die Richtung)
	 * 
	 * @param t gibt den Stadtkontext an. Kann auch <code>null</code> sein, dann kann der Bus aber keine Personen einladen
	 */
	private void checkIfWaypointIsReached() {
		if (isNextWaypointReached()) {
			//Genaue Position setzen:
			if (schedule.isNormal()) {
				setX(schedule.getSchedule().getWaypoint(currentWaypoint).getX());
				setY(schedule.getSchedule().getWaypoint(currentWaypoint).getY());
			} else {
				setX(schedule.getSchedule().getWaypointReverse(currentWaypoint).getX());
				setY(schedule.getSchedule().getWaypointReverse(currentWaypoint).getY());				
			}

			
			//Halte den Wegpunkt immer im richtigen Bereich: (Rotationsprinzip)
			currentWaypoint++;
			if (currentWaypoint>=schedule.getSchedule().getWaypointSize()) {
				currentWaypoint = 0;
			}
			

			//Es wird immer davon ausgegangen, dass die aktuelle Koordinate eine Stra�e ist:
			StreetTile st = (StreetTile) town.getTiles()[(int)getX()][(int)getY()];
			if ( st.isStation() ) {
				persons = new ArrayList<Person>(st.getPersons().subList(0, 
						Math.min(maxPersons, st.getPersons().size()))); //Personen setzen
				//Diese Personen aus Station l�schen:
				for (int i=0;i<persons.size();i++) {
					st.getPersons().remove(0);
				}
			}
		
			
			
			calcSpeed();
		}
	}
	
	/**
	 * Pr�ft, ob n�chster Wegpunkt erreicht wurde ( Vergleich mithilfe genauer Koordinaten )
	 */
	private boolean isNextWaypointReached() {
		
		if (schedule.isNormal()) {
			if (nextWaypointSmaller) {
				return (schedule.getSchedule().getWaypoint(currentWaypoint).getX() >= getX() &&
						schedule.getSchedule().getWaypoint(currentWaypoint).getY() >= getY());
			} else {
				return (schedule.getSchedule().getWaypoint(currentWaypoint).getX() <= getX() &&
						schedule.getSchedule().getWaypoint(currentWaypoint).getY() <= getY());				
			}
		} else { //also reverse
			if (nextWaypointSmaller) {
				return (schedule.getSchedule().getWaypointReverse(currentWaypoint).getX() >= getX() &&
						schedule.getSchedule().getWaypointReverse(currentWaypoint).getY() >= getY());
			} else {
				return (schedule.getSchedule().getWaypointReverse(currentWaypoint).getX() <= getX() &&
						schedule.getSchedule().getWaypointReverse(currentWaypoint).getY() <= getY());				
			}
		}
	}
	
	/**
	 * Setzt die Geschwindigkeit anhand der Richtung des n�chsten Wegpunktes.
	 */
	private void calcSpeed() {
		speedX = 0;
		speedY = 0;
		if (schedule.getSchedule().getWaypointSize() != 0) {
			if (schedule.isNormal()) {
				if (getX() == schedule.getSchedule().getWaypoint(currentWaypoint).getX()) { //Bewegung auf der Y-Achse
					if (getY() < schedule.getSchedule().getWaypoint(currentWaypoint).getY()) { //Bewegung nach oben
						speedY=getDefaultSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach unten
						speedY=-getDefaultSpeed();
						nextWaypointSmaller = true;
					}
				} else { //Bewegung auf der X-Achse
					if (getX() < schedule.getSchedule().getWaypoint(currentWaypoint).getX()) { //Bewegung nach rechts
						speedX=getDefaultSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach links
						speedX=-getDefaultSpeed();
						nextWaypointSmaller = true;
					}
				}
			} else { //Reverse Bewegung
				if (getX() == schedule.getSchedule().getWaypointReverse(currentWaypoint).getX()) { //Bewegung auf der Y-Achse
					if (getY() < schedule.getSchedule().getWaypointReverse(currentWaypoint).getY()) { //Bewegung nach oben
						speedY=getDefaultSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach unten
						speedY=-getDefaultSpeed();
						nextWaypointSmaller = true;
					}
				} else { //Bewegung auf der X-Achse
					if (getX() < schedule.getSchedule().getWaypointReverse(currentWaypoint).getX()) { //Bewegung nach rechts
						speedX=getDefaultSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach links
						speedX=-getDefaultSpeed();
						nextWaypointSmaller = true;
					}
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
	public void setPersons(ArrayList<Person> persons) {
		this.persons = persons;
	}
	public void setMaxPersons(int maxPersons) {
		this.maxPersons = maxPersons;
	}
	
	
	
	public static double getDefaultSpeed() {
		return 0.3d;
	}
	
	public static int getDefaultMaxPersons() {
		return 20;
	}



}
