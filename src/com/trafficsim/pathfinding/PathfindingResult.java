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
	/**
	 * Repr�sentiert unendlich f�r eine Wegl�nge, falls kein L�sungsweg gefunden wurde.
	 */
	public static final float INFINITY = -1;
	
	public float length; //Gibt die L�nge des Weges an
	public ArrayList<Vertex> path; //Gibt den Pfad des Weges an
	
	public PathfindingResult(float length, ArrayList<Vertex> path) {
		this.length = length;
		this.path = path;
	}
	
	/**
	 * Gibt eine leere L�sung zur�ck, hier betr�gt die Distanz -1 (unendlich) und im L�sungsweg befinden sich keine Elemente.
	 */
	public static PathfindingResult getEmptyResult() {
		return new PathfindingResult(INFINITY, new ArrayList<Vertex>());
	}
}
