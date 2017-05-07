package com.trafficsim.town;

public class Town {
	private Tile[][] tiles;
	private int sizeX, sizeY;
	
	public Town(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
}
