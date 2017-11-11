package com.trafficsim.pathfinding;

public class Test {
	public static void main(String[] args) {
		Graph g = new Graph();
		
		Vertex a = new Vertex();
		Vertex b = new Vertex();
		Vertex c = new Vertex();
		Vertex d = new Vertex();

		d.outnodes.add(new VertexEdge(c, 4));
		d.outnodes.add(new VertexEdge(b, 1));
		c.outnodes.add(new VertexEdge(a, 2));
		b.outnodes.add(new VertexEdge(a, 3));
		b.outnodes.add(new VertexEdge(a, 3));
		
		g.vertexes.add(d);
		g.vertexes.add(c);
		g.vertexes.add(b);
		g.vertexes.add(a);
		
		DijkstraAlgorithm di = new DijkstraAlgorithm();
		PathfindingResult p = di.bestWay(g, d, a);
		System.out.println(p.path);
	}
}
