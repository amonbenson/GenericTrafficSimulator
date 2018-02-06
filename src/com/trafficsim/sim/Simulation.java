package com.trafficsim.sim;

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
	
	/**
	 * Gibt eine Teststadt zur�ck.
	 */
	public static float[][][] testTown() {
		float[][][] townList = new float[10][10][3];
		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				townList[x][y][0] = 1; //Haus
				townList[x][y][1] = 10; //Anzahl Bewohner
				townList[x][y][2] = 5; //Interessenfaktor, wie viele Leute wollen hier hin
			}
		}
		//Hauptstra�e:
		for (int x = 0; x < 9; x++) {
			townList[1+x][4][0] = 0;
			townList[1+x][4][1] = 1f/3f;
		}
		//Drei kleinere Stra�en:
		for (int y = 0; y < 3; y++) {
			townList[3][1+y][0] = 0;
			townList[3][1+y][1] = 0.5f/3f;			
		}
		for (int y = 0; y < 3; y++) {
			townList[7][1+y][0] = 0;
			townList[7][1+y][1] = 0.5f/3f;			
		}
		for (int y = 0; y < 4; y++) {
			townList[5][5+y][0] = 0;
			townList[5][5+y][1] = 0.5f/3f;			
		}
		//Nebenstra�en:
		for (int x = 0; x < 2; x++) {
			townList[1+x][1][0] = 0;
			townList[1+x][1][1] = 0.25f/3f;
		}
		for (int x = 0; x < 3; x++) {
			townList[2+x][7][0] = 0;
			townList[2+x][7][1] = 0.25f;
		}
		for (int x = 0; x < 2; x++) {
			townList[6+x][7][0] = 0;
			townList[6+x][7][1] = 0.25f;
		}
		return townList;
	}
	
	/*public static float[][][] testTownManhatten() {
		float[][][] town = new float[25][2][3];
	}*/
	
	public static float[][][] testTown3x3() {
		float[][][] townList = new float[3][3][3];
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				townList[x][y][0] = 1; //Haus
				townList[x][y][1] = 10; //10 Einwohner
				townList[x][y][2] = 5; //Interessenfaktor, wie viele Leute wollen hier hin
			}
		}
		
		townList[1][0][0] = 0;
		townList[1][0][1] = 0.5f;
		townList[1][1][0] = 0;
		townList[1][1][1] = 0.5f;
		townList[2][1][0] = 0;
		townList[2][1][1] = 0.5f;
		townList[0][1][0] = 0;
		townList[0][1][1] = 0.5f;
		townList[1][2][0] = 0;
		townList[1][2][1] = 0.5f;
		
		return townList;
	}
}
