package com.trafficsim.town;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Logger;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.generic.Schedule;
import com.trafficsim.sim.Simulation;

public class Town {
	



	public static Logger logger = Logger.getGlobal();
	
	
	
	private Tile[][] tiles; //Die Karte der Start
	private int sizeX, sizeY; //Gr��e der Stadt
	private Chromosom chromosom; //Chromosom, welches auf diese Stadt angewendet wurde.
	private ArrayList<Event> events; //Liste aller Events

	private int events_index; //aktuelle Position der Events
	private ArrayList<Bus> busses; //alle Busse der Stadt
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so k�nnen bestimmte Szenarien erneut simuliert werden)
	
	public Town(int sizeX, int sizeY) {
		this(sizeX, sizeY, null, null, new Random());
	}
	
	public Town(int sizeX, int sizeY, Tile[][] tiles, Chromosom chromosom, Random random) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.tiles = tiles;
		this.chromosom = chromosom;
		this.random = random;
		
		events = new ArrayList<Event>();
		busses = new ArrayList<Bus>();
		time = 0;
	}
	
	/**
	 * Gibt an, ob die Tiles bereits erzeugt wurden.
	 */
	public boolean areTilesReady() {
		return (tiles != null)?true:false;
	}

	
	/**
	 * Erzeugt die Tiles anhand eines dreidimensionalen Arrays.
	 * Diese ist wie folgt aufgebaut:
	 *
	 * list[x][y][0] = Typ des Tiles, 0 ist Stra�e, 1 ist Haus
	 * list[x][y][1] = Wert des Tiles, bei einer Stra�e die maximale Geschwindigkeit, bei einem Haus die Anzahl der Personen 
	 *
	 */
	public void generateTiles(float[][][] list) {
		if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Gr��e X / Y muss gr��er als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Stra�e
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein g�ltiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
	}

	public ArrayList<Bus> getBusses() {
		return busses;
	}
	
	public long getTime() {
		return time;
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
	
	/**
	 * Gibt alle StreetTiles zur�ck.
	 */
	public ArrayList<StreetTile> getStreetTiles() {
		if (!areTilesReady()) throw new NullPointerException("Tiles aren't ready");
		
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
	 * Gibt ein zuf�lliges Tile zur�ck.
	 */
	public Tile getRandomTile() {
		return tiles[random.nextInt(tiles.length)][random.nextInt(tiles[0].length)];
	}

	/**
	 * Gibt ein zuf�lliges Tile, exklusive dem Parameter, zur�ck.
	 * Exklusives Item wird hier immer mithilfe des Pointers betrachtet, nicht nach Inhalt.
	 */
	public Tile getRandomTileWithExclude(Tile exclude) {
		Tile back=null;
		while ( (back=getRandomTile()) == exclude ) {
			
		}
		return back;
	}
	
	public ArrayList<Event> getEvents() {
		return events;
	}

	public int getSizeX() {
		return sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Chromosom getChromosom() {
		return chromosom;
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
	


	


	/**
	 * Initialisiert die Objekte, welche vor dem Start der Simulation initialisiert werden m�ssen
	 */
	public void init() {
		for (Bus b : busses) {
			b.init();
		}
	}


	
	
	public Random getRandom() {
		return random;
	}


	public void setRandom(Random random) {
		this.random = random;
	}
	
	
	/**
	 * Setzt das Chromosom c als Eingabewert der Simulation. Dieses wird noch nicht angewendet, d.h. die Stadt wird bisher nicht ver�ndert.
	 * Der Parameter darf nicht <code>null</code>sein, zum Reseten der Stadt wird die Funktion resetChromosom() verwendet
	 * @param c Chromosom zur Eingabe, darf nicht <code>null</code> sein
	 */
	public void setChromosom(Chromosom c) {
		if (c == null) throw new NullPointerException("Chromosom can't be null.");
		this.chromosom = c;
	}
	
	/**
	 * Wendet das aktuelle Chromosom auf die Stadt an, falls diese existiert. Ansonsten wird nichts gemacht.
	 */
	public void applyChromosom() {
		if (chromosom != null) {
			toNormal(); //Alles auf Anfang setzen
			//Bushaltestellen setzen:
			for ( Point p : chromosom.getStations() ) {
				if (! (tiles[p.x][p.y] instanceof StreetTile) ) {
					logger.warning("Achtung, Station im Chromosom verweist nicht auf eine g�ltige Stra�enkoordinate!"+" X: "+p.x+" Y: "+p.y);
				} else { //korrekte Koordinate
					StreetTile street = (StreetTile) tiles[p.x][p.y];
					street.setToStation();
				}
			}
			//Buslinien einf�gen:
			for ( Schedule schedule : chromosom.getSchedules()) {
				schedule.calcWaypoints(tiles); //kreiert die internen Wegpunkte
				events.addAll(schedule.getBusCreationEvents());
			}
			
			//Generiert die Routen f�r die Menschen:
			generateRoutings();
			
			//Sortiert schlie�lich die fertige Eventliste:
			sortEvents();
			
		}
	}
	
	/**
	 * Updatet die Stadt einen Tick. Die Zeit wird mitgerechnet.
	 */
	public void update() {
		//Alle Busse m�ssen fahren: (geupdatet werden)
		for (Bus b : busses) {
			b.update(this);
		}
		//Es wird gepr�ft, ob ein Event auftritt
		for (int i=events_index;i<events.size();i++) { 
			if (events.get(i).getStartTime() == time) {
				events.get(i).start(this);
				events_index++;
			} else {
				break;
			}
		}
		time++;
	}
	
	/**
	 * Simuliert die Stadt um einen Schritt r�ckw�rts. Die Zeit wird um 1 abgezogen.
	 */
	public void revert() {
		for (Bus b : busses) {
			b.revert(this);
		}
		time--;
	}
	
	/**
	 * Wandelt die Stadt wieder in den urspr�nglichen Zustand um und l�scht das aktuelle Chromosom
	 * @see #toNormal()
	 */
	public void resetChromosom() {
		chromosom = null;
		toNormal();
	}
	
	/**
	 * Wandelt die Stadt wieder in die Urspr�ngliche um, alle �nderungen eines angewendeten Chromosoms werden r�ckg�ngig gemacht
	 */
	public void toNormal() {
		//L�scht aus allen Stra�en die m�glichen Stationen
		for (int x = 0; x < getSizeX(); x++) {
			for (int y = 0; y < getSizeY(); y++) {
				if (tiles[x][y] instanceof StreetTile) {
					((StreetTile) tiles[x][y]).setToStreet();
				}
			}
		}
	}
	
	
	

	
	/**
	 * Generiert alle RoutingEvents f�r die aktuelle Simulation.
	 */
	private void generateRoutings() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		//Generiert f�r jede Person RoutingEvents:
		ArrayList<HouseTile> houses = getHouseTiles();
		ArrayList<Event> tmpEvents = new ArrayList<Event>();
		for ( HouseTile house : houses ) {
			for ( int i=0;i<house.getNumberPersons();i++) {
				Person p = new Person(house);
				generateRoutingForPerson(p, tmpEvents);
			}
		}
		//RoutingEvents hinzuf�gen:
		events.addAll(tmpEvents);
	}
	
	
	/**
	 * Generiert Routen f�r die Person p und f�gt diese der Liste list hinzu
	 * @param p die Person, auf welche sich die Routen beziehen
	 * @param list die Liste
	 */
	private void generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
		for ( int i=0;i<6;i++ ) { //TODO vern�ftigen Zeitrythmus f�r einen Menschen finden
			long startTime = random.nextInt(TimeHelper.MINUTE) + i*TimeHelper.DAY;
			Route route = new Route(p.getHouse(), getRandomTileWithExclude(p.getHouse()), p);
			RoutingEvent event = new RoutingEvent(startTime, route);
			list.add(event);
			Route routeBack = new Route(route.getTarget(), route.getOrigin(), p);
			RoutingEvent eventBack = new RoutingEvent(startTime+TimeHelper.HOUR*8, routeBack);
			list.add(eventBack);
		}
	}
	
	/**
	 * Sortiert die interne Liste <code>events</code> nach der Beginnzeit
	 */
	private void sortEvents() {
		Collections.sort(events, new Comparator<Event>() {
	        public int compare(Event o1, Event o2) {
	        	return Long.compare(o1.getStartTime(), o2.getStartTime());
	        }
	    });
	}
	
	
}
