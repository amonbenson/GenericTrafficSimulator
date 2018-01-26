package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;

import com.trafficsim.pathfinding.DijkstraAlgorithm;
import com.trafficsim.pathfinding.Graph;
import com.trafficsim.pathfinding.PathfindingResult;
import com.trafficsim.pathfinding.Vertex;
import com.trafficsim.pathfinding.VertexEdge;

/**
 * K�mmert sich um den Wegfindungsalgorithmus, welcher den besten Weg f�r eine Route von Menschen findet
 * @author Luca
 *
 */
public class RoutingAlgorithm {
	
	private static Tile[][] tiles;
	private static Graph stationGraph; //Graph, welcher das Stationennetz darstellt
	private static Graph waypointGraph; //Graph, welcher Wegpunkte darstellt	
	private static Graph bigGraphs; //Graph, welcher Stationnetz und Wegpunkte darstellt
	
	public static void init(Tile[][] tiles) {
		RoutingAlgorithm.tiles = tiles;

		generateBigGraph(tiles);
		generateStationGraph(tiles);
		generateWaypointGraph(tiles);


	}
	

	/**
	 * 
	 * Generiert das RoutingEvent f�r eine bestimmte Person
	 *
	 * @param p
	 * @param tileStart
	 * @param tileEnd
	 */
	private void generateRoutingForPerson(Person p, Tile tileStart, Tile tileEnd) {
		/*
		 * Als erstes muss f�r jede im Einzugsbereich liegende Station die beste Route berechnet werden.
		 * Daraus kann ermittelt werden, welche Station verwendet werden soll (die Route mit der geringsten L�nge ist die beste).
		 * 
		 * Auch das Ziel kann eventuell durch mehrere Stationen erreicht werden, also muss auch hier f�r jede die beste Route berechnet werden.
		 */
		
		ArrayList<StreetTile> allStationsStart = tileStart.getAllNextStations();
		ArrayList<StreetTile> allStationsEnd = tileEnd.getAllNextStations();		
		
		System.out.println("ACHTUNG, KOMISCHE FUNKTION WIRD BENUTZT");
		PathfindingResult bestResult = null;
		
		DijkstraAlgorithm algorithm = new DijkstraAlgorithm();
		
		for ( StreetTile startStation : allStationsStart ) {
			if ( startStation.isStation() ) {
				for ( StreetTile endStation : allStationsEnd ) {
					
					Vertex vertexStart = findStationVertex(startStation.getX(), startStation.getY());
					Vertex vertexEnd = findStationVertex(endStation.getX(), endStation.getY());
					
					if (vertexStart != null && vertexEnd != null) { //Nur Weg berechnen, wenn beide Stationen im Graphen als Vertex vorliegen
						PathfindingResult result = algorithm.bestWay(stationGraph, vertexStart, vertexEnd);
						
						if (result.length != PathfindingResult.INFINITY) { //Es muss ein L�sungsweg existieren
							if (bestResult == null) { //Falls noch keine L�sung gefunden wurde, wird diese als beste L�sung betrachtet
								bestResult = result;
							} else { //Es existiert eine beste L�sung 
								if (result.length < bestResult.length) { //Wenn die gerade berechnete L�sung eine bessere L�sung ist als die bisherige beste L�sung
									bestResult = result;
								}
							}
						}
					}
				}
			}
		}
		
		if (bestResult != null) { //Wenn eine L�sung gefunden wurde
			System.out.println("Beste L�sung f�r die Person ist:");
			System.out.println("L�nge: "+bestResult.length);
			System.out.println("Weg: "+Arrays.toString(bestResult.path.toArray()));
			System.out.println(tileStart);
			System.out.println(tileEnd);
			//Dieses Ergebnis muss nun noch in eine Route umgewandelt werden:
			
			for ( int i=0; i<bestResult.path.size()-1;i++ ) {
				Schedule s = ((StreetTile)tiles[bestResult.path.get(i).x][bestResult.path.get(i).y]).getSchedules().get(0);
				System.out.println(s.whichDirectionIsFaster(s.searchWaypoint(bestResult.path.get(i).x, bestResult.path.get(i).y), 
						s.searchWaypoint(bestResult.path.get(i+1).x, bestResult.path.get(i+1).y)));
			}
		}
		
	}
	
	
	/**
	 * Generiert den Graphen, welcher alle Stra�enwegpunkte anzeigt. Jede Station ist auch automatisch ein Stra�enwegpunkt
	 * Daf�r muss die Funktion in <code>Town</code> <code>applyChromosom</code> schon aufgerufen geworden sein.
	 */
	private static void generateWaypointGraph(Tile[][] tiles) {
		waypointGraph = new Graph();
		
		ArrayList<Vertex> allVertexes = new ArrayList<Vertex>();
		//Als erstes alle Vertexes, die existieren, generieren
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof StreetTile) {
					if (isWaypoint(x,y,tiles)) {
						allVertexes.add(new Vertex(x, y));
					}
				}
			}
		}
		
		//Nun die Kanten setzen:
		for (Vertex v : allVertexes) {
			Vertex tmp = null;
			if ( (tmp = findWaypointVertexLeft(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, v.x-tmp.x));
			}
			if ( (tmp = findWaypointVertexRight(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, tmp.x-v.x));
			}
			if ( (tmp = findWaypointVertexTop(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, v.y-tmp.y));
			}
			if ( (tmp = findWaypointVertexBottom(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, tmp.y-v.y));
			}
			
		}
		waypointGraph.vertexes = allVertexes;
	}
	

	/**
	 * Generiert einen Graphen, welcher aus den Wegpunkten und Stationen besteht
	 * @param tiles
	 */
	private static void generateBigGraph(Tile[][] tiles) {
		bigGraphs = new Graph();
		
		ArrayList<Vertex> allVertexes = new ArrayList<Vertex>();
		//Als erstes alle Vertexes, die existieren, generieren
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof StreetTile) {
					if (isWaypoint(x,y,tiles) || ((StreetTile) tiles[x][y]).isStation() ) {
						allVertexes.add(new Vertex(x, y));
					}
				}
			}
		}
		
		//Nun die Kanten setzen:
		for (Vertex v : allVertexes) {
			Vertex tmp = null;
			if ( (tmp = findWaypointStationVertexLeft(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, v.x-tmp.x));
			}
			if ( (tmp = findWaypointStationVertexRight(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, tmp.x-v.x));
			}
			if ( (tmp = findWaypointStationVertexTop(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, v.y-tmp.y));
			}
			if ( (tmp = findWaypointStationVertexBottom(v.x, v.y, tiles, allVertexes)) != null) {
				v.outnodes.add(new VertexEdge(tmp, tmp.y-v.y));
			}
		}
		
		bigGraphs.vertexes = allVertexes;
		
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
	
	
	/**
	 * Gibt den Vertex aus dem Stationengraph zur�ck, welcher auf das Tile t verweist.
	 * Falls der Stationengraph dieses Vertex nicht besitzt, wird <code>null</code> zur�ckgegeben.
	 * 
	 * @see #findStationVertex(int, int)
	 */
	private static Vertex findStationVertex(Tile t) {
		return findStationVertex(t.getX(), t.getY());
	}
	
	/**
	 * Gibt den Vertex aus dem Stationengraph zur�ck, welcher auf das Tile t verweist.
	 * Falls der Stationengraph dieses Vertex nicht besitzt, wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findStationVertex(int x, int y) {
		for (Vertex v : stationGraph.vertexes) {
			if (v.equalTo(x, y)) return v;
		}
		return null;
	}

	/**
	 * Gibt den n�chsten Vertex - Waypoint/Station zur�ck, welcher sich in der Karte links befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointStationVertexLeft(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (x==0) return null;
		while (x!=0) {
			x--;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if ( isStation(x, y, tiles) || isWaypoint(x, y, tiles) ) { //Vertex gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}
	
	/**
	 * Gibt den n�chsten Vertex - Waypoint/Station zur�ck, welcher sich in der Karte rechts befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointStationVertexRight(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (x>=tiles.length) return null;
		while (x<tiles.length-1) {
			x++;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if ( isStation(x, y, tiles) ||isWaypoint(x, y, tiles) ) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}
	
	/**
	 * Gibt den n�chsten Vertex - Waypoint/Station zur�ck, welcher sich in der Karte oben befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointStationVertexTop(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (y==0) return null;
		while (y!=0) {
			y--;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if ( isStation(x, y, tiles) || isWaypoint(x, y, tiles) ) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}

	/**
	 * Gibt den n�chsten Vertex - Waypoint/Station zur�ck, welcher sich in der Karte unten befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointStationVertexBottom(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (y>=tiles[0].length) return null;
		while (y<tiles[0].length-1) {
			y++;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if ( isStation(x, y, tiles) || isWaypoint(x, y, tiles) ) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}
	
	/**
	 * Gibt den n�chsten Vertex/Waypoint zur�ck, welcher sich in der Karte links befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointVertexLeft(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (x==0) return null;
		while (x!=0) {
			x--;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if (isWaypoint(x, y, tiles)) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}

	/**
	 * Gibt den n�chsten Vertex/Waypoint zur�ck, welcher sich in der Karte rechts befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointVertexRight(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (x>=tiles.length) return null;
		while (x<tiles.length-1) {
			x++;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if (isWaypoint(x, y, tiles)) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}
	
	/**
	 * Gibt den n�chsten Vertex/Waypoint zur�ck, welcher sich in der Karte oben befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointVertexTop(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (y==0) return null;
		while (y!=0) {
			y--;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if (isWaypoint(x, y, tiles)) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}

	/**
	 * Gibt den n�chsten Vertex/Waypoint zur�ck, welcher sich in der Karte unten befindet.
	 * Falls es keinen gibt wird <code>null</code> zur�ckgegeben.
	 */
	private static Vertex findWaypointVertexBottom(int x, int y, Tile[][] tiles, ArrayList<Vertex> vertexes) {
		if (y>=tiles[0].length) return null;
		while (y<tiles[0].length-1) {
			y++;
			if (! (tiles[x][y] instanceof StreetTile)) { //Falls zuvor auf eine nicht-Stra�e getroffen wurde, muss abgebrochen werden
				return null;
			}
			if (isWaypoint(x, y, tiles)) { //Waypoint gefunden!
				for (Vertex v : vertexes) {
					if (v.equalTo(x, y)) {
						return v;
					}
				}
				return null; //Waypoint wurde nicht in Vertexliste gefunden
			}
		}
		return null;
	}
	
	
	/**
	 * Gibt an, ob die Koordinate ein Wegpunkt, also eine Kurve/Kreuzung ist.
	 */
	private static boolean isWaypoint(int x, int y, Tile[][] tiles) {
		boolean top = false, bottom = false, left = false, right = false;
		//Links:
		if (x>0) {
			if (tiles[x-1][y] instanceof StreetTile) left = true;
		}
		//Rechts:
		if (x+1<tiles.length) {
			if (tiles[x+1][y] instanceof StreetTile) right = true;
		}
		//Oben:
		if (y>0) {
			if (tiles[x][y-1] instanceof StreetTile) top = true;
		}
		//Unten:
		if (y+1<tiles[0].length) {
			if (tiles[x][y+1] instanceof StreetTile) bottom = true;
		}
		return (left&&top || top&&right || right&&bottom || bottom&&left);
	}
	
	/**
	 * Gibt an, ob die Koordinate eine Station ist.
	 */
	private static boolean isStation(int x, int y, Tile[][] tiles) {
		if (tiles[x][y] instanceof StreetTile) {
			if ( ((StreetTile)tiles[x][y]).isStation() ) return true;
		}
		return false;
	}
}
