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
		 town.setCurrentTime(0);
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
	
	public static void main(String[] args) {
		Town t = new Town(5, 5);
		Random r = t.getRandom();
		t.generateTiles(randomTown(5, 5, r));
		
		Simulation s = new Simulation(t);
		s.startSimulation();
	}
	
	public static float[][][] randomTown(int width, int height) {
		return randomTown(width, height, new Random());
	}
	
	public static float[][][] randomTown(int width, int height, Random r) {
		float[][][] townList = new float[width][height][2];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if ( x == 2 || x == 4 || y == 2 || y == 4 ) {
					townList[x][y][0] = 0;
					townList[x][y][1] = 0.3f;
				} else {
					townList[x][y][0] = 1;
					townList[x][y][1] = r.nextInt(5)+1;
				}
			}
		}
		return townList;
	}
}
