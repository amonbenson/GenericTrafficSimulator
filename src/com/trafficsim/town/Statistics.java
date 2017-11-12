package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

public class Statistics {
	
	private int countNoStationFound = 0;
	private int countNoRouteFound = 0;
	private int countRouteSameTargets = 0; //"Fehler", wenn Start und Ziel gleich sind
	private int errorNoRoute = 0; //insgesamte Fehler wenn irgendwie keine Route gefunden wurden konnte
	private int countRouteFound = 0;
	private HashSet<Waypoint> noStationNearby;
	
	private ArrayList<RouteTime> travelTimes;
	public Statistics() {
		noStationNearby = new HashSet<Waypoint>();
		travelTimes = new ArrayList<RouteTime>();
		travelTimes.add(new RouteTime(null, 0, 2));
		travelTimes.add(new RouteTime(null, 0, 4));
		travelTimes.add(new RouteTime(null, 0, 1000));
	}
	
	public void export(String fileName) {
		
	}
	
	public void print() {
		System.out.println("Route found: "+countRouteFound);
		System.out.println("No Station found: "+countNoStationFound + " ("+countNoStationFound/((float)errorNoRoute+countRouteFound)*100f+"%)");
		System.out.println("No Route found: "+countNoRouteFound + " ("+countNoRouteFound/((float)errorNoRoute)*100f+"%)");
		System.out.println("Route same targets (counts as error): "+countRouteSameTargets + " ("+countRouteSameTargets/((float)errorNoRoute)*100f+"%)");
		System.out.println("All Errors: "+errorNoRoute+"("+(errorNoRoute/(float)(errorNoRoute+countRouteFound))*100f+"%)");
		System.out.println("Koordinaten welche nicht abgedeckt sind: ");
		for (Iterator<Waypoint> i = noStationNearby.iterator(); i.hasNext();) {
			Waypoint w = i.next();
			System.out.print(w);
		}
		System.out.println("Insgesamte Zeit f�r den Transport:");
		System.out.println(getMedianTravelTime());
	}
	
	public void print(String toPrintIn) {
		
	}
	
	public int getCounterRouteFound() {
		return countRouteFound;
	}
	
	public int getCounterNoRouteFound() {
		return countNoRouteFound;
	}
	
	public int getCounterNoStationFound() {
		return countNoStationFound;
	}
	
	public int getErrorNoRoute() {
		return errorNoRoute;
	}
	
	public int getCountRouteSameTargets() {
		return countRouteSameTargets;
	}
	
	public void addRouteFound() {
		countRouteFound++;
	}
	
	public void addRouteNotFound() {
		countNoRouteFound++;
		errorNoRoute++;
	}
	
	
	public void addNoStationFound() {
		countNoStationFound++;
		errorNoRoute++;
	}
	/**
	 * Logt den Fehler dass der Algorithmus keine Station gefunden hat mit dieser Position
	 * @param w Position, auf welchen keine nahe Station gefunden werden konnte
	 */
	public void addNoStationFound(Waypoint w) {
		addNoStationFound();
		noStationNearby.add(w);
	}

	/**
	 * Z�hlt nicht als Fehler, der Algorithmus ist ja nicht Schuld
	 */
	public void addRouteSameTargets() {
		countRouteSameTargets++;
		//errorNoRoute++;
	}
	
	public void addTravelTime(Route r, long start, long end) {
		addTravelTime(new RouteTime(r, start, end));
	}
	
	public void addTravelTime(RouteTime r) {
		travelTimes.add(r);
	}
	
	/**
	 * Gibt das arithmetische Mittel aller ben�tigten Reisezeiten von angekommenden Menschen zur�ck
	 * @return
	 */
	public float getAverageTravelTime() {
		long sum = 0;
		for (RouteTime r : travelTimes) {
			sum += r.getDuration();
		}
		return (float)sum/(float)travelTimes.size();
	}
	/**
	 * Gibt den Median der ben�tigten Reisezeit aller gereisten (angekommenden) Menschen zur�ck
	 * @return
	 */
	public float getMedianTravelTime() {
		if (travelTimes.size() <= 1) {
			if (travelTimes.size() == 1) {
				return travelTimes.get(0).getDuration();
			} else {
				return -1;
			}
		}
		ArrayList<RouteTime> copy = new ArrayList<RouteTime>(travelTimes);
		Collections.sort(copy, new Comparator<RouteTime>() {
		    public int compare(RouteTime o1, RouteTime o2) {
		    	if (o1.getDuration() == o2.getDuration() ) return 0;
		        return (o1.getDuration() < o2.getDuration() ? -1 : 1);
		    }
		});
		
		if (travelTimes.size() % 2 == 0) { //gerade Anzahl, arithmetisches Mittel vom Unter und Obermedian wird ben�tigt
			long lower, upper;
			if (travelTimes.size() == 2) {
				lower = copy.get(0).getDuration();
				upper = copy.get(1).getDuration();
			} else {
				lower = copy.get(copy.size()/2).getDuration();
				upper = copy.get(copy.size()/2+1).getDuration();
			}
			return (lower+upper)/2f;
		} else { //einfach das mittlere Element picken
			return copy.get((int)(copy.size()-1)/2).getDuration();
		}
	}
}
