package com.trafficsim.town;

import java.util.ArrayList;

/**
 * Kümmert sich um den Wegfindungsalgorithmus, welcher den besten Weg für eine Route von Menschen findet
 * @author Luca
 *
 */
public class RoutingAlgorithm {
	
	private static Tile[][] tiles;
	
	public static void init(Tile[][] tiles) {
		RoutingAlgorithm.tiles = tiles;
	}
	
	public static void generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
	}
	
	
}
