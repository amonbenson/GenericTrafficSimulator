package com.trafficsim.town;

/**
 * Repräsentiert ein RoutingEvent, welches eine Aktivierungszeitpunkt sowie eine Route hat.
 * 
 */
public class RoutingEvent extends Event{

	private Route route;
	
	public RoutingEvent(long startTime, Route route) {
		super(startTime);
		this.route = route;
	}
	
	public Route getRoute() {
		return route;
	}
	
	public void setRoute(Route route) {
		this.route = route;
	}
	
}
