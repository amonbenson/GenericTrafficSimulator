package com.trafficsim.town;

import java.util.ArrayList;

/**
 * Repräsentiert eine Buslinie
 */
public class Schedule {
	
	//Liste mit Stationen, welche angefahren werden sollen
	private ArrayList<Waypoint> stations;
	/**
	 * Liste an Wegpunkten, welche chronologisch angefahren werden. Diese ergeben sich aus den 
	 * anzufahrenenden Stationen. Ein Wegpunkt ist immer eine Abweichung der Richtung, also eine Kurve, Kreuzung etc.
	 */
	private ArrayList<Waypoint> waypoints;
	//Name der Buslinie, z.B. M85
	private String name;
	/**
	 * Zeitpunkte der Buserzeugungen, inklusive deren Richtung
	 */
	private ArrayList<BusStartTime> busStartTimes;
	/**
	 * Gibt an, in welcher Taktzahl Busse minimal an einer Station abfahren sollen.
	 * Beispiel:
	 * minDelay = 10
	 * Bus A erreicht Station 1 zum Zeitpunkt 5
	 * Bus B erreicht Station 1 zum Zeitpunkt 13
	 * 13-5<10, daher muss der Bus noch 10-(13-5)=2 Ticks an dieser Station warten
	 */
	private long minDelay;
	
	/**
	 * Speichert beide möglichen spezifischen Schedules, damit diese nicht mehrmals erzeugt werden müssen
	 */
	private SpecificSchedule scheduleNormal, scheduleReverse;
	
	public Schedule(ArrayList<Waypoint> stations, ArrayList<BusStartTime> busStartTimes, int minDelay, String name) {
		this.stations = stations;
		this.waypoints = null;
		this.busStartTimes = busStartTimes;
		this.minDelay = minDelay;
		this.name = name;
		
		scheduleNormal = new SpecificSchedule(this, BusDirection.NORMAL);
		scheduleReverse = new SpecificSchedule(this, BusDirection.REVERSE);
	}
	
	
	/**
	 * Berechnet alle Wegpunkte, welche angefahren werden müssen.
	 * Muss aufgerufen werden, bevor getBusCreationEvents() aufgerufen wird.
	 *  
	 * @param map Karte der Stadt
	 */
	//TODO A*-Algorithmus einbauen
	public void calcWaypoints(Tile[][] map) {
		waypoints  = stations; //Momentan gibt es noch kein Wegfindungsalgorithmus, also müssen Stationen immer nebeneinander liegen
	}
	
	
	public long getMinDelay() {
		return minDelay;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Waypoint> getStations() {
		return stations;
	}
	
	/**
	 * Gibt die Größe der Waypoints zurück.
	 */
	public int getWaypointSize() {
		return waypoints.size();
	}
	/**
	 * Gibt den Waypoint des Indexes <code>index</code> zurück.
	 */
	public Waypoint getWaypoint(int index) {
		return waypoints.get(index);
	}
	/**
	 * Gibt den Waypoint zurück, nur werden die Waypoints hier in verkehrter Reihenfolge betrachtet.
	 * (wird benötigt, wenn BusDirection.REVERSE ist und die Waypoints von der anderen Richtung aus gelesen werden)
	 */
	public Waypoint getWaypointReverse(int index) {
		return waypoints.get(waypoints.size()-index-1);
	}
	
	/**
	 * Erzeugt die Events, welche die Stadt benötigt, um zum richtigen Zeitpunkt den Bus in die Stadt aufzunehmen.
	 * @return Liste mit den BusCreationEvents
	 */
	public ArrayList<Event> getBusCreationEvents(Town town) {
		if (waypoints == null) throw new NullPointerException("No waypoints found - call calcWaypoints(Tile[][]) before to create them");
		ArrayList<Event> back = new ArrayList<Event>(busStartTimes.size());
		for ( BusStartTime bst : busStartTimes ) {
			Bus b;
			if (bst.getDirection() == BusDirection.NORMAL) {
				b = new Bus(getWaypoint(0).getX(), getWaypoint(0).getY(), scheduleNormal, town);
			} else { //reverse
				b = new Bus(getWaypointReverse(0).getX(), getWaypointReverse(0).getY(), scheduleReverse, town);				
			}
			BusCreationEvent bce = new BusCreationEvent(bst.getStartTime(), b);
			back.add(bce);
		}
		return back;
	}
	
	
	
	/**
	 * @see #canGetToTarget(int, int)
	 */
	public boolean canGetToTarget(Tile t) {
		return canGetToTarget(t.getX(), t.getY());
	}
	
	/**
	 * Prüft, ob die angegebene Koordinate im Einzugsbereich (für Personen) dieser Buslinie ist.
	 */
	public boolean canGetToTarget(int x, int y) {
		for ( Waypoint s : stations ) {
			if (x > s.getX() - Tile.AREA_STATION && x < s.getX() + Tile.AREA_STATION &&
					y > s.getY() - Tile.AREA_STATION && y < s.getY() + Tile.AREA_STATION) {
				return true;
			}
		}
		return false;
	}
	
	public Waypoint whichStationIsInArea(int x, int y) {
		for ( Waypoint s : stations ) {
			if (x > s.getX() - Tile.AREA_STATION && x < s.getX() + Tile.AREA_STATION &&
					y > s.getY() - Tile.AREA_STATION && y < s.getY() + Tile.AREA_STATION) {
				return s;
			}
		}
		return null;
	}


	

	public static int getDefaultMaxPersons() {
		return 30;
	}
}
