package com.trafficsim.sim;

import com.trafficsim.town.Town;

/**
 * Manages the simulation of a Town
 *
 */
public class Simulation {
	
	private Town town = null;
	
	
	/**
	 * Startet die Simulation mit einer Laufzeit von einer Woche
	 */
	public void startSimulation() {
		startSimulation(604800); //Eine Woche
	}
	
	public void startSimulation(long timeGoal) {
		 if ( town == null ) throw new NullPointerException("Town is null, cannot start simulation");
		 town.setCurrentTime(0);
		 for (int i=0; i<timeGoal;i++) {
			 
			 town.addCurrentTime();
		 }
		 
	}
	
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
	
	
	
	
}
