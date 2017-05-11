package com.trafficsim.sim;

import com.trafficsim.town.TimeHelper;
import com.trafficsim.town.Town;

/**
 * Manages the simulation of a Town
 *
 */
public class Simulation {
	
	private Town town = null;
	
	public Simulation() {
		this(null);
	}
	
	public Simulation(Town town) {
		this.town = town;
	}
	
	/**
	 * Startet die Simulation mit einer Laufzeit von einer Woche
	 */
	public void startSimulation() {
		startSimulation(TimeHelper.WEEK);
	}
	
	public void startSimulation(long timeGoal) {
		 if ( town == null ) throw new NullPointerException("Town is null, cannot start simulation");
		 town.setCurrentTime(0);
		 town.generateRoutings();
		 System.out.println(town.getRoutingEvents());
		 System.exit(0);
		 for (int i=0; i<timeGoal;i++) {
			 
			
		 }
		 
	}
	
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
	
}
