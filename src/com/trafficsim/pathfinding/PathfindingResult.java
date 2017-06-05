package com.trafficsim.pathfinding;

import java.util.ArrayList;

/**
 * Repr�sentiert das Ergebnis eines Wegfindungsalgorithmus.
 * Dabei wird die L�nge des Weges und die Vertexes (Knoten) gespeichert.
 * 
 * @author Luca
 *
 */
public class PathfindingResult {
	public float length; //Gibt die L�nge des Weges an
	public ArrayList<Vertex> path; //Gibt den Pfad des Weges an
	
	public PathfindingResult(float length, ArrayList<Vertex> path) {
		this.length = length;
		this.path = path;
	}
}
