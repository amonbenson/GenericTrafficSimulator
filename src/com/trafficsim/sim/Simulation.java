package com.trafficsim.sim;

import java.awt.Point;
import java.util.Random;
import java.util.logging.Logger;

import com.trafficsim.town.TimeHelper;
import com.trafficsim.town.Town;

/**
 * Manages the simulation of a Town
 *
 */
public class Simulation {
	
	private Town town = null;
	
	public static Logger logger = Logger.getGlobal();
	
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
				if ( x == 0 || x == 1 || x == 3 || x == 5 || x == 6 || y == 0 || y==1||y==2||y==3||y==4) { //Stra�e
					townList[x][y][0] = 0;
					townList[x][y][1] = 0.3f;
				} else { //Haus
					townList[x][y][0] = 1;
					townList[x][y][1] = 2;
				}
			}
		}
		return townList;
	}
}
