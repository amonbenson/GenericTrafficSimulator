package com.trafficsim.town;

import java.util.ArrayList;

public abstract class Tile {
	private int x, y; //Koordinaten
	
	//in welchem quadratischen Bereich die Person eine Station finden kann, bei 1 betr�gt diese 3x3, bei 2 5x5 usw
	public static int AREA_STATION=2;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	

	

	/**
	 * Gibt die n�chste beste Station (mit den meisten Buslinien) zur�ck
	 * @param map
	 * @return die "beste" n�chste Station, wenn keine vorhanden ist <code>null</code>
	 */
	public StreetTile getNextStation(Tile[][] map) {
		ArrayList<StreetTile> all = getAllNextStations(map);
		StreetTile best = null;
		int numberLines = 1;
		int distance = 9;
		for (StreetTile t : all) {
			if ( (Math.abs(t.getX()-x)+Math.abs(t.getY()-y)) < distance &&
				t.getSchedules().size() >= numberLines) {
				numberLines = t.getSchedules().size();
				best = t;
				distance = Math.abs(t.getX()-x)+Math.abs(t.getY()-y);
			}
			if (t.getSchedules().size() > numberLines) {
				numberLines = t.getSchedules().size();
				best = t;
				distance = Math.abs(t.getX()-x)+Math.abs(t.getY()-y);
			}
			
		}
		return best;
	}
	
	/**
	 * Gibt alle verf�gbaren Stationen dieser Person zur�ck.
	 * Falls es keine gibt, wird <code>null</code>zur�ckgegeben.
	 * @param map die Karte der Stadt
	 */
	public ArrayList<StreetTile> getAllNextStations(Tile[][] map) {
		ArrayList<StreetTile> back = new ArrayList<StreetTile>();
		for (int x2=x-AREA_STATION;x2<=x+AREA_STATION;x2++) {
			for (int y2=y-AREA_STATION;y2<=y+AREA_STATION;y2++) {
				if (x2 >= 0 && x2 < map.length && y2 >= 0 && y2 < map[0].length ) { //Koordinaten m�ssen im Bereich liegen
					if (map[x2][y2] instanceof StreetTile) {
						if (((StreetTile) map[x2][y2]).isStation()) {
							back.add((StreetTile)map[x2][y2]);
						}
					}
				}
			}
		}
		return back;
	}
	
	public Waypoint toWaypoint() {
		return new Waypoint(x, y);
	}
	
	public Waypoint toWaypointWithOffset() {
		return new Waypoint(x+0.5, y+0.5);
	}
	
	@Override
	public String toString() {
		return "X: "+x+" Y:"+y;
	}
}
