package com.trafficsim.town;

public class StreetTile extends Tile {
	
	private float maxSpeed;
	
	public StreetTile(int x, int y, float maxSpeed) {
		super(x, y);
		this.maxSpeed = maxSpeed;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}


	
	
}
