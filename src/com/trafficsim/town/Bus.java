package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Iterator;

import com.trafficsim.sim.Simulation;

public class Bus extends Entity {

	private int maxPersons; //maximale Anzahl der Personen des Busses
	private double speedX, speedY, rotation; //Geschwindigkeit und Rotation des Busses
	private ArrayList<Person> persons; //Liste der momentanen Personen im Bus
	private SpecificSchedule schedule; //ein Bus hat immer eine Buslinie mit Richtungsangabe
	private int currentWaypoint; //gibt an, welcher Wegpunkt gerade als nächstes angefahren werden soll
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
		this.speedX = 0;
		this.speedY = 0;
		this.rotation = 0;
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

		//Fahre zum nächsten Wegpunkt:
		setX(getX()+speedX);
		setY(getY()+speedY);
		
		// Rotation neu berechnen
		double deltaRotation = Math.atan2(speedY, speedX) - rotation;
		while (deltaRotation < -Math.PI) deltaRotation += 2 * Math.PI;
		while (deltaRotation > Math.PI * 0.9) deltaRotation -= 2 * Math.PI;
		
		// neue Position interpolieren
		double maxRotation = 0.6;
		if (deltaRotation > 0) rotation += Math.min(deltaRotation, maxRotation);
		if (deltaRotation < 0) rotation -= Math.min(-deltaRotation, maxRotation);
		
		checkIfWaypointIsReached();
	}
	
	public void revert() {
		checkIfWaypointIsReached();
		
		setX(getX()-speedX);
		setY(getY()-speedY);
	}
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde. Ist dies der Fall, wird der Index <code>currentWaypoint</code> weitergesetzt.
	 * Außerdem wird die Geschwindigkeit neu berechnet (damit auch die Richtung)
	 * 
	 * @param t gibt den Stadtkontext an. Kann auch <code>null</code> sein, dann kann der Bus aber keine Personen einladen
	 */
	private void checkIfWaypointIsReached() {
		// x y  Position aller Personen updaten
		for (Person person : persons) {
			if (person != null) {
				person.setX(getX());
				person.setY(getY());
			}
		}
		
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
			
			if (currentWaypoint>=schedule.getSchedule().getWaypointSize()) { //Hier wird die Richtung geändert, da die Endhaltestelle erreicht ist
				if (schedule.isNormal()) {
					schedule = schedule.getSchedule().getScheduleReverse();
					//Wenn der Switch dazu resultiert, dass die gleiche Station nochmal angefahren wird, currentWaypoint++ machen
				} else {
					schedule = schedule.getSchedule().getScheduleNormal();						
				}
				currentWaypoint = 1;
			}
			
			
			//Es wird immer davon ausgegangen, dass die aktuelle Koordinate eine Straße ist:
			StreetTile st = (StreetTile) town.getTiles()[(int)getX()][(int)getY()];
			if ( st.isStation() ) {
				
				//Personen umsteigen lassen: (oder aussteigen lassen)
				for (Iterator<Person> iterator = persons.iterator(); iterator.hasNext();) {
				    Person p = iterator.next();
				    if (p.getRoute().getNextStation().getStation().isSame(st.toWaypoint())) { //Umsteigen und ausladen
						p.getRoute().reachedChangeStation();
						//Die Person wird der Station nur hinzugefügt, wenn diese nicht das Ziel ist:
						if (!p.getRoute().isFinished()) {
							st.addPerson(p);
						} else {
							// Andernfalls, wenn die Person ihr Ziel erreicht hat, wird sie auch aus der Stadt entfernt
							p.done(town.getTime());
							town.getPersons().remove(p);
							// TODO: An diesem Punkt müsste man schauen, wie "zufrieden" die Person mit ihrere Reise war,
							// um das aktuelle Chromosom berwerten zu können.
						}
						iterator.remove();
					}
				}
				
				//Personen, die müssen, einsteigen lassen:
				for (Iterator<Person> iterator = st.getPersons().iterator(); iterator.hasNext();) {
					Person p = iterator.next();
					//Wenn die Person genau diesen Bus benötigt, also wenn er den gleichen Namen und die richtige Richtung hat, muss diese einsteigen
					if (p.getRoute().getCurrentStation().getSchedule().isSameDirection(schedule.getDirection()) &&
							p.getRoute().getCurrentStation().getSchedule().getSchedule().hasSameName(schedule.getSchedule().getName())) {
						if (persons.size()<maxPersons) { //wenn noch Personen einsteigen können
							persons.add(p);
							iterator.remove();
						} else { //ansonsten die Schleife beenden, da der Bus voll ist
							break;
						}
					}
				}
			}
			


				calcSpeed();
			} else {
				//Darf sich nicht bewegen
			}
		}
	
	
	/**
	 * Prüft, ob nächster Wegpunkt erreicht wurde ( Vergleich mithilfe genauer Koordinaten )
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
	 * Setzt die Geschwindigkeit anhand der Richtung des nächsten Wegpunktes.
	 */
	private void calcSpeed() {
		speedX = 0;
		speedY = 0;
		if (schedule.getSchedule().getWaypointSize() != 0) {
			if (schedule.isNormal()) {
				if (getX() == schedule.getSchedule().getWaypoint(currentWaypoint).getX()) { //Bewegung auf der Y-Achse
					if (getY() < schedule.getSchedule().getWaypoint(currentWaypoint).getY()) { //Bewegung nach oben
						speedY=getCurrentSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach unten
						speedY=-getCurrentSpeed();
						nextWaypointSmaller = true;
					}
				} else { //Bewegung auf der X-Achse
					if (getX() < schedule.getSchedule().getWaypoint(currentWaypoint).getX()) { //Bewegung nach rechts
						speedX=getCurrentSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach links
						speedX=-getCurrentSpeed();
						nextWaypointSmaller = true;
					}
				}
			} else { //Reverse Bewegung
				if (getX() == schedule.getSchedule().getWaypointReverse(currentWaypoint).getX()) { //Bewegung auf der Y-Achse
					if (getY() < schedule.getSchedule().getWaypointReverse(currentWaypoint).getY()) { //Bewegung nach oben
						speedY=getCurrentSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach unten
						speedY=-getCurrentSpeed();
						nextWaypointSmaller = true;
					}
				} else { //Bewegung auf der X-Achse
					if (getX() < schedule.getSchedule().getWaypointReverse(currentWaypoint).getX()) { //Bewegung nach rechts
						speedX=getCurrentSpeed();
						nextWaypointSmaller = false;
					} else { //Bewegung nach links
						speedX=-getCurrentSpeed();
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
	public double getRotation() {
		return rotation;
	}
	public ArrayList<Person> getPersons() {
		return persons;
	}
	public int getMaxPersons() {
		return maxPersons;
	}
	public SpecificSchedule getSchedule() {
		return schedule;
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
	public void setSchedule(SpecificSchedule schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * Gibt die Geschwindigkeit auf dem Tile zurück, wo der Bus gerade ist
	 */
	public double getCurrentSpeed() {
		Tile t = town.getTiles()[(int)getX()][(int)getY()];
		if (t instanceof StreetTile) {
			return ((StreetTile) t).getMaxSpeed();
		} else {
			Simulation.logger.warning("Achtung, Bus ist gerade nicht auf einer Streettile..");
			return getDefaultSpeed();
		}
	}
	
	public static double getDefaultSpeed() {
		return 0.05d;
	}
	
	public static int getDefaultMaxPersons() {
		return 20;
	}



}
