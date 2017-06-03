package com.trafficsim.town;

/**
 * Repräsentiert einen Wegpunkt, beispielsweise für einen Bus.
 * Zwei Wegpunkte müssen immer waagerecht / senkrecht zueinander sein, daher die Funktion isNextToEachOther()
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
	 * Prüft, ob der Wegpunkt valide im Kontext zum Wegpunkt w ist.
	 */
	public boolean isValidTo(Waypoint w) {
		return isValidTo(this, w);
	}
	
	/**
	 * Vergleicht, ob zwei Wegpunkte im Kontext zum anderen valide sind. (diese müssen waagerecht / senkrecht zueinander sein)
	 * @param w1 Wegpunkt 1
	 * @param w2 Wegpunkt 2
	 * @return true, wenn beide Wegpunkte zueinander valide sind.
	 */
	public static boolean isValidTo(Waypoint w1, Waypoint w2) {
		if (w1.getX() == w2.getX() ) return true;
		if (w1.getY() == w2.getY() ) return true;
		return false;
	}
	
	public boolean isSame(Waypoint w) {
		return ((int) getX() == (int) w.getX() && (int) getY() == (int) w.getY());
	}
	
	@Override
	public String toString() {
		return "X: "+x+"Y: "+y+"\n";
	}
}
