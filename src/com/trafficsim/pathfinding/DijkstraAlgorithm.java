package com.trafficsim.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;

public class DijkstraAlgorithm implements Pathfinding {

	public void bestWay(Graph g, Vertex start, Vertex end) {
		if (g.vertexes.contains(start) && g.vertexes.contains((end))) {
			
			HashSet<Vertex> visited = new HashSet<Vertex>();
			Vertex current;
			//Den Graph ersetzen, dabei alle Entfernungen auf -1 (unendlich) und den vorherigen Knoten auf null setzen:
			ArrayList<Vertex> dijkstraGraph = new ArrayList<Vertex>();
			for (Vertex v : g.vertexes) {
				Vertex dv = v;
				dv.data.put("distance", new Float(-1f));
				dv.data.put("before", null);
				dijkstraGraph.add(dv);
			}
			
			Vertex dStart = null;
			//Startelement aus dijkstraGraph finden und setzen:
			for (Vertex dv : dijkstraGraph) {
				if (dv.equalTo(start)) {
					dStart = dv;
					break;
				}
			}
			if (dStart != null) {
				dStart.data.put("distance", new Float(0f));
				dStart.data.put("before", null);
				visited.add(dStart);
				current = dStart;
			} else {
				System.out.println("Fehler im Dijkstra-Algorithmus, Startelement konnte in neuer Liste nicht gefunden werden.");
				return;
			}
			
			System.out.println("Los");
			
			while (visited.size() != g.vertexes.size()) {

				
				System.out.println("Berechne Distanzen neu");
				//Neue Distanzen berechnen:
				for ( VertexEdge ve : current.outnodes) {
					float val = ve.edgeVal + (Float) current.data.get("distance");
					if ( ((Float)ve.vertex.data.get("distance")) == -1 ) {
						System.out.println("Nachbarknoten auf unendlich: "+ve.vertex.data.get("name") + ", jetzt auf "+val);
						ve.vertex.data.put("distance", (Float)val);
					} else { //Eventuell relax?
						if ( ((Float)ve.vertex.data.get("distance")) > val ) { //Relax?
							System.out.println("Relaxiere "+ve.vertex.data.get("name") + " auf "+val);
							ve.vertex.data.put("distance", (Float)val);
						}
					}
				}
				
				//Suche den Nachbarknoten mit kleinster Distanz, welcher noch nicht in der visited Liste ist:
				Vertex tmp = null;
				for (VertexEdge out : current.outnodes) {
					if (!visited.contains(out.vertex)) {
						tmp = out.vertex;
						tmp.data.put("distance", out.edgeVal + ((Float) current.data.get("distance")));
					}
				}
				

				for (VertexEdge out : current.outnodes) {
					if (!visited.contains(out.vertex)) {
						System.out.println("Unbesuchter Knoten gefunden: "+out.vertex.data.get("name"));
						System.out.println(out.edgeVal + ((Float) current.data.get("distance")) + "?"+ (Float) tmp.data.get("distance"));
						if (out.edgeVal + ((Float) current.data.get("distance")) < (Float) tmp.data.get("distance")) {
							tmp = out.vertex;
							System.out.println("Besseren Nachbarknoten gefunden");
						}
					}
				}
				System.out.println("Bester Nachbarknoten: "+tmp.data.get("name"));
				//Markiere diesen als besucht:
				current = tmp;
				visited.add(current);
				System.out.println("Wurde als besucht markiert: "+current.data.get("name"));
				if ( ((String)current.data.get("name")).equalsIgnoreCase("z") ) {
					System.out.println("FERTIG");
					return;
				}
				
				
				System.out.println();
			}

			
		}
	}

	
	public static void main(String[] args) {
		Graph graph = new Graph();
		ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
		Vertex s = new Vertex("s");
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		Vertex f = new Vertex("f");
		Vertex g = new Vertex("g");
		Vertex z = new Vertex("z");
		s.outnodes.add(new VertexEdge(a, 5));
		s.outnodes.add(new VertexEdge(b, 2));
		s.outnodes.add(new VertexEdge(g, 4));
		
		a.outnodes.add(new VertexEdge(s, 5));
		a.outnodes.add(new VertexEdge(b, 1));
		a.outnodes.add(new VertexEdge(c, 3));
		
		b.outnodes.add(new VertexEdge(a, 1));
		b.outnodes.add(new VertexEdge(s, 2));
		b.outnodes.add(new VertexEdge(c, 8));
		
		c.outnodes.add(new VertexEdge(a, 3));
		c.outnodes.add(new VertexEdge(b, 8));
		c.outnodes.add(new VertexEdge(e, 6));		
		c.outnodes.add(new VertexEdge(d, 4));
		
		d.outnodes.add(new VertexEdge(c, 4));
		d.outnodes.add(new VertexEdge(e, 10));
		d.outnodes.add(new VertexEdge(f, 8));		
		d.outnodes.add(new VertexEdge(g, 2));		
		
		e.outnodes.add(new VertexEdge(c, 6));
		e.outnodes.add(new VertexEdge(d, 10));
		e.outnodes.add(new VertexEdge(z, 7));		
		
		f.outnodes.add(new VertexEdge(d, 8));
		f.outnodes.add(new VertexEdge(z, 11));
		
		g.outnodes.add(new VertexEdge(s, 4));
		g.outnodes.add(new VertexEdge(d, 2));		
		
		z.outnodes.add(new VertexEdge(e, 7));
		z.outnodes.add(new VertexEdge(f, 11));
		
		vertexes.add(s);
		vertexes.add(a);
		vertexes.add(b);
		vertexes.add(c);
		vertexes.add(d);
		vertexes.add(e);
		vertexes.add(f);
		vertexes.add(g);
		vertexes.add(z);
		graph.vertexes = vertexes;
		
		DijkstraAlgorithm algorithm = new DijkstraAlgorithm();
		algorithm.bestWay(graph, s, z);
	}
}
