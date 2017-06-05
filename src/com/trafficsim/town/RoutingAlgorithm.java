package com.trafficsim.town;

import java.util.ArrayList;

import com.trafficsim.pathfinding.Graph;
import com.trafficsim.pathfinding.Vertex;
import com.trafficsim.pathfinding.VertexEdge;

/**
 * K�mmert sich um den Wegfindungsalgorithmus, welcher den besten Weg f�r eine Route von Menschen findet
 * @author Luca
 *
 */
public class RoutingAlgorithm {
	
	private static Tile[][] tiles;
	private static Graph stationGraph; //Graph, welcher das Stationennetz darstellt TODO implementieren
	private static Graph waypointGraph; //Graph, welcher das Stationennetz und Wegpunkte darstellt	
	
	public static void init(Tile[][] tiles) {
		RoutingAlgorithm.tiles = tiles;
		generateStationGraph(tiles);
	}
	

	/**
	 * 
	 * Generiert das RoutingEvent f�r eine bestimmte Person
	 *
	 * @param p
	 * @param tileStart
	 * @param tileEnd
	 */
	public static void generateRoutingForPerson(Person p, Tile tileStart, Tile tileEnd) {
		/*
		 * Als erstes muss f�r jede im Einzugsbereich liegende Station die beste Route berechnet werden.
		 * Daraus kann ermittelt werden, welche Station verwendet werden soll (die Route mit der geringsten L�nge ist die beste).
		 */
		ArrayList<StreetTile> allStations = tileStart.getAllNextStations(tiles);
		
	}
	
	
	/**
	 * Generiert den Graphen, welcher alle Stra�enwegpunkte anzeigt.
	 * Daf�r muss die Funktion in <code>Town</code> <code>applyChromosom</code> schon aufgerufen geworden sein.
	 */
	private static void generateStreetGraph(Tile[][] tiles) {
		waypointGraph = new Graph();
		
	}
	
	/**
	 * Generiert den Graphen, welcher alle Stationen anzeigt.
	 * Daf�r muss die Funktion in <code>Town</code> <code>applyChromosom</code> schon aufgerufen geworden sein.
	 */
	private static void generateStationGraph(Tile[][] tiles) {
		stationGraph = new Graph();
		
		/*
		 * Als erstes muss eine Liste mit allen Knoten erzeugt werden, damit sp�ter nur diese verwendet werden und 
		 * f�r den gleichen Knotenpunkt nicht 2 Instanzen erzeugt werden m�ssen.
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
						allVertexes.add(v);
					}
				}
			}
		}
		
		//Nun werden die endg�ltigen Knoten erzeugt:
		for (Vertex v : allVertexes) {
			StreetTile station = (StreetTile) tiles[v.x][v.y];
			for (Schedule s : station.getSchedules()) {
				int sIndex = s.getStationIndex(v.x, v.y);
				if (sIndex != -1) { //Index -1 hei�t, diese Koordinate existiert im Fahrplan nicht als Station
					//Vorherige Station hinzuf�gen
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
					//N�chste Station hinzuf�gen
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
			//Dieses Element schlie�lich zum stationGraph hinzuf�gen
			stationGraph.vertexes.add(v);
		}
	}
	
	

	


}
