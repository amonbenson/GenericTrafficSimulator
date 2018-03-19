package com.trafficsim.sim;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.trafficsim.genericalgorithm.Units;
import com.trafficsim.town.NameGenerator;
import com.trafficsim.town.TimeHelper;
import com.trafficsim.town.Town;

/**
 * Manages the simulation of a Town
 *
 */
public class Simulation {
	
	public static NameGenerator nameGenerator = new NameGenerator();
	public static Logger logger = Logger.getGlobal();
	
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
				if ( x == 0 || x == 1 || x == 3 || x == 5 || x == 6 || y == 0 || y==1||y==2||y==3||y==4) { //Stra�e
					townList[x][y][0] = 0;
					townList[x][y][1] = Units.kmhToTilesPerTick(10);
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
				townList[x][y][0] = 1; //0=Strasse, 1=Haus
				townList[x][y][1] = 10; //Bus Geschw. (Kacheln Pro Tick) / Anzahl Bewohner
				townList[x][y][2] = 5; //Interessenfaktor, wie viele Leute wollen hier hin
			}
		}
		//Hauptstra�e:
		for (int x = 0; x < 9; x++) {
			townList[1+x][4][0] = 0;
			townList[1+x][4][1] = Units.kmhToTilesPerTick(50);
		}
		//Drei kleinere Stra�en:
		for (int y = 0; y < 3; y++) {
			townList[3][1+y][0] = 0;
			townList[3][1+y][1] = Units.kmhToTilesPerTick(30);
		}
		for (int y = 0; y < 3; y++) {
			townList[7][1+y][0] = 0;
			townList[7][1+y][1] = Units.kmhToTilesPerTick(30);
		}
		for (int y = 0; y < 4; y++) {
			townList[5][5+y][0] = 0;
			townList[5][5+y][1] = Units.kmhToTilesPerTick(30);
		}
		//Nebenstra�en:
		for (int x = 0; x < 2; x++) {
			townList[1+x][1][0] = 0;
			townList[1+x][1][1] = Units.kmhToTilesPerTick(10);
		}
		for (int x = 0; x < 3; x++) {
			townList[2+x][7][0] = 0;
			townList[2+x][7][1] = Units.kmhToTilesPerTick(10);
		}
		for (int x = 0; x < 2; x++) {
			townList[6+x][7][0] = 0;
			townList[6+x][7][1] = Units.kmhToTilesPerTick(10);
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
		townList[1][0][1] = Units.kmhToTilesPerTick(20);
		townList[1][1][0] = 0;
		townList[1][1][1] = Units.kmhToTilesPerTick(20);
		townList[2][1][0] = 0;
		townList[2][1][1] = Units.kmhToTilesPerTick(20);
		townList[0][1][0] = 0;
		townList[0][1][1] = Units.kmhToTilesPerTick(20);
		townList[1][2][0] = 0;
		townList[1][2][1] = Units.kmhToTilesPerTick(20);
		
		return townList;
	}

	public static float[][][] loadHeatMap(String file, float populationMin, float populationMax, float speedMin, float speedMax, float interestMin, float interestMax) {
		try {
			BufferedImage img;
			img = ImageIO.read(Simulation.class.getClassLoader().getResource(file));
			
			float[][][] map = new float[img.getWidth()][img.getHeight()][3];
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					Color col = new Color(img.getRGB(x, y));
					
					float isHouse = col.getBlue() < 128 ? 1f : 0f;
					float strength = 0;
					if (isHouse == 0f) {
						strength = limit(col.getGreen() / 255f, speedMin, speedMax);
					} else {
						strength = limit(col.getGreen() / 255f, populationMin, populationMax);
					}
					float interest = limit(col.getRed() / 255f, interestMin, interestMax);
					
					map[x][y][0] = isHouse;
					map[x][y][1] = strength;
					map[x][y][2] = interest;
				}
			}
			
			return map;
			
		} catch (Exception ex) {
			logger.severe("Couldn't load heatmap. Using default map instead.");
			ex.printStackTrace();
		}

		return testTown();
	}
	
	private static float limit(float factor, float limitLower, float limitUpper) {
		return limitLower + factor * (limitUpper - limitLower);
	}
}
