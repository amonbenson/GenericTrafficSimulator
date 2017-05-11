package com.trafficsim.town;

import java.util.ArrayList;

public class Person {
	
	//in welchem quadratischen Bereich die Person eine Station finden kann, bei 1 beträgt diese 3x3, bei 2 5x5 usw
	public static int AREA_STATION=2;
	
	private double x, y; //Position des Person


	private boolean floating; // when a person "floats" it won't be drawn to the town
	private Route route; //aktuelle Route
	private HouseTile house;
	
	/**
	 * Setzt die aktuelle Position auf -1, -1
	 * 		@param house das Haus der Person
	 */
	public Person(HouseTile house) {
		this(-1d, -1d, house);
	}
	
	/**
	 * Initialisiert eine Person mit Koordinaten und Haus
	 * 		@param x X-Koordinate
	 * 		@param y Y-Koordinate
	 * 		@param house das Haus der Person
	 */
	public Person(double x, double y, HouseTile house) {
		this.house = house;
		floating = false;
		
		route = null;
	}

	/**
	 * Gibt die nächste Station um Umkreis von <code>AREA_STATION</code>*2+1 zurück, wenn eine vorhanden ist.
	 * Ansonsten wird <code>null</code> zurückgegeben.
	 * @return nächste Station, ansonsten <code>null</code>
	 */
	public StreetTile getNextStation(Tile[][] map) {
		for (int x=house.getX()-AREA_STATION;x<=house.getX()+AREA_STATION;x++) {
			for (int y=house.getY()-AREA_STATION;y<=house.getY()+AREA_STATION;y++) {
				if (x >= 0 && x < map.length && y >= 0 && y < map[0].length ) { //Koordinaten müssen im Bereich liegen
					if (map[x][y] instanceof StreetTile) {
						if (((StreetTile) map[x][y]).isStation()) {
							return (StreetTile) map[x][y];
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Gibt alle verfügbaren Stationen dieser Person zurück.
	 * Falls es keine gibt, wird <code>null</code>zurückgegeben.
	 * @param map die Karte der Stadt
	 */
	public ArrayList<StreetTile> getAllNextStations(Tile[][] map) {
		ArrayList<StreetTile> back = new ArrayList<StreetTile>();
		for (int x=house.getX()-AREA_STATION;x<=house.getX()+AREA_STATION;x++) {
			for (int y=house.getY()-AREA_STATION;y<=house.getY()+AREA_STATION;y++) {
				if (x >= 0 && x < map.length && y >= 0 && y < map[0].length ) { //Koordinaten müssen im Bereich liegen
					if (map[x][y] instanceof StreetTile) {
						if (((StreetTile) map[x][y]).isStation()) {
							back.add((StreetTile)map[x][y]);
						}
					}
				}
			}
		}
		return back;
	}
	
	public HouseTile getHouse() {
		return house;
	}

	public void setHouse(HouseTile house) {
		this.house = house;
	}

	public Route getRoute() {
		return route;
	}

	/**
	 * Sets the current route of the person. Any existing rout will be overwritten.
	 * 
	 * @param route
	 * 			The route to set the person's one to. It must not be null, otherwise a null pointer exception is thrown. To clear
	 * 			a route use removeRoute()
	 */
	public void setRoute(Route route) {
		if (route == null) throw new NullPointerException("Route shouldn't be null. Use removeRoute() to clear the current route");
		
		this.route = route;
	}
	
	/**
	 * Clears the current route (sets it to null).
	 */
	public void removeRoute() {
		route = null;
	}
	
	public boolean isFloating() {
		return floating;
	}
	
	public void setFloating(boolean floating) {
		this.floating = floating;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
}
