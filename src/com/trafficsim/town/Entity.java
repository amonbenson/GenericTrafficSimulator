package com.trafficsim.town;

public class Entity {
	protected double x, y; //Position des Entitys
	
	public Entity(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Wird für jeden Tick einmal aufgerufen
	 */
	public void update() { 
		
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
}
