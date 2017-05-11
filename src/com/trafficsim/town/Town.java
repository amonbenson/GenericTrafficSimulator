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

public class Town implements Updateable, Initable {
	



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
	private Chromosom chromosom; //Chromosom, welches auf diese Stadt angewendet wurde.
	private ArrayList<Event> events; //Liste aller Events

	private int events_index; //aktuelle Position der Events
	private ArrayList<Bus> busses; //alle Busse der Stadt
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so können bestimmte Szenarien erneut simuliert werden)
	
	public Town(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		events = new ArrayList<Event>();
		busses = new ArrayList<Bus>();
		time = 0;
		random = new Random();
	}
	
	
	/**
	 * Gibt an, ob die Tiles bereits erzeugt wurden.
	 */
	public boolean areTilesReady() {
		if (tiles == null) return false;
		else return true;
	}
	
	/**
	 * Generiert Routen für die Person p und fügt diese der Liste list hinzu
	 * @param p die Person, auf welche sich die Routen beziehen
	 * @param list die Liste
	 */
	private void generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
		for ( int i=0;i<6;i++ ) { //TODO vernüftigen Zeitrythmus für einen Menschen finden
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
	 * Generiert alle RoutingEvents für die aktuelle Simulation.
	 */
	private void generateRoutings() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		//Generiert für jede Person RoutingEvents:
		ArrayList<HouseTile> houses = getHouseTiles();
		ArrayList<Event> tmpEvents = new ArrayList<Event>();
		for ( HouseTile house : houses ) {
			for ( int i=0;i<house.getNumberPersons();i++) {
				Person p = new Person(house);
				generateRoutingForPerson(p, tmpEvents);
			}
		}
		//RoutingEvents hinzufügen:
		events.addAll(tmpEvents);
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
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein gültiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
	}
	
	
	
	public ArrayList<Bus> getBusses() {
		return busses;
	}
	

	
	public long getCurrentTime() {
		return time;
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
	
	
	/**
	 * Gibt ein zufälliges Tile zurück.
	 */
	private Tile getRandomTile() {
		return tiles[random.nextInt(tiles.length)][random.nextInt(tiles[0].length)];
	}

	/**
	 * Gibt ein zufälliges Tile, exklusive dem Parameter, zurück.
	 * Exklusives Item wird hier immer mithilfe des Pointers betrachtet, nicht nach Inhalt.
	 * 
	 */
	private Tile getRandomTileWithExclude(Tile exclude) {
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
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public Chromosom getChromosom() {
		return chromosom;
	}
	
	public void setBusses(ArrayList<Bus> busses) {
		this.busses = busses;
	}

	public void setCurrentTime(int time) {
		this.time = time;
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
	 * Setzt das Chromosom c als Eingabewert der Simulation.
	 * @param c Chromosom zur Eingabe, wenn dieser <code>null</code> ist, wird die Stadt wieder ursprünglich gemacht
	 */
	public void setChromosom(Chromosom chromosom) {
		if (chromosom != null) {
			reset();
			//Bushaltestellen setzen:
			for ( Point p : chromosom.getStations() ) {
				if (! (tiles[p.x][p.y] instanceof StreetTile) ) {
					logger.warning("Achtung, Station im Chromosom verweist nicht auf eine gültige Straßenkoordinate!"+" X: "+p.x+" Y: "+p.y);
				} else { //korrekte Koordinate
					StreetTile street = (StreetTile) tiles[p.x][p.y];
					street.setToStation();
				}
			}
			
			//Buslinien einfügen:
			for ( Schedule schedule : chromosom.getSchedules()) {
				schedule.calcWaypoints(tiles); //kreiert die internen Wegpunkte
				events.addAll(schedule.getBusCreationEvents());
			}
			
			//Generiert die Routen:
			generateRoutings();
			
			//Sortiert schließlich die fertige Eventliste:
			sortEvents();
			
		} else { //nur einen Reset
			reset();
		}
	}
	
	public void reset() {
		for (int x = 0; x < getSizeX(); x++) {
			for (int y = 0; y < getSizeY(); y++) {
				if (tiles[x][y] instanceof StreetTile) {
					((StreetTile) tiles[x][y]).setToStreet();
				}
			}
		}
	}
	
	/**
	 * Updatet die Stadt einen Tick. Die Zeit wird mitgerechnet
	 */
	public void update() {
		for (Bus b : busses) {
			b.update();
		}
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
	 * Initialisiert die Objekte, welche vor dem Start der Simulation initialisiert werden müssen
	 */
	public void init() {
		for (Bus b : busses) {
			b.init();
		}
	}

	/**
	 * Simuliert die Stadt um einen Schritt rückwärts. Die Zeit wird um 1 abgezogen.
	 */
	public void revert() {
		for (Bus b : busses) {
			b.revert();
		}
		time--;
	}
	
	
	public Random getRandom() {
		return random;
	}


	public void setRandom(Random random) {
		this.random = random;
	}
	
}
