package com.trafficsim.pathfinding;

import java.util.ArrayList;

public interface Pathfinding {
	public ArrayList<Vertex> bestWay(Graph g, Vertex start, Vertex end);
}
