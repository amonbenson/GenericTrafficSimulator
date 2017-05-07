package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Logger;

import com.trafficsim.sim.Simulation;

public class Town {
	
	public static Logger logger = Logger.getGlobal();
	
	//Test
	//TODO entfernen
	public static void main(String[] args) {
		Town t = new Town(2,2);
		Tile[][] tiles = new Tile[2][2];
		tiles[0][0] = new StreetTile(0,0, 5f);
		tiles[0][1] = new StreetTile(0, 1, 2f);
		tiles[1][0] = new HouseTile(1, 0, 5);
		tiles[1][1] = new HouseTile(1, 1, 10);
		t.tiles = tiles;
		Simulation s = new Simulation(t);
		s.startSimulation();
	}
	
	
	private Tile[][] tiles = null; //Die Karte der Start
	private int sizeX, sizeY; //Größe der Stadt
	private ArrayList<RoutingEvent> routingEvents; //Liste aller Events
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so können bestimmte Szenarien erneut simuliert werden)
	
	public Town(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		random = new Random();
	}
	
	public void addCurrentTime() {
		time++;
	}
	
	/**
	 * Gibt an, ob die Tiles bereits erzeugt wurden.
	 */
	public boolean areTilesReady() {
		if (tiles == null) return false;
		else return true;
	}
	
	/**
	 * Erzeugt die Tiles anhand eines dreidimensionalen Arrays.
	 * Diese ist wie folgt aufgebaut:
	 *
	 * list[x][y][0] = Typ des Tiles, 0 ist Straße, 1 ist Haus
	 * list[x][y][1] = Wert des Tiles, bei einer Straße die maximale Geschwindigkeit, bei einem Haus die Anzahl der Personen 
	 *
	 */
	public void generateTiles(float[][][] list ) {
		if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Größe X / Y muss größer als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Straße
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
					System.out.println("Street");
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
					System.out.println("house");
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein gültiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	

	
	/**
	 * Gibt alle StreetTiles zurück.
	 */
	public ArrayList<StreetTile> getStreetTiles() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		
		ArrayList<StreetTile> back = new ArrayList<StreetTile>();
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof StreetTile) {
					back.add((StreetTile)tiles[x][y]);
				}
			}
		}	
		return back;
	}
	
	/**
	 * Gibt alle HouseTiles zurück.
	 */
	public ArrayList<HouseTile> getHouseTiles() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		
		ArrayList<HouseTile> back = new ArrayList<HouseTile>();
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof HouseTile) {
					back.add((HouseTile)tiles[x][y]);
				}
			}
		}	
		return back;
	}
	
	
	public Tile[][] getTiles() {
		return tiles;
	}

	public long getCurrentTime() {
		return time;
	}
	
	public void setCurrentTime(int time) {
		this.time = time;
	}

	public void generateRoutings() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		ArrayList<HouseTile> houses = getHouseTiles();
		ArrayList<RoutingEvent> tmpEvents = new ArrayList<RoutingEvent>();
		for ( HouseTile house : houses ) {
			for ( int i=0;i<house.getNumberPersons();i++) {
				Person p = new Person(house);
				generateRoutingForPerson(p, tmpEvents);
			}
		}
		//Nun alle RoutingEvents nach der Beginnzeit sortieren:
		Collections.sort(tmpEvents, new Comparator<RoutingEvent>() {
	        public int compare(RoutingEvent o1, RoutingEvent o2) {
	        	return Long.compare(o1.getStartTime(), o2.getStartTime());
	        }
	    });
		
		routingEvents = tmpEvents;
	}
	
	private void generateRoutingForPerson(Person p, ArrayList<RoutingEvent> list) {
		
		for ( int i=0;i<6;i++ ) { //TODO vernüftigen Zeitrythmus für einen Menschen finden
			long startTime = random.nextInt(TimeHelper.DAY) + i*TimeHelper.DAY;
			Route route = new Route(p.getHouse(), getRandomTileWithExclude(p.getHouse()), p);
			RoutingEvent tmp = new RoutingEvent(startTime, route);
			list.add(tmp);
		}
		
	}
	
	/**
	 * Gibt ein zufälliges Tile, exklusive dem Parameter, zurück.
	 * Exklusives Item wird hier immer mithilfe des Pointers betrachtet, nicht nach Inhalt.
	 * 
	 */
	private Tile getRandomTileWithExclude(Tile exclude) {
		Tile back=null;
		while ( (back=getRandomTile()) != exclude ) {
			
		}
		return back;
	}
	/**
	 * Gibt ein zufälliges Tile zurück.
	 */
	private Tile getRandomTile() {
		return tiles[random.nextInt(tiles.length)][random.nextInt(tiles[0].length)];
	}
	
	
	/**
	 * Setzt alle Tiles einer Stadt, erstmal zum debuggen, wird eventuell hinterher wieder entfernt
	 * 
	 * TODO entfernen
	 * 
	 * @param tiles
	 * 			Die neuen Tiles.
	 */
	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public ArrayList<RoutingEvent> getRoutingEvents() {
		return routingEvents;
	}
	
	
}
