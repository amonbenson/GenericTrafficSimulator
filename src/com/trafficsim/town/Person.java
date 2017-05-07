package com.trafficsim.town;

public class Person {
	
	private double x, y;
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
		this.x = x;
		this.y = y;
		this.house = house;
		floating = false;
		
		route = null;
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
	
	public boolean isFloating() {
		return floating;
	}
	
	public void setFloating(boolean floating) {
		this.floating = floating;
	}
}
