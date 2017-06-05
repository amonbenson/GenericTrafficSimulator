package com.trafficsim.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;

import com.trafficsim.town.Waypoint;

/**
 * Repr�sentiert ein Knoten. Dieser hat zu jedem Nachbarknoten einen Kantenwert (in VertexEdge edgeVal)
 * @author Luca
 */
public class Vertex {
	public ArrayList<VertexEdge> outnodes;
	public int x, y; //Koordinaten
	public HashMap<String, Object> data; //Hier k�nnen, je nach Algorithmus, Daten gespeichert werden
	
	public Vertex() {
		outnodes = new ArrayList<VertexEdge>();
		data = new HashMap<String, Object>();
	}
	
	public Vertex(Vertex v) {
		outnodes = v.outnodes;
		x = v.x;
		y = v.y;
		data = v.data;
	}
	
	public Vertex(int x, int y){
		this.outnodes = new ArrayList<VertexEdge>();
		this.x = x;
		this.y = y;
		data = new HashMap<String, Object>();
	}
	
	public Vertex(ArrayList<VertexEdge> outnodes, int x, int y) {
		this.outnodes = outnodes;
		this.x = x;
		this.y = y;
		data = new HashMap<String, Object>();
	}
	
	public boolean equalTo(Vertex v) {
		return (v.x == x && v.y == y);
	}
	public boolean equalTo(Waypoint w) {
		return ((int) w.getX() == x && (int) w.getY() == y);
	}
	public boolean equalTo(int x, int y) {
		return (this.x == x && this.y == y);
	}
	
	/**
	 * L�scht alle Elemente aus <code>data</code>
	 */
	public void clearData() {
		data.clear();
	}
	
	/**
	 * Gibt <code>true</code> zur�ck, wenn der Knoten <code>v</code> (Kontrolle nach Koordinaten) als Referenzknoten vorhanden ist
	 * Ansonsten wird <code>false</code> zur�ckgegeben.
	 */
	public boolean contains(Vertex v) {
		for (VertexEdge ve : outnodes) {
			if (v.equalTo(ve.vertex)) return true;
		}
		return false;
	}
	

}