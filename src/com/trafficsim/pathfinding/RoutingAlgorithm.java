package com.trafficsim.pathfinding;

import java.util.ArrayList;

import com.trafficsim.town.Event;
import com.trafficsim.town.Person;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Waypoint;

/**
 * Kümmert sich um den Wegfindungsalgorithmus, welcher den besten Weg für eine Route von Menschen findet
 * @author Luca
 *
 */
public class RoutingAlgorithm {
	
	private static Tile[][] tiles;
	private static Graph stationGraph; //Graph, welcher das Stationennetz darstellt
	
	
	public static void init(Tile[][] tiles) {
		RoutingAlgorithm.tiles = tiles;
		generateStationGraph(tiles);
	}
	


	public static void generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
	}
	
	
	
	
	/**
	 * Generiert den Graphen, welcher alle Stationen anzeigt.
	 * Dafür muss die Funktion in <code>Town</code> <code>applyChromosom</code> schon aufgerufen geworden sein.
	 */
	private static void generateStationGraph(Tile[][] tiles) {
		stationGraph = new Graph();
		
		/*
		 * Als erstes muss eine Liste mit allen Knoten erzeugt werden, damit später nur diese verwendet werden und 
		 * für den gleichen Knotenpunkt nicht 2 Instanzen erzeugt werden müssen.
		 */
		ArrayList<Vertex> allVertexes = new ArrayList<Vertex>();
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof StreetTile) {
					StreetTile street = (StreetTile) tiles[x][y];
					if (street.isStation()) {
						Vertex v = new Vertex();
						v.x = x;
						v.y = y;
						v.outnodes = new ArrayList<VertexEdge>();
						allVertexes.add(v);
					}
				}
			}
		}
		
		//Nun werden die endgültigen Knoten erzeugt:
		for (Vertex v : allVertexes) {
			StreetTile station = (StreetTile) tiles[v.x][v.y];
			for (Schedule s : station.getSchedules()) {
				int sIndex = s.getStationIndex(v.x, v.y);
				if (sIndex != -1) { //Index -1 heißt, diese Koordinate existiert im Fahrplan nicht als Station
					//Vorherige Station hinzufügen
					Waypoint wLeft = s.getStationLeft(sIndex);
					if (wLeft != null) {
						//wLeft Koordinate in allVertexes Liste finden (damit Verlinkungen immer funktionieren)
						for (Vertex v2 : allVertexes) {
							if (v2.equalTo(wLeft)) { //dieses Element ist gefunden:
								if (!v.contains(v2)) { //es kann sein, dass dieses Element schon vorhanden ist
									v.outnodes.add(new VertexEdge(v2, Math.abs(v2.x-v.x)+Math.abs(v2.y-v.y))); //Manhattendistanz als Dauer	
									break;
								}
							}
						}
					}
					//Nächste Station hinzufügen
					Waypoint wRight = s.getStationRight(sIndex);
					if (wRight != null) {
						//wRight Koordinate in allVertexes Liste finden (damit Verlinkungen immer funktionieren)
						for (Vertex v2 : allVertexes) {
							if (v2.equalTo(wRight)) { //dieses Element ist gefunden:
								if (!v.contains(v2)) { //es kann sein, dass dieses Element schon vorhanden ist
									v.outnodes.add(new VertexEdge(v2, Math.abs(v2.x-v.x)+Math.abs(v2.y-v.y))); //Manhattendistanz als Dauer								
									break;
								}
							}
						}
					}
					
				}
			}
			//Dieses Element schließlich zum stationGraph hinzufügen
			stationGraph.vertexes.add(v);
		}
		System.out.println("Done");
	}
	
	

	

	

}
