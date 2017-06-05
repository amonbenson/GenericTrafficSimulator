package com.trafficsim.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DijkstraAlgorithm implements Pathfinding {

	/**
	 * Sucht den optimalen Weg von <code>start</code> nach <code>end</code> und gibt diesen zurück.
	 * Dafür wird eine Liste mit <code>Vertex</code> (Knotenpunkte) zurückgegeben, welche die beste Route von <code>start</code> nach <code>end</code> repräsentiert.
	 * 
	 * Existiert kein Weg, ist die zurückgegebende Liste leer (size = 0).
	 * 
	 * Existiert ein Weg, wird auch der Start- und Endvertex zurückgegeben. 
	 * Dabei ist der Startvertex immer das erste Element der Rückgabe, der Endvertex immer das letzte Element.
	 * 
	 * Ist Start-/Endvertex nicht im Graphen enthalten, wird eine leere Liste zurückgegeben.
	 * 
	 * 
	 * Die Rückgabe erfolgt immer im <code>PathfindingResult</code>container, welche eine Liste mit dem Lösungsweg sowie die benötigte Distanz enthält.
	 * Falls kein Lösungsweg existiert, beträgt die Distanz -1.
	 */
	public PathfindingResult bestWay(Graph g, Vertex start, Vertex end) {
		if (g.vertexes.contains(start) && g.vertexes.contains((end))) {
			
			HashSet<Vertex> visited = new HashSet<Vertex>();
			Vertex current;
			//Alle Entfernungen auf -1 (unendlich) und den vorherigen Knoten auf null setzen:
			for (Vertex v : g.vertexes) {
				v.data.put("distance", new Float(-1f));
				v.data.put("before", null);
			}
			
			Vertex dStart = null;
			//Startelement aus dijkstraGraph finden und setzen:
			for (Vertex dv : g.vertexes) {
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
				return PathfindingResult.getEmptyResult();
			}

			while (visited.size() != g.vertexes.size()) {

				
				//Neue Distanzen berechnen:
				for ( VertexEdge ve : current.outnodes) {
					float val = ve.edgeVal + (Float) current.data.get("distance");
					if ( ((Float)ve.vertex.data.get("distance")) == -1 ) {
						ve.vertex.data.put("distance", (Float)val);
						ve.vertex.data.put("before", current);
					} else { //Eventuell relax?
						if ( ((Float)ve.vertex.data.get("distance")) > val ) { //Relax?
							ve.vertex.data.put("distance", (Float)val);
							ve.vertex.data.put("before", current);
						}
					}
				}
				
				//Suche den Nachbarknoten mit kleinster Distanz, welcher noch nicht in der visited Liste ist:
				//Dafür als erstes ein nicht visited Element suchen und als Kontext setzen
				Vertex tmp = null;
				for (Vertex v : visited) {
					for (VertexEdge out : v.outnodes) {
						if (!visited.contains(out.vertex)) {
							tmp = out.vertex;
						}
					}
				}
				//Nun einen Nachbarknoten finden, welcher nicht visited ist und die kleinste Distanz hat:
				for (Vertex v : visited) {
					for (VertexEdge out : v.outnodes) {
						if (!visited.contains(out.vertex)) {
							if (out.edgeVal + ((Float) current.data.get("distance")) < (Float) tmp.data.get("distance")) {
								tmp = out.vertex;
							}
						}
					}
				}
				
				if (tmp == null) { //Es existiert kein Weg
					return PathfindingResult.getEmptyResult();
				}

				//Markiere diesen als besucht:
				current = tmp;
				visited.add(current);
				
				//Prüfen, ob Zielknoten als visited markiert wurde:
				if (current == end) {
					ArrayList<Vertex> back = new ArrayList<Vertex>();
					float length = (Float) current.data.get("distance");
					
					Vertex c = current;
					do {
						back.add(c);
						c = (Vertex) c.data.get("before");
					} while ( c != null );
					//Alle Daten löschen:
					for ( Vertex v : g.vertexes ) {
						v.clearData();
					}
					Collections.reverse(back);
					return new PathfindingResult(length, back);
				}
			}
			System.out.println("Error in DijkstraAlgorithm");
			return PathfindingResult.getEmptyResult();
		} else { //Start- oder Endknoten nicht im Graphen
			System.out.println("Warnung, Startvertex und/oder Endvertex nicht im Graphen enthalten.");
			return PathfindingResult.getEmptyResult();
		}
	}
}
