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
	private int sizeX, sizeY; //Gr��e der Stadt
	private ArrayList<RoutingEvent> routingEvents; //Liste aller Events
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so k�nnen bestimmte Szenarien erneut simuliert werden)
	
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
	 * list[x][y][0] = Typ des Tiles, 0 ist Stra�e, 1 ist Haus
	 * list[x][y][1] = Wert des Tiles, bei einer Stra�e die maximale Geschwindigkeit, bei einem Haus die Anzahl der Personen 
	 *
	 */
	public void generateTiles(float[][][] list ) {
		if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Gr��e X / Y muss gr��er als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Stra�e
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
					System.out.println("Street");
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
					System.out.println("house");
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein g�ltiger Typ! ("+list[x][y][0]+")");
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
	 * Gibt alle StreetTiles zur�ck.
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
	 * Gibt alle HouseTiles zur�ck.
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

	/**
	 * Generiert alle RoutingEvents f�r die aktuelle Simulation.
	 */
	public void generateRoutings() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		//Generiert f�r jede Person RoutingEvents:
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
		
		//RoutingEvents setzen:
		routingEvents = tmpEvents;
	}
	/**
	 * Generiert Routen f�r die Person p und f�gt diese der Liste list hinzu
	 * @param p die Person, auf welche sich die Routen beziehen
	 * @param list die Liste
	 */
	private void generateRoutingForPerson(Person p, ArrayList<RoutingEvent> list) {
		
		for ( int i=0;i<6;i++ ) { //TODO vern�ftigen Zeitrythmus f�r einen Menschen finden
			long startTime = random.nextInt(TimeHelper.DAY) + i*TimeHelper.DAY;
			Route route = new Route(p.getHouse(), getRandomTileWithExclude(p.getHouse()), p);
			RoutingEvent event = new RoutingEvent(startTime, route);
			list.add(event);
			Route routeBack = new Route(route.getTarget(), route.getOrigin(), p);
			RoutingEvent eventBack = new RoutingEvent(startTime+TimeHelper.HOUR*8, routeBack);
			list.add(eventBack);
		}
		
	}
	
	/**
	 * Gibt ein zuf�lliges Tile, exklusive dem Parameter, zur�ck.
	 * Exklusives Item wird hier immer mithilfe des Pointers betrachtet, nicht nach Inhalt.
	 * 
	 */
	private Tile getRandomTileWithExclude(Tile exclude) {
		Tile back=null;
		while ( (back=getRandomTile()) == exclude ) {
			
		}
		return back;
	}
	/**
	 * Gibt ein zuf�lliges Tile zur�ck.
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
