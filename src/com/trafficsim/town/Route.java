package com.trafficsim.town;

import java.util.ArrayList;

/**
 * Repräsentiert eine Route, welche einen Start und Endpunkt hat (Tile) und die zugehörige Person, welche sich bewegen möchte.
 * Außerdem kann die Zeit, welche benötigt wird, um die Route zu fahren, gemessen werden.
 * Jede Route hat mindestens zwei ChangeStations, also Umsteigestationen. Bei einer Fahrt ohne Umsteigen sind hier nur 
 * Startstation und Endstation gespeichert.
 *
 */
public class Route {
	
	private Tile origin, target;
	private long timeOrigin, timeTarget;
	/**
	 * Darf auch <code>null</code> sein, wenn keine Linie gefunden wurde.
	 */
	private ArrayList<ChangeStation> stations;
	/**
	 * Gibt den Index der nächsten Umsteigestation an (bezogen auf <code>stations</code>)
	 */
	private int nextStation;
	
	/**
	 * Erzeugt eine Route. 
	 * 
	 * Die nächste Station, welche angefahren werden muss, ist stations.get(1), da stations.get(0) 
	 * als Startpunkt definiert ist und so nicht erneut angefahren werden muss
	 * @param origin Startfeld, nicht Startstation
	 * @param target Zielfeld, nicht Zielstation
	 * @param stations Stationen, welche angefahren werden sollen. Muss mindestens 2 Elemente beinhalten ( min. Start- und Zielstation )
	 */
	public Route(Tile origin, Tile target, ArrayList<ChangeStation> stations) {
		if (origin == null) throw new NullPointerException("Origin can't be null.");
		if (target == null) throw new NullPointerException("Target can't be null.");
		if (stations != null) {
			if (stations.size() < 2) throw new java.lang.IllegalArgumentException("Stations has to be the size bigger 1.");
		}
		
		this.origin = origin;
		this.target = target;
		this.stations = stations;
		
		nextStation = 1;
		timeOrigin = -1;
		timeTarget = -1;
	}
	


	/**
	 * Will be called when the route starts, e. g. when a person want's to get from Tile A to B. This sets the origin
	 * time flag, so the total time can be read out later on.
	 * 
	 * @param currentTime
	 * 			Long value representing the current time of the town, used to set the origin time flag.
	 */
	public void startRoute(long currentTime) {
		timeOrigin = currentTime;
	}
	
	/**
	 * Will be called when the route ends, e. g. when a person arrived at it's target. This sets the target time flag
	 * So the total time can be read out later on. The function will throw an Exception if the route wasn't started
	 * before. Also, the time when stopping must be greater or equal than the time when starting.
	 * 
	 * @param currentTime
	 * 			Long value representing the current time of the town, used to set the target time flag.
	 */
	public void stopRoute(long currentTime) {
		if (timeOrigin == -1) throw new IllegalArgumentException("Route must be started first.");
		if (timeTarget < timeOrigin) throw new IllegalArgumentException("Stop time must be greater than start time.");
		
		timeTarget = currentTime;
	}

	//---------------------------GETTER----------------------
	
	/**
	 * Returns the total time that was taken while passing the route.
	 * 
	 * @return
	 * 			Long value to represent the taken time. Returns -1 when route wasn't started
	 * 			or hasn't finished yet.
	 */
	public long getTotalTime() {
		if (timeTarget == -1) return -1;
		else return timeTarget - timeOrigin;
	}

	public Tile getOrigin() {
		return origin;
	}

	public Tile getTarget() {
		return target;
	}
	
	public ArrayList<ChangeStation> getStations() {
		return stations;
	}
	
	/**
	 * Gibt die nächste Umsteigestation zurück.
	 * Falls <code>stations</code> <code>null</code> ist, wird <code>null</code> zurückgegeben.
	 */
	public ChangeStation getNextStation() {
		if (stations != null) {
			return stations.get(nextStation);
		}
		return null;
	}
	
	/**
	 * Wird aufgerufen, wenn die Route die nächste Umsteigestation erreicht hat
	 */
	public void reachedChangeStation() {
		nextStation++;
	}
	
	/**
	 * Gibt an, ob die Route beendet wurde und die letzte (Umsteige)station erreicht wurde.
	 * Falls <code>stations</code> <code>null</code> ist, wird <code>false</code> zurückgegeben.
	 */
	public boolean isFinished() {
		if (stations != null) {
			return (nextStation >= stations.size())?true:false;
		}
		return false;
	}
	
}
