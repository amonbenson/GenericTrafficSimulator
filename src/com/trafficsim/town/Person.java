package com.trafficsim.town;

import java.util.Random;

public class Person {
	
	public static long currentIDTracker = 0; // Tracker for the current id, will be incremented to generate a unque id for every new Person.
	
	private double x, y; //Position des Person


	private boolean floating; // when a person "floats" it won't be drawn to the town
	private Route route; //aktuelle Route
	private HouseTile house;
	
	private Statistics statistics;
	private long timeStart, timeEnd;
	
	private long id; // A unique id that every Person has
	private String name; // A name to identify persons in the person list;
	
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
		this(x, y, house, null);
	}
	/**
	 * Initialisiert eine Person mit Koordinaten und Haus und Statistikobjekt zum loggen der Zeit
	 * 		@param x X-Koordinate
	 * 		@param y Y-Koordinate
	 * 		@param house das Haus der Person
	 */
	public Person(double x, double y, HouseTile house, Statistics statistics) {
		if (house == null) throw new NullPointerException("House tile cannot be null (we don't have homeless people yet).");
		
		id = createID();
		name = createName();
		
		this.house = house;
		floating = false;
		
		this.statistics = statistics;
		
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getID() {
		return id;
	}

	@Override
	public String toString() {
		return "X: "+x+"\n"
				+ "Y: "+y+"\n"
				+ "Route wird ausgelassen..\n"
				+ house;
	}
	
	public void start(long ticks) {
		timeStart = ticks;
	}
	
	public void done(long ticks) {
		timeEnd = ticks;
		statistics.addTravelTime(route, timeStart, timeEnd);
	}
	
	/**
	 * Creates an id for a new person.
	 * 
	 * @return		the newly generated id.
	 */
	private static long createID() {
		long id = currentIDTracker;
		currentIDTracker++;
		return id;
	}
	
	/**
	 * A static name generator that creates a random name String from a specific seed.
	 * 
	 * @param seed	The seed that will be used to generate a name (propably the person's id.
	 * 
	 * @return		The name string that was generated.
	 */
	private static String createName() {
		// Generate Name and Surname
		return Town.getPersonNameGenerator().getName() + " " + Town.getPersonNameGenerator().getName();
	}
}
