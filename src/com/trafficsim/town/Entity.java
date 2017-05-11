package com.trafficsim.town;

public abstract class Entity implements Updateable, Initable {
	private double x, y; //Position des Entitys
	
	public Entity(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Wird vor dem Beginn der Simulation aufgerufen
	 */
	public void init() {
		
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
