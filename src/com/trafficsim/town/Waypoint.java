package com.trafficsim.town;

/**
 * Repr�sentiert einen Wegpunkt, beispielsweise f�r einen Bus.
 * Zwei Wegpunkte m�ssen immer waagerecht / senkrecht zueinander sein, daher die Funktion isNextToEachOther()
 *
 */
public class Waypoint {
	private double x, y;

	public Waypoint(double x, double y) {
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
	
	/**
	 * Pr�ft, ob der Wegpunkt valide im Kontext zum Wegpunkt w ist.
	 */
	public boolean isNextTo(Waypoint w) {
		return isNextTo(this, w);
	}
	
	/**
	 * Vergleicht, ob zwei Wegpunkte im Kontext zum anderen valide sind. (diese m�ssen waagerecht / senkrecht zueinander sein)
	 * @param w1 Wegpunkt 1
	 * @param w2 Wegpunkt 2
	 * @return true, wenn beide Wegpunkte zueinander valide sind.
	 */
	public static boolean isNextTo(Waypoint w1, Waypoint w2) {
		if (w1.getX() == w2.getX() ) return true;
		if (w1.getY() == w2.getY() ) return true;
		return false;
	}
	
}
