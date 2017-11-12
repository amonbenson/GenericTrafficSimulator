package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Repr�sentiert eine Route, welche einen Start und Endpunkt hat (Tile) und die zugeh�rige Person, welche sich bewegen m�chte.
 * Au�erdem kann die Zeit, welche ben�tigt wird, um die Route zu fahren, gemessen werden.
 * Jede Route hat mindestens zwei ChangeStations, also Umsteigestationen. Bei einer Fahrt ohne Umsteigen sind hier nur 
 * Startstation und Endstation gespeichert.
 *
 */
public class Route {
	
	private Tile origin, target;
	/**
	 * Darf auch <code>null</code> sein, wenn keine Linie gefunden wurde.
	 */
	private ArrayList<ChangeStation> stations;
	/**
	 * Gibt den Index der n�chsten Umsteigestation an (bezogen auf <code>stations</code>)
	 */
	private int nextStation;
	
	/**
	 * Erzeugt eine Route. 
	 * 
	 * Die n�chste Station, welche angefahren werden muss, ist stations.get(1), da stations.get(0) 
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
	}
	
	public Route(Route route) {
		this.origin = route.origin;
		this.target = route.target;
		this.stations = route.stations;
		this.nextStation = route.nextStation;
	}


	//---------------------------GETTER----------------------
	


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
	 * Gibt die Station zur�ck, bei welcher die Person zuletzt war, oder momentan noch ist (und wartet)
	 */
	public ChangeStation getCurrentStation() {
		if (stations != null) {
			return stations.get(nextStation-1);
		}
		return null;
	}
	
	/**
	 * Gibt die n�chste Umsteigestation zur�ck.
	 * Falls <code>stations</code> <code>null</code> ist, wird <code>null</code> zur�ckgegeben.
	 */
	public ChangeStation getNextStation() {
		if (stations != null) {
			return stations.get(nextStation);
		}
		return null;
	}
	
	/**
	 * Wird aufgerufen, wenn die Route die n�chste Umsteigestation erreicht hat
	 */
	public void reachedChangeStation() {
		nextStation++;
	}
	
	/**
	 * Gibt an, ob die Route beendet wurde und die letzte (Umsteige)station erreicht wurde.
	 * Falls <code>stations</code> <code>null</code> ist, wird <code>false</code> zur�ckgegeben.
	 */
	public boolean isFinished() {
		if (stations != null) {
			return (nextStation >= stations.size())?true:false;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Origin "+origin+"\n"+
				"Target "+target+"\n"+
				"ChangeStations: "+Arrays.toString(stations.toArray())+"\n"+
				"nextStation(position): "+nextStation+"\n";
	}
}
