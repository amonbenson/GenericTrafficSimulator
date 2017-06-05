package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;

import com.trafficsim.pathfinding.Graph;
import com.trafficsim.pathfinding.Vertex;

/**
 * Repr�sentiert eine Buslinie
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
	 * Speichert beide m�glichen spezifischen Schedules, damit diese nicht mehrmals erzeugt werden m�ssen
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
	 * Berechnet alle Wegpunkte, welche angefahren werden m�ssen.
	 * Muss aufgerufen werden, bevor getBusCreationEvents() aufgerufen wird.
	 *  
	 * @param map Karte der Stadt
	 */
	//TODO A*-Algorithmus einbauen
	public void calcWaypoints(Tile[][] map) {
		waypoints  = stations; //Momentan gibt es noch kein Wegfindungsalgorithmus, also m�ssen Stationen immer nebeneinander liegen
	}
	
	public boolean hasSameName(String n) {
		return this.name.equalsIgnoreCase(n);
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
	 * Gibt die Gr��e der Waypoints zur�ck.
	 */
	public int getWaypointSize() {
		return waypoints.size();
	}
	
	public SpecificSchedule getScheduleNormal() {
		return scheduleNormal;
	}
	
	public SpecificSchedule getScheduleReverse() {
		return scheduleReverse;
	}
	
	/**
	 * Gibt die Station des Indexes <code>index</code> zur�ck.
	 */
	public Waypoint getStation(int index) {
		return stations.get(index);
	}
	/**
	 * Gibt die Station links/vorherig vom <code>index/<code> zur�ck.
	 * Falls diese au�erhalb des Arrays liegt, wird <code>null</code> zur�ckgegeben.
	 */
	public Waypoint getStationLeft(int index) {
		if (index-1 < 0) {
			return null;
		} else {
			return stations.get(index-1);
		}
	}
	
	/**
	 * Gibt die Station rechts/n�chste vom <code>index</code> zur�ck.
	 * Falls diese au�erhalb des Arrays liegt, wird <code>null</code> zur�ckgegeben.
	 */
	public Waypoint getStationRight(int index) {
		if (index+1 >= stations.size()) {
			return null;
		} else {
			return stations.get(index+1);
		}
	}
	
	/**
	 * Gibt den Waypoint mit den Koordinaten zur�ck, falls dieser vorhanden ist.
	 * Ansonsten wird <code>null</code> zur�ckgegeben.
	 */
	public Waypoint searchWaypoint(int x, int y) {
		for ( Waypoint w : waypoints ) {
			if (w.isSame(x, y)) return w;
		}
		return null;
	}
	
	/**
	 * Gibt den Waypoint des Indexes <code>index</code> zur�ck.
	 */
	public Waypoint getWaypoint(int index) {
		return waypoints.get(index);
	}
	/**
	 * Gibt den Waypoint zur�ck, nur werden die Waypoints hier in verkehrter Reihenfolge betrachtet.
	 * (wird ben�tigt, wenn BusDirection.REVERSE ist und die Waypoints von der anderen Richtung aus gelesen werden)
	 */
	public Waypoint getWaypointReverse(int index) {
		return waypoints.get(waypoints.size()-index-1);
	}
	
	/**
	 * Gibt den Index an, wo die angegebende Koordinate f�r die Station in diesem Fahrplan zu finden ist.
	 * Falls diese nicht zu finden ist, wird -1 zur�ckgegeben.
	 */
	public int getStationIndex(int x, int y) {
		for (int i=0;i<stations.size();i++ ) {
			if (stations.get(i).isSame(x, y)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Erzeugt die Events, welche die Stadt ben�tigt, um zum richtigen Zeitpunkt den Bus in die Stadt aufzunehmen.
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
	 * Pr�ft, ob die angegebene Koordinate im Einzugsbereich (f�r Personen) dieser Buslinie ist.
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


	/**
	 * Gibt zur�ck, mit welcher Richtung der Bus schneller zur angegebenden Station kommt.
	 */
	public BusDirection whichDirectionIsFaster(Waypoint start, Waypoint end) {
		float timeNormal = 0, timeReverse = 0;
		int indexStart = waypoints.indexOf(start);
		int indexEnd = waypoints.indexOf(end);
		
		//Messe Zeit f�r BusDirection.NORMAL
		int i = 0;
		do {
			int sizeOfWay = 0;
			sizeOfWay = Math.max( 
					Math.abs((int) (getWaypoint((indexStart+i)%waypoints.size()).getX()) - (int) (getWaypoint((indexStart+i+1)%waypoints.size()).getX())),
					Math.abs((int) (getWaypoint((indexStart+i)%waypoints.size()).getY()) - (int) (getWaypoint((indexStart+i+1)%waypoints.size()).getY()))
					);
			timeNormal += sizeOfWay;
			i++;
		} while (indexEnd == (indexStart+i)%waypoints.size());
		//Messe Zeit f�r BusDirection.REVERSE
		i = 0;
		do {
			int sizeOfWay = 0;
			sizeOfWay = Math.max( 
					Math.abs((int) (getWaypointReverse((indexStart+i)%waypoints.size()).getX()) - (int) (getWaypointReverse((indexStart+i+1)%waypoints.size()).getX())),
					Math.abs((int) (getWaypointReverse((indexStart+i)%waypoints.size()).getY()) - (int) (getWaypointReverse((indexStart+i+1)%waypoints.size()).getY()))
					);
			timeReverse += sizeOfWay;
			i++;
		} while (indexEnd == (indexStart+i)%waypoints.size());
		return ( timeNormal <= timeReverse ? BusDirection.NORMAL : BusDirection.REVERSE );
	}
	
	@Override
	public String toString() {
		return "Stations: "+Arrays.toString(stations.toArray())+"\n"+
				"Waypoints: "+Arrays.toString(waypoints.toArray())+"\n"+
				"Name: "+name+"\n"+
				"BusStartTimes: "+Arrays.toString(busStartTimes.toArray())+"\n"+
				"minDelay: "+minDelay+"\n";
	}

	public static int getDefaultMaxPersons() {
		return 30;
	}
	
	
}
