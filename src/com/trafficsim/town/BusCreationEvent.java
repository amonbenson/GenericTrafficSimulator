package com.trafficsim.town;

/**
 * Repräsentiert ein BusCreationEvent, welches eine Aktivierungszeitpunkt sowie einen Bus hat.
 * Um die gegebene Zeit soll der angegebene Bus in der Stadt erzeugt werden.
 */
public class BusCreationEvent extends Event {

	private Bus bus;
	
	public BusCreationEvent(long startTime, Bus bus) {
		super(startTime);
		this.bus = bus;
	}

	public Bus getBus() {
		return bus;
	}

	public void setBus(Bus bus) {
		this.bus = bus;
	}
	
	public void start(Town t) {
		System.out.println("Bus creation event");
		t.getBusses().add(bus);
	}

}
