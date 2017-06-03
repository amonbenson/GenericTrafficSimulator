package com.trafficsim.pathfinding;

import java.util.ArrayList;

import com.trafficsim.town.Waypoint;

/**
 * Repräsentiert ein Knoten. Dieser hat zu jedem Nachbarknoten einen Kantenwert (in VertexEdge edgeVal)
 * @author Luca
 */
public class Vertex {
	public ArrayList<VertexEdge> outnodes;
	public int x, y; //Koordinaten
	
	public boolean equalTo(Vertex v) {
		return (v.x == x && v.y == y);
	}
	public boolean equalTo(Waypoint w) {
		return ((int) w.getX() == x && (int) w.getY() == y);
	}
	/**
	 * Gibt <code>true</code> zurück, wenn der Knoten <code>v</code> (Kontrolle nach Koordinaten) als Referenzknoten vorhanden ist
	 * Ansonsten wird <code>false</code> zurückgegeben.
	 */
	public boolean contains(Vertex v) {
		for (VertexEdge ve : outnodes) {
			if (v.equalTo(ve.vertex)) return true;
		}
		return false;
	}
}