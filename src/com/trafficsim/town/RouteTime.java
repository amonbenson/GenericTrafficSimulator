package com.trafficsim.town;

public class RouteTime {
	public Route route;
	public long startTime, endTime;
	
	public RouteTime(Route route, long startTime, long endTime) {
		this.route = route;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getDuration() {
		return endTime-startTime;
	}
}
