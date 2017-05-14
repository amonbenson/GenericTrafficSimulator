package com.trafficsim.town;

/**
 * Repr�sentiert ein RoutingEvent, welches eine Aktivierungszeitpunkt sowie eine Route hat.
 * 
 */
public class RoutingEvent extends Event{

	private Person person;
	private Route route;
	
	public RoutingEvent(long startTime, Person person, Route route) {
		super(startTime);
		this.person = person;
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
		
		if (route.getStations() != null) {
			Tile startTile = t.getTiles()[(int) route.getStations().get(0).getStation().getX()][(int) route.getStations().get(0).getStation().getY()];
			if (startTile instanceof StreetTile) {
				StreetTile st = (StreetTile) startTile;
				if (st.isStation()) { //F�gt die Person der Startstation hinzu
					person.setRoute(route);
					st.addPerson(person);
				} else {
					Town.logger.warning("Startfield isn't a station (but a street)");
				}
			} else {
				Town.logger.warning("Startfield isn't a street");
			}
			
		} else {
			Town.logger.warning("Route is null");
		}
	}
	
	@Override
	public String toString() {
		return person+"\n"+route;
	}
	
	
}
