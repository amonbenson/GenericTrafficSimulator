package com.trafficsim.generic;

import java.util.ArrayList;

import com.trafficsim.town.Bus;
import com.trafficsim.town.BusCreationEvent;
import com.trafficsim.town.Event;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Waypoint;

/**
 * Repr�sentiert eine Buslinie
 */
public class Schedule {
	
	private ArrayList<Waypoint> stations; //Liste mit Stationen, welche angefahren werden sollen
	/**
	 * Liste an Wegpunkten, welche chronologisch angefahren werden. Diese ergeben sich aus den 
	 * anzufahrenenden Stationen. Ein Wegpunkt ist immer eine Abweichung der Richtung, also eine Kurve, Kreuzung etc.
	 */
	private ArrayList<Waypoint> waypoints;
	private ArrayList<Long> startTimes;
	
	
	public Schedule(ArrayList<Waypoint> stations, ArrayList<Long> startTimes) {
		this.stations = stations;
		this.startTimes = startTimes;
	}
	
	
	//TODO A*-Algorithmus einbauen
	public void calcWaypoints(Tile[][] map) {
		waypoints  = stations; //Momentan gibt es noch kein Wegfindungsalgorithmus, also m�ssen Stationen immer nebeneinander liegen
	}
	
	/**
	 * Erzeugt die Events, welche die Stadt ben�tigt, um zum richtigen Zeitpunkt den Bus in die Stadt aufzunehmen.
	 * @return Liste mit den BusCreationEvents
	 */
	public ArrayList<Event> getBusCreationEvents() {
		ArrayList<Event> back = new ArrayList<Event>(startTimes.size());
		for ( Long l : startTimes ) {
			Bus b = new Bus(waypoints.get(0).getX(), waypoints.get(0).getY(), getDefaultMaxPersons(), waypoints);
			BusCreationEvent bce = new BusCreationEvent(l.longValue(), b);
			back.add(bce);
		}
		return back;
	}
	
	public void setStations(ArrayList<Waypoint> stations) {
		this.stations = stations;
	}


	public void setWaypoints(ArrayList<Waypoint> waypoints) {
		this.waypoints = waypoints;
	}


	public void setStartTimes(ArrayList<Long> startTimes) {
		this.startTimes = startTimes;
	}
	
	
	
	public ArrayList<Waypoint> getWaypoints() {
		return waypoints;
	}
	
	public ArrayList<Long> getStartTimes() {
		return startTimes;
	}
	
	public ArrayList<Waypoint> getStations() {
		return stations;
	}




	

	public static int getDefaultMaxPersons() {
		return 30;
	}
}
