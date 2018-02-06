package com.trafficsim.town;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	//Fehler, wenn keine Route erzeugt wird, weil der Weg zu umständlich ist.
	private int countCrypleErrors = 0;
	private HashSet<Tile> noStationNearby;
	
	private ArrayList<RouteTime> travelTimes;
	public Statistics() {
		noStationNearby = new HashSet<Tile>();
		travelTimes = new ArrayList<RouteTime>();
	}
	
	public void export(String fileName) {
		
	}
	
	public void print(Town t) {
		System.out.println("Route found: "+countRouteFound);
		System.out.println("No Station found: "+countNoStationFound + " ("+countNoStationFound/((float)errorNoRoute+countRouteFound)*100f+"%)");
		System.out.println("No Route found: "+countNoRouteFound + " ("+countNoRouteFound/((float)errorNoRoute)*100f+"%)");
		System.out.println("Route same targets (counts as error): "+countRouteSameTargets + " ("+countRouteSameTargets/((float)errorNoRoute)*100f+"%)");
		System.out.println("Cryple-Error: "+getCounterCrypleError());
		System.out.println("All Errors: "+errorNoRoute+"("+(errorNoRoute/(float)(errorNoRoute+countRouteFound))*100f+"%)");
		System.out.println("Koordinaten welche nicht abgedeckt sind: (insgesamt "+noStationNearby.size()+")");
		for (Iterator<Tile> i = noStationNearby.iterator(); i.hasNext();) {
			Tile w = i.next();
			System.out.print(w.getX()+":"+w.getY()+"\n");
		}
		System.out.println("Insgesamte Zeit für den Transport:");
		System.out.println(getAverageTravelTime(t));
	}
	
	
	public int getCounterRouteFound() {
		return countRouteFound;
	}
	
	public int getCounterCrypleError() {
		return countCrypleErrors;
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

	public HashSet<Tile> getNoStationNearby() {
		return noStationNearby;
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
	public void addNoStationFound(Tile w) {
		addNoStationFound();
		noStationNearby.add(w);
	}

	/**
	 * Zählt nicht als Fehler, der Algorithmus ist ja nicht Schuld
	 */
	public void addRouteSameTargets() {
		countRouteSameTargets++;
		//errorNoRoute++;
	}
	
	public void addCrypleError() {
		countCrypleErrors++;
		countRouteFound--;
		countNoRouteFound++;
	}
	
	public void addTravelTime(Route r, long start, long end) {
		addTravelTime(new RouteTime(r, start, end));
	}
	
	public void addTravelTime(RouteTime r) {
		travelTimes.add(r);
	}
	
	public int getCounterRouteDone() {
		return travelTimes.size();
	}
	
	/**
	 * Gibt das arithmetische Mittel aller benötigten Reisezeiten von angekommenden Menschen zurück.
	 * In TravelTime sind nur die erfolgreichen Transporte geloggt, daher müssen noch die Fehler+
	 * nicht transportierten Personen hinzugefügt werden.
	 * @return
	 */
	public float getAverageTravelTime(Town t) {
		BigDecimal sumBig = new BigDecimal(0);
		long sum = 0;
		for (RouteTime r : travelTimes) { //Erfolgreiche Transporte hinzufügen
			sumBig = sumBig.add(new BigDecimal(r.getDuration()));
			sum += r.getDuration();
		}

		sum += errorNoRoute*Integer.MAX_VALUE; //Fehler betrachten und mitzählen
		BigDecimal a = new BigDecimal(errorNoRoute);
		BigDecimal mul = a.multiply(new BigDecimal(Integer.MAX_VALUE));

		sumBig = sumBig.add(mul);
		//Personen hinzuzählen, die irgendwo noch rumstehen:
			//in Bussen:
		ArrayList<Bus> busses = t.getBusses();
		int personCounter=0;
		for ( Bus b : busses ) {
			ArrayList<Person> persons = b.getPersons();
			for ( Person p : persons ) {
				sumBig = sumBig.add(new BigDecimal(t.getTime() - p.getTimeStart()));
				sum += (t.getTime() - p.getTimeStart()); //Fügt die Differenz hinzu, wie lange der Mensch schon wartet
				personCounter++;
			}
		}
			//in Stationen:
		ArrayList<StreetTile> streets = t.getStreetTiles();
		for ( StreetTile street : streets ) {
			if (street.isStation()) {
				ArrayList<Person> persons = street.getPersons();
				for ( Person p : persons ) {
					sumBig = sumBig.add(new BigDecimal(t.getTime() - p.getTimeStart()));
					sum += (t.getTime() - p.getTimeStart()); //Fügt die Differenz hinzu, wie lange der Mensch schon wartet
					personCounter++;
				}
			}
		}
		int elems = travelTimes.size() + errorNoRoute + personCounter;
		if (elems != 0) {
			sumBig = sumBig.divide(new BigDecimal(elems), 50, RoundingMode.CEILING);
			System.out.println("Returning "+sumBig.floatValue());
			return sumBig.floatValue();
		} else {
			return 9999999f;
		}/*
		float value = (float)sum/(long)(elems);
		if (value < 0 ) {
			System.out.println("SUM: "+sum);
			System.out.println(sumBig);
			System.out.println("ER:" +value);
		}
		//System.out.println((float)sum/(float)(travelTimes.size() + errorNoRoute + personCounter));
		return (float)sum/(float)(elems);*/
	}
	/**
	 * Gibt den Median der benötigten Reisezeit aller gereisten (angekommenden) Menschen zurück
	 * @return
	 */
	public float getMedianTravelTime(Town t) {
		ArrayList<Long> times = new ArrayList<Long>();
		//TravelTimes (erfolgreiche Transporte) hinzufügen
		for ( RouteTime r : travelTimes ) {
			times.add(r.getDuration());
		}
		//Fehler hinzufügen
		for (int i=0;i<errorNoRoute;i++) {
			times.add(Long.MAX_VALUE);
		}
		
		//Personen hinzuzählen, die irgendwo noch rumstehen:
		//in Bussen:
		ArrayList<Bus> busses = t.getBusses();
		for ( Bus b : busses ) {
			ArrayList<Person> persons = b.getPersons();
			for ( Person p : persons ) {
				times.add(t.getTime() - p.getTimeStart()); //Fügt die Differenz hinzu, wie lange der Mensch schon wartet
			}
		}
		//in Stationen:
		ArrayList<StreetTile> streets = t.getStreetTiles();
		for ( StreetTile street : streets ) {
			if (street.isStation()) {
				ArrayList<Person> persons = street.getPersons();
				for ( Person p : persons ) {
					times.add(t.getTime() - p.getTimeStart()); //Fügt die Differenz hinzu, wie lange der Mensch schon wartet
				}
			}
		}
		
		//Und sortieren (klein nach groß)
		Collections.sort(times);
		
		if (times.size() <= 1) {
			if (times.size() == 1) {
				return times.get(0);
			} else {
				return Integer.MAX_VALUE;
			}
		}
		
		if (times.size() % 2 == 0) { //gerade Anzahl, arithmetisches Mittel vom Unter und Obermedian wird benötigt
			long lower, upper;
			if (times.size() == 2) {
				lower = times.get(0);
				upper = times.get(1);
			} else {
				lower = times.get(times.size()/2);
				upper = times.get(times.size()/2+1);
			}
			return (lower+upper)/2f;
		} else { //einfach das mittlere Element picken
			return times.get((int)(times.size()-1)/2);
		}
	}
}
