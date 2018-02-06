package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;

import com.trafficsim.sim.Simulation;

/**
 * Repräsentiert eine Buslinie
 * 
 * WICHTIG:
 * Wenn in den Stations Waypoints doppelt vorkommen (z.B. Position 1,2) MÜSSEN diese das gleiche Objekt sein und dürfen nicht erneut erzeugt werden!
 */
public class Schedule {
	
	/*
	 * Liste mit Stationen, welche angefahren werden sollen
	 */
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
	

	public static final int FLAG_NO_INDEX=1234567890;
	
	public Schedule(ArrayList<Waypoint> stations, ArrayList<BusStartTime> busStartTimes, int minDelay, String name) {
		this.stations = stations;
		this.waypoints = null;
		this.busStartTimes = busStartTimes;
		this.minDelay = minDelay;
		this.name = name;
		
		scheduleNormal = new SpecificSchedule(this, BusDirection.NORMAL);
		scheduleReverse = new SpecificSchedule(this, BusDirection.REVERSE);
		
		this.minDelay = 10;
		
	}
	
	/**
	 * Berechnet alle Wegpunkte, welche angefahren werden müssen.
	 * Muss aufgerufen werden, bevor getBusCreationEvents() aufgerufen wird.
	 *  
	 * @param map Karte der Stadt
	 */
	//TODO A*-Algorithmus einbauen
	public void calcWaypoints(Town t) {
		if (stations.size() != 0) {
			waypoints = new ArrayList<Waypoint>();
			Waypoint start = stations.get(0);
			Waypoint end;
			for (int i=1;i<stations.size();i++) {
				end = stations.get(i);
				List<GridCell> result = t.findPath((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
				
				waypoints.add(start);
				for (GridCell c : result) {
					waypoints.add(new Waypoint(c.x+0.5, c.y+0.5));
				}
				//waypoints.add(end);
				
				start = stations.get(i);
			}
			//waypoints  = stations; //Momentan gibt es noch kein Wegfindungsalgorithmus, also müssen Stationen immer nebeneinander liegen
		} else {
			Simulation.logger.warning("Der Fahrplan beinhaltet keine Stationen!");
		}
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
	 * Gibt die Größe der Waypoints zurück.
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
	
	public ArrayList<BusStartTime> getBusStartTimes() {
		return busStartTimes;
	}
	
	/**
	 * Gibt die Station des Indexes <code>index</code> zurück.
	 */
	public Waypoint getStation(int index) {
		return stations.get(index);
	}
	
	public Waypoint getStationReverse(int index) {
		return stations.get(stations.size()-index-1);
	}
	
	/**
	 * Gibt die Station links/vorherig vom <code>index/<code> zurück.
	 * Falls diese außerhalb des Arrays liegt, wird <code>null</code> zurückgegeben.
	 */
	public Waypoint getStationLeft(int index) {
		if (index-1 < 0) {
			return null;
		} else {
			return stations.get(index-1);
		}
	}
	
	/**
	 * Gibt die Station rechts/nächste vom <code>index</code> zurück.
	 * Falls diese außerhalb des Arrays liegt, wird <code>null</code> zurückgegeben.
	 */
	public Waypoint getStationRight(int index) {
		if (index+1 >= stations.size()) {
			return null;
		} else {
			return stations.get(index+1);
		}
	}
	
	/**
	 * Gibt den Waypoint mit den Koordinaten zurück, falls dieser vorhanden ist.
	 * Ansonsten wird <code>null</code> zurückgegeben.
	 */
	public Waypoint searchWaypoint(int x, int y) {
		for ( Waypoint w : waypoints ) {
			if (w.isSame(x, y)) return w;
		}
		return null;
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
	 * Gibt den Index an, wo die angegebende Koordinate für die Station in diesem Fahrplan zu finden ist.
	 * Falls diese nicht zu finden ist, wird -1 zurückgegeben.
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


	/**
	 * Gibt zurück, mit welcher Richtung der Bus schneller zur angegebenden Station kommt.
	 * NULL wird zurückgegeben, falls einmal der Fehler no index auftratt.
	 */
	public BusDirection whichDirectionIsFaster(Waypoint start, Waypoint end) {
		float timeNormal, timeReverse;
		timeNormal = getTimeForBusDirectionNormal(start, end);
		timeReverse = getTimeForBusDirectionReverse(start, end);
		if (timeNormal == FLAG_NO_INDEX || timeNormal == FLAG_NO_INDEX) {
			return null;
		}
		return ( timeNormal <= timeReverse ? BusDirection.NORMAL : BusDirection.REVERSE );
	}
	

	public float getTimeForBusDirectionNormal(Waypoint start, Waypoint end) {
		float timeReverse = 0;
		
		int indexStart = getStationIndex((int) start.getX(), (int) start.getY());
		int indexEnd = getStationIndex((int) end.getX(), (int) end.getY());
		
		if (indexStart == -1 || indexEnd == -1) {
			//Simulation.logger.warning("Linie: " + name + " indexStart oder indexEnd konnte nicht gefunden werden! Start: "+start+",dessen Index:"+indexStart+":Ende:"+end+"dessen Index:"+indexEnd);
			return FLAG_NO_INDEX;
		} else if (indexStart == indexEnd) {
			Simulation.logger.warning("Start und End ist eine Station?"+start+":"+end);
		}
		
		//Messe Zeit für BusDirection.Normal
		int i = 0;
		int realIndex, realIndexNext;
		do {
			int sizeOfWay = 0;

			realIndex = (indexStart+i>=stations.size() ? stations.size()-1-((indexStart+i)%stations.size()) : i+indexStart);
			realIndexNext = (indexStart+i+1>=stations.size() ? stations.size()-1-((indexStart+1+i)%stations.size()) : i+1+indexStart);
			sizeOfWay = 
					Math.abs((int) (getStation(realIndex).getX()) - (int) (getStation(realIndexNext).getX()))+
					Math.abs((int) (getStation(realIndex).getY()) - (int) (getStation(realIndexNext).getY()));
					
			timeReverse += sizeOfWay;
			i++;
		} while (indexEnd != realIndexNext );
		return timeReverse;
	}
	
	
	public float getTimeForBusDirectionReverse(Waypoint start, Waypoint end) {
		float timeReverse = 0;
		
		int indexStart = getStationIndex((int) start.getX(), (int) start.getY());
		int indexEnd = getStationIndex((int) end.getX(), (int) end.getY());
		
		if (indexStart == -1 || indexEnd == -1) {
			//Simulation.logger.warning(name + " indexStart oder indexEnd konnte nicht gefunden werden! Start: "+start+",dessen Index:"+indexStart+":Ende:"+end+"dessen Index:"+indexEnd);
			return FLAG_NO_INDEX;
		} else if (indexStart == indexEnd) {
			Simulation.logger.warning("Start und End ist eine Station?"+start+":"+end);
		}
		
		//Messe Zeit für BusDirection.REVERSE
		int i = 0;
		i = 0;
		do {
			int sizeOfWay = 0;
			
			sizeOfWay = 
					Math.abs((int) (getStation(Math.abs(indexStart+i)).getX()) - (int) (getStation(Math.abs(indexStart+i-1)).getX()))+
					Math.abs((int) (getStation(Math.abs(indexStart+i)).getY()) - (int) (getStation(Math.abs(indexStart+i-1)).getY()));
					
			timeReverse += sizeOfWay;
			i--;
		} while (indexEnd != Math.abs(indexStart+i) );
		return timeReverse;
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
