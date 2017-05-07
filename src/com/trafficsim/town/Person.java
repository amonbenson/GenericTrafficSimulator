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

	public void setRoute(Route route) {
		if (route == null) throw new NullPointerException("Route shouldn't be null. Use removeRoute() to remove the current route");
		
		this.route = route;
	}
	
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
