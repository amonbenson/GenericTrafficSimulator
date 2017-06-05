package com.trafficsim.pathfinding;

public interface Pathfinding {
	public PathfindingResult bestWay(Graph g, Vertex start, Vertex end);
}
