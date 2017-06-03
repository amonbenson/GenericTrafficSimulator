package com.trafficsim.sim;

import java.util.Random;

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
		 for (int i=0; i<timeGoal;i++) {
			 town.update();
		 }
		 
	}
	
	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	
	public static float[][][] randomTown(int width, int height) {
		return randomTown(width, height, new Random());
	}
	
	public static float[][][] randomTown(int width, int height, Random r) {
		float[][][] townList = new float[width][height][2];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if ( y == 3 || x == 3 || y == 0 || y == 5) { //Straße
					townList[x][y][0] = 0;
					townList[x][y][1] = 0.3f;
				} else { //Haus
					townList[x][y][0] = 1;
					townList[x][y][1] = 0;
				}
			}
		}
		townList[0][1][1] = 1; //0px, 1py einen Menschen setzen
		return townList;
	}
}
