package com.trafficsim.town;

public class Person {
	
	private double x, y;
	private boolean floating; // when a person "floats" it won't be drawn to the town
	private Route route;
	
	public Person(double x, double y) {
		this.x = x;
		this.y = y;
		floating = false;
		
		route = null;
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
