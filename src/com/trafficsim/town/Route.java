package com.trafficsim.town;

/**
 * Repr�sentiert eine Route, welche einen Start und Endpunkt hat (Tile) und die zugeh�rige Person, welche sich bewegen m�chte.
 * Au�erdem kann die Zeit, welche ben�tigt wird, um die Route zu fahren, gemessen werden.
 *
 */
public class Route {
	
	private Tile origin, target;
	private long timeOrigin, timeTarget;
	private Person connectedPerson; //verweist auf die Person, welche die Route benutzt
	
	public Route(Tile origin, Tile target, Person connectedPerson) {
		if (origin == null) throw new NullPointerException("Origin can't be null.");
		if (target == null) throw new NullPointerException("Target can't be null.");
		if (connectedPerson == null) throw new NullPointerException("ConnectedPerson can't be null.");
		
		this.origin = origin;
		this.target = target;
		this.connectedPerson = connectedPerson;
		
		timeOrigin = -1;
		timeTarget = -1;
	}
	


	/**
	 * Will be called when the route starts, e. g. when a person want's to get from Tile A to B. This sets the origin
	 * time flag, so the total time can be read out later on.
	 * 
	 * @param currentTime
	 * 			Long value representing the current time of the town, used to set the origin time flag.
	 */
	public void startRoute(long currentTime) {
		timeOrigin = currentTime;
	}
	
	/**
	 * Will be called when the route ends, e. g. when a person arrived at it's target. This sets the target time flag
	 * So the total time can be read out later on. The function will throw an Exception if the route wasn't started
	 * before. Also, the time when stopping must be greater or equal than the time when starting.
	 * 
	 * @param currentTime
	 * 			Long value representing the current time of the town, used to set the target time flag.
	 */
	public void stopRoute(long currentTime) {
		if (timeOrigin == -1) throw new IllegalArgumentException("Route must be started first.");
		if (timeTarget < timeOrigin) throw new IllegalArgumentException("Stop time must be greater than start time.");
		
		timeTarget = currentTime;
	}

	//---------------------------GETTER----------------------
	
	/**
	 * Returns the total time that was taken while passing the route.
	 * 
	 * @return
	 * 			Long value to represent the taken time. Returns -1 when route wasn't started
	 * 			or hasn't finished yet.
	 */
	public long getTotalTime() {
		if (timeTarget == -1) return -1;
		else return timeTarget - timeOrigin;
	}

	public Tile getOrigin() {
		return origin;
	}

	public Tile getTarget() {
		return target;
	}

	public Person getConnectedPerson() {
		return connectedPerson;
	}

	//---------------------------SETTER----------------------
	
	public void setConnectedPerson(Person connectedPerson) {
		this.connectedPerson = connectedPerson;
	}
	
}
