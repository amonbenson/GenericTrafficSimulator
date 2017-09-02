package com.trafficsim.town;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.sim.Simulation;

public class Town implements Updateable {
	

	
	
	
	private Tile[][] tiles; //Die Karte der Start
	private int sizeX, sizeY; //Größe der Stadt
	private Chromosom chromosom; //Chromosom, welches auf diese Stadt angewendet wurde.
	private ArrayList<Event> events; //Liste aller Events

	private int events_index; //aktuelle Position der Events
	private ArrayList<Bus> busses; //alle Busse der Stadt
	private ArrayList<Person> persons; //alle Personen der Stadt
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so können bestimmte Szenarien erneut simuliert werden)
	
	private GridCell[][] pathfindingMap = null; //die aktuelle Pathfindingkarte, wird bei applyChromosom erzeugt
	//create a navigation grid with the cells you just created
	private NavigationGrid<GridCell> navGrid = null;
	
	//or create your own pathfinder options:
	private GridFinderOptions opt = null;
	
	AStarGridFinder<GridCell> finder = null;
	
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
		persons = new ArrayList<Person>();
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
	 * list[x][y][0] = Typ des Tiles, 0 ist Straße, 1 ist Haus
	 * list[x][y][1] = Wert des Tiles, bei einer Straße die maximale Geschwindigkeit, bei einem Haus die Anzahl der Personen 
	 *
	 */
	public void generateTiles(float[][][] list) {
		if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Größe X / Y muss größer als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Straße
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
				} else {
					Simulation.logger.warning("Liste["+x+"]["+y+"][0] ist kein gültiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
	}

	public ArrayList<Bus> getBusses() {
		return busses;
	}
	
	public ArrayList<Person> getPersons() {
		return persons;
	}
	
	public long getTime() {
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
	 * Gibt alle StreetTiles zurück.
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
	 * Gibt ein zufälliges Tile zurück.
	 */
	public Tile getRandomTile() {
		return tiles[random.nextInt(tiles.length)][random.nextInt(tiles[0].length)];
	}

	/**
	 * Gibt ein zufälliges Tile, exklusive dem Parameter, zurück.
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
	 * Initialisiert die Objekte, welche vor dem Start der Simulation initialisiert werden müssen
	 */
	private void init() {
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
	 * Setzt das Chromosom c als Eingabewert der Simulation. Dieses wird noch nicht angewendet, d.h. die Stadt wird bisher nicht verändert.
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
			//Pathfindingkarte erzeugen:
			pathfindingMap = new GridCell[sizeX][sizeY];

			for (int x=0;x<sizeX;x++) {
				for (int y=0;y<sizeY;y++) {
					boolean walkable = false;
					if (tiles[x][y] instanceof StreetTile) {
						walkable = true;
					}
					pathfindingMap[x][y] = new GridCell(x, y, walkable);
				}
			}
			navGrid = new NavigationGrid(pathfindingMap);
			opt = new GridFinderOptions();
			opt.allowDiagonal = false;
			finder = new AStarGridFinder(GridCell.class, opt);
			
			//Bushaltestellen setzen:
			for ( Point p : chromosom.getStations() ) {
				if (! (tiles[p.x][p.y] instanceof StreetTile) ) {
					Simulation.logger.warning("Achtung, Station im Chromosom verweist nicht auf eine gültige Straßenkoordinate!"+" X: "+p.x+" Y: "+p.y);
				} else { //korrekte Koordinate
					StreetTile street = (StreetTile) tiles[p.x][p.y];
					street.setToStation();
				}
			}
			//Buslinien einfügen:
			for ( Schedule schedule : chromosom.getSchedules()) {
				schedule.calcWaypoints(this); //kreiert die internen Wegpunkte TODO eventuell muss auf eine Kopie zugegriffen werden
				events.addAll(schedule.getBusCreationEvents(this));
				//Jede Station muss wissen, dass hier diese Linie fährt:
				for ( Waypoint w : schedule.getStations() ) {
					if (!(tiles[(int) (w.getX())][(int) (w.getY())] instanceof StreetTile)) {
						Simulation.logger.warning("Busstation verweist nicht auf eine Straße");
					} else {
						StreetTile station = (StreetTile) tiles[(int) (w.getX())][(int) (w.getY())];
						if (!station.hasSchedule(schedule)) { //Diese Linie nur hinzufügen, wenn diese noch nicht existiert
							station.addSchedule(schedule);
						}
					}
				}
			}
			
			RoutingAlgorithm.init(tiles);
			
			//Generiert die Routen für die Menschen:
			generateRoutings();
			
			//Sortiert schließlich die fertige Eventliste:
			sortEvents();
			

		}
	}
	
	/**
	 * Updatet die Stadt einen Tick. Die Zeit wird mitgerechnet.
	 */
	public void update() {
		//Alle Busse müssen fahren: (geupdatet werden)
		for (Bus b : busses) {
			b.update();
		}
		//Es wird geprüft, ob ein Event auftritt
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
	 * Simuliert die Stadt um einen Schritt rückwärts. Die Zeit wird um 1 abgezogen.
	 */
	public void revert() {
		for (Bus b : busses) {
			b.revert();
		}
		time--;
	}
	
	/**
	 * Wandelt die Stadt wieder in den ursprünglichen Zustand um und löscht das aktuelle Chromosom
	 * @see #toNormal()
	 */
	public void resetChromosom() {
		chromosom = null;
		toNormal();
	}
	
	/**
	 * Wandelt die Stadt wieder in die Ursprüngliche um, alle Änderungen eines angewendeten Chromosoms werden rückgängig gemacht.
	 * Löscht außerdem die bisherigen Events
	 */
	public void toNormal() {
		//Löscht aus allen Straßen die möglichen Stationen
		for (int x = 0; x < getSizeX(); x++) {
			for (int y = 0; y < getSizeY(); y++) {
				if (tiles[x][y] instanceof StreetTile) {
					((StreetTile) tiles[x][y]).setToStreet();
				}
			}
		}
		events.clear();
		pathfindingMap = null;
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
	 * 
	 * TODO richtigen Algorithmus einbauen
	 * Generiert Routen für die Person p und fügt diese der Liste list hinzu
	 * @param p die Person, auf welche sich die Routen beziehen
	 * @param list die Liste
	 */
	private void generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
		if (random.nextBoolean()) {
			StreetTile origin = (StreetTile) tiles[0][0];
			Tile target = tiles[5][3];
			ArrayList<ChangeStation> stations = new ArrayList<ChangeStation>();		
			stations.add(new ChangeStation(new Waypoint(0,0), origin.getSchedules().get(0).getScheduleNormal()));
			stations.add(new ChangeStation(new Waypoint(3,3), ((StreetTile) tiles[5][3]).getSchedules().get(0).getScheduleNormal()));
			stations.add(new ChangeStation(new Waypoint(5,3), ((StreetTile) tiles[5][3]).getSchedules().get(0).getScheduleNormal()));
			Route r = new Route(origin, target, stations);
			RoutingEvent re = new RoutingEvent(15, p, r);
			list.add(re);
		} else {
			StreetTile origin = (StreetTile) tiles[0][0];
			Tile target = tiles[0][5];
			ArrayList<ChangeStation> stations = new ArrayList<ChangeStation>();		
			stations.add(new ChangeStation(new Waypoint(0,0), origin.getSchedules().get(0).getScheduleNormal()));
			stations.add(new ChangeStation(new Waypoint(3,3), ((StreetTile) tiles[5][3]).getSchedules().get(0).getScheduleNormal()));
			stations.add(new ChangeStation(new Waypoint(0,5), ((StreetTile) tiles[5][3]).getSchedules().get(0).getScheduleNormal()));
			Route r = new Route(origin, target, stations);
			RoutingEvent re = new RoutingEvent(15, p, r);
			list.add(re);
		}
		
		/*
		int warning_counter = 0;
		StreetTile origin = null;
		Tile target = null;
		Waypoint stationTarget = null;
		ArrayList<ChangeStation> stations = null;
		
		main:
		while (true) {
			if (warning_counter > 1000) {
				break main;
			}
			
			origin = p.getHouse().getAllNextStations(tiles).get(random.nextInt(p.getHouse().getAllNextStations(tiles).size()));
			target = tiles[tiles.length-1][tiles[0].length-1];
			for ( Schedule s : origin.getSchedules() ) {
				if (s.canGetToTarget(target)) {
					stations = new ArrayList<ChangeStation>();
					stations.add(new ChangeStation(new Waypoint(origin.getX(), origin.getY()), new SpecificSchedule(s, BusDirection.REVERSE)));
					stations.add(new ChangeStation(s.whichStationIsInArea(target.getX(), target.getY()), new SpecificSchedule(s, BusDirection.REVERSE)));
					if (stations.get(0).getStation().isSame(stations.get(1).getStation())) {
						continue;
					}

					break main;
				}
			}
			

			
			warning_counter++;
		}
		

		try {
		Route route = new Route(origin, target, stations);
		
		RoutingEvent event = new RoutingEvent(random.nextInt(10), p, route);
		list.add(event);
		} catch (Exception e) { }
		*/
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
	
	public GridCell[][] getPathfindingMap() {
		return pathfindingMap;
	}
	
	public List<GridCell> findPath(int x1, int y1, int x2, int y2) {
		return finder.findPath(x1, y1, x2, y2, navGrid);
	}
}
