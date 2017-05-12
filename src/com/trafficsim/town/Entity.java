package com.trafficsim.town;

public abstract class Entity implements Updateable {
	private double x, y; //Position des Entitys
	
	public Entity(double x, double y) {
		this.x = x;
		this.y = y;
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
