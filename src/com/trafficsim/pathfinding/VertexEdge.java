package com.trafficsim.pathfinding;

/**
 * Repr�sentiert ein Knoten mt Kantenwert
 * @author Luca
 */
public class VertexEdge {
	public float edgeVal;
	public Vertex vertex;
	
	public VertexEdge() {
		
	}
	
	public VertexEdge(Vertex vertex, float edgeVal) {
		this.vertex = vertex;
		this.edgeVal = edgeVal;
	}
}