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
	
	@Override
	public void start(Town t) {
		StreetTile station = route.getConnectedPerson().getNextStation(t.getTiles());
		if (station != null) {
			route.getConnectedPerson().setRoute(route);
			station.getPersons().add(route.getConnectedPerson());
		} else {
			Town.logger.info("Person hat keine Haltestelle in der Nähe gefunden :(");
		}
	}
	
	
	
}
