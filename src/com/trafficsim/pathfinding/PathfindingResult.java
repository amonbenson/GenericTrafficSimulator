package com.trafficsim.pathfinding;

import java.util.ArrayList;

/**
 * Repräsentiert das Ergebnis eines Wegfindungsalgorithmus.
 * Dabei wird die Länge des Weges und die Vertexes (Knoten) gespeichert.
 * 
 * @author Luca
 *
 */
public class PathfindingResult {
	/**
	 * Repräsentiert unendlich für eine Weglänge, falls kein Lösungsweg gefunden wurde.
	 */
	public static final float INFINITY = -1;
	
	public float length; //Gibt die Länge des Weges an
	public ArrayList<Vertex> path; //Gibt den Pfad des Weges an
	
	public PathfindingResult(float length, ArrayList<Vertex> path) {
		this.length = length;
		this.path = path;
	}
	
	/**
	 * Gibt eine leere Lösung zurück, hier beträgt die Distanz -1 (unendlich) und im Lösungsweg befinden sich keine Elemente.
	 */
	public static PathfindingResult getEmptyResult() {
		return new PathfindingResult(INFINITY, new ArrayList<Vertex>());
	}
}
