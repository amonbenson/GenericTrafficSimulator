package com.trafficsim.town;

public abstract class Tile {
	private int x, y; //Koordinaten
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "X: "+x+" Y:"+y;
	}
}
