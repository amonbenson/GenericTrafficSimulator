package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.genericalgorithm.FrameLauncher;
import com.trafficsim.sim.Simulation;

public class Town implements Updateable {
	
	private Statistics statistics;
	
	private Tile[][] tiles; //Die Karte der Start
	private int sizeX, sizeY; //Gr��e der Stadt
	private Blueprint blueprint; //Blueprint, welches auf diese Stadt angewendet wurde.
	private ArrayList<Event> events; //Liste aller Events

	private int events_index; //aktuelle Position der Events
	private ArrayList<Bus> busses; //alle Busse der Stadt
	private ArrayList<Person> persons; //alle Personen der Stadt
	private long time; //aktuelle Zeit der Stadt
	private int allNumberOfPersons; //Die zuletzt berechnete Gesamtanzahl an Personen
	private float allInterestFactor; //Der zuletzt berechnete Faktor aller Interessenfaktoren aller H�user
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so k�nnen bestimmte Szenarien erneut simuliert werden)
	/**
	 * Einstellung f�r das Erzeugung von Routen von Personen.
	 * @see PersonRoutingOptions.getDefault
	 */
	private PersonRoutingOption personRoutingOption;
	
	//Wegfindungszeug f�r Busse:
	private GridCell[][] pathfindingMap = null; //die aktuelle Pathfindingkarte, wird bei applyBlueprint erzeugt
	private NavigationGrid<GridCell> navGrid = null;
	private GridFinderOptions opt = null;
	AStarGridFinder<GridCell> finder = null;
	
	//Hilfsarray f�r generateRoutingForPerson, wird in applyBlueprint erzeugt
	private ArrayList<Tile> tilesNearStations;
	//Wegfindungszeug f�r Personen:
	private SimpleDirectedWeightedGraph<Waypoint, DefaultWeightedEdge> stationGraph = null;
	
	public Town(int sizeX, int sizeY) {
		this(sizeX, sizeY, null, null, new Random());
	}
	
	public Town(int sizeX, int sizeY, PersonRoutingOption p) {
		this(sizeX, sizeY, null, null, new Random(), p);
	}
	
	public Town(int sizeX, int sizeY, Random random) {
		this(sizeX, sizeY, null, null, random);
	}
	
	public Town(int sizeX, int sizeY, Random random, PersonRoutingOption p) {
		this(sizeX, sizeY, null, null, random, p);
	}
	
	public Town(int sizeX, int sizeY, Tile[][] tiles, Blueprint blueprint, Random random) {
		this(sizeX, sizeY, tiles, blueprint, random, PersonRoutingOption.getDefault());
	}
	
	public Town(int sizeX, int sizeY, Tile[][] tiles, Blueprint blueprint, Random random, PersonRoutingOption p) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.tiles = tiles;
		this.blueprint = blueprint;
		this.random = random;
		this.personRoutingOption = p;
		
		events = new ArrayList<Event>();
		busses = new ArrayList<Bus>();
		persons = new ArrayList<Person>();
		time = 0;
		allInterestFactor = Float.NaN;
		allNumberOfPersons = -1;
		
		statistics = new Statistics();
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
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1], list[x][y][2]);
				} else {
					Simulation.logger.warning("Liste["+x+"]["+y+"][0] ist kein g�ltiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
		calcAllInterest();
		calcAllNumberOfPersons();
	}

	public ArrayList<Bus> getBusses() {
		return busses;
	}
	
	public ArrayList<Person> getPersons() {
		return persons;
	}
	
	public PersonRoutingOption getPersonRoutingOption() {
		return personRoutingOption;
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
	 * Gibt ein zuf�lliges Tiles zur�ck, welches in der N�he einer Station ist.
	 */
	public Tile getRandomTileNearStation() {
		return tilesNearStations.get(random.nextInt(tilesNearStations.size()));
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
	
	/**
	 * Gibt ein Haus zur�ck, wobei ein Haus mit h�herer Anzahl an numberPersons h�ufiger zur�ckgegeben wird.
	 */
	public Tile getHouseTileWithProbability_NumberPersons() {
		float whichTile = random.nextFloat()*getLastAllNumberOfPersons();
		float probabilityCounter = 0;
		Tile selected = null;
		a:
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof HouseTile) {
					float probability = ((HouseTile)tiles[x][y]).getNumberPersons();
					probabilityCounter += probability;
					if (whichTile <= probabilityCounter) {
						selected = tiles[x][y];
						break a;
					}
					selected = tiles[x][y];
				}
			}
		}
		if (selected == null) {
			Simulation.logger.warning("getHouseTileWithProbability_NumberPersons hat nichts gefunden! Komischer Fehler, untersuchen.");
		}
		return selected;
	}
	

	public Tile getHouseTileWithProbability_InterestFactor() {
		float whichTile = random.nextFloat()*getLastAllInterest();
		float probabilityCounter = 0;
		Tile selected = null;
		a:
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof HouseTile) {
					float probability = ((HouseTile)tiles[x][y]).getFactorInterest();
					probabilityCounter += probability;
					if (whichTile <= probabilityCounter) {
						selected = tiles[x][y];
						break a;
					}
					selected = tiles[x][y];
				}
			}
		}

		if (selected == null) {
			Simulation.logger.warning("getHouseTileWithProbability_InterestFactor hat nichts gefunden! Komischer Fehler, untersuchen.");
		}
		return selected;
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
	
	public Statistics getStatistics() {
		return statistics;
	}
	
	public Blueprint getBlueprint() {
		return blueprint;
	}
	
	public int calcAllNumberOfPersons() {
		allNumberOfPersons = 0;
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof HouseTile) {
					allNumberOfPersons += ((HouseTile) tiles[x][y]).getNumberPersons();
				}
			}
		}
		return getLastAllNumberOfPersons();
	}
	
	public int getLastAllNumberOfPersons() {
		if (allNumberOfPersons == -1) {
			Simulation.logger.warning("allNumberOfPersons wurde noch nicht berechnet, richtiges PersonenRouting kann nicht klappen.");
		}
		return allNumberOfPersons;
	}
	
	/**
	 * Gibt die Summe aller Interessenfaktoren aller H�user zur�ck.
	 * Diese ist sehr wichtig, um die Wahrscheinlichkeit f�r die Ankunft in einem Haus zu berechnen.
	 */
	public float calcAllInterest() {
		allInterestFactor = 0;
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof HouseTile) {
					allInterestFactor += ((HouseTile) tiles[x][y]).getFactorInterest();
				}
			}
		}
		return getLastAllInterest();
	}
	/**
	 * Ist dieser Float.NaN, wurde dieser noch nicht berechnet.
	 * @return
	 */
	public float getLastAllInterest() {
		if (allInterestFactor == Float.NaN) {
			Simulation.logger.warning("AllInterestFactor wurde noch nicht berechnet, richtiges PersonenRouting kann nicht klappen.");
		}
		return allInterestFactor;
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
	 * Setzt das Blueprint c als Eingabewert der Simulation. Dieses wird noch nicht angewendet, d.h. die Stadt wird bisher nicht ver�ndert.
	 * Der Parameter darf nicht <code>null</code>sein, zum Reseten der Stadt wird die Funktion resetBlueprint() verwendet
	 * @param c Blueprint zur Eingabe, darf nicht <code>null</code> sein
	 */
	public void setBlueprint(Blueprint c) {
		if (c == null) throw new NullPointerException("Blueprint can't be null.");
		this.blueprint = c;
	}
	
	/**
	 * Wendet das aktuelle Blueprint auf die Stadt an, falls diese existiert. Ansonsten wird nichts gemacht.
	 */
	public void applyBlueprint() {
		if (blueprint != null) {
			toNormal(); //Alles auf Anfang setzen
			
			long t1 = System.currentTimeMillis();
			
			//Pathfindingkarte erzeugen (f�r Busse):
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
			
			//Pathfindingkarte f�r Menschen erzeugen:
			stationGraph = new SimpleDirectedWeightedGraph<Waypoint, DefaultWeightedEdge>(DefaultWeightedEdge.class); 
			for ( Schedule s : blueprint.getSchedules()) { //F�gt alle Vertexe ein
				for ( Waypoint w : s.getStations()) {
					if (!stationGraph.containsVertex(w)) {
						stationGraph.addVertex(w);
					}
				}
			}
			
			
			//Verlinkt die Vertexe untereinander:
			for ( Schedule s : blueprint.getSchedules()) {
				Waypoint start = s.getStations().get(0);
				Waypoint end = null;
				for ( int i=1;i<s.getStations().size();i++) {
					end = s.getStations().get(i);
					if (!stationGraph.containsEdge(start, end) && !stationGraph.containsEdge(end, start)) {
						DefaultWeightedEdge d = stationGraph.addEdge(start, end);
						stationGraph.setEdgeWeight(d, getPathLength( 
								(int) start.getX(), 
								(int) start.getY(),
								(int) end.getX(),
								(int) end.getY()));
						DefaultWeightedEdge d2 = stationGraph.addEdge(end, start);
						stationGraph.setEdgeWeight(d2, getPathLength( 
								(int) end.getX(),
								(int) end.getY(),
								(int) start.getX(), 
								(int) start.getY()));			
					}
					start = s.getStations().get(i);
				}
			}


			//Bushaltestellen setzen:
			for ( Waypoint p : blueprint.getStations() ) {
				if (! (tiles[(int)p.getX()][(int)p.getY()] instanceof StreetTile) ) {
					Simulation.logger.warning("Achtung, Station im Blueprint verweist nicht auf eine g�ltige Stra�enkoordinate!"+" X: "+p.getX()+" Y: "+p.getY());
				} else { //korrekte Koordinate
					StreetTile street = (StreetTile) tiles[(int)p.getX()][(int)p.getY()];
					street.setToStation();
				}
			}

			//Buslinien einf�gen:
			for ( Schedule schedule : blueprint.getSchedules()) {
				schedule.calcWaypoints(this); //kreiert die internen Wegpunkte TODO eventuell muss auf eine Kopie zugegriffen werden
				events.addAll(schedule.getBusCreationEvents(this));
				//Jede Station muss wissen, dass hier diese Linie f�hrt:
				for ( Waypoint w : schedule.getStations() ) {
					if (!(tiles[(int) (w.getX())][(int) (w.getY())] instanceof StreetTile)) {
						Simulation.logger.warning("Busstation verweist nicht auf eine Stra�e");
					} else {
						StreetTile station = (StreetTile) tiles[(int) (w.getX())][(int) (w.getY())];
						if (!station.hasSchedule(schedule)) { //Diese Linie nur hinzuf�gen, wenn diese noch nicht existiert
							station.addSchedule(schedule);
						}
					}
				}
			}
			//Die Tiles wissen lassen, wo ihre n�chste Station ist
			for (int x=0;x<tiles.length;x++) {
				for (int y=0;y<tiles[0].length;y++) {
					tiles[x][y].calcNextStation(getTiles());
				}
			}
			
			//Erzeugt noch ein Hilfsarray mit allen verf�gbaren Tiles mit Stationen f�r generateRoutingForPerson:
			tilesNearStations = new ArrayList<Tile>();
			for (int x=0;x<tiles.length;x++) {
				for (int y=0;y<tiles[0].length;y++) {
					if (tiles[x][y].getNextStation() != null) {
						tilesNearStations.add(tiles[x][y]);
					}
				}
			}
			
			RoutingAlgorithm.init(tiles);
			

			
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
			b.update();
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
			b.revert();
		}
		time--;
	}
	
	/**
	 * Wandelt die Stadt wieder in den urspr�nglichen Zustand um und l�scht das aktuelle Blueprint
	 * @see #toNormal()
	 */
	public void resetBlueprint() {
		blueprint = null;
		toNormal();
	}
	
	/**
	 * Wandelt die Stadt wieder in die Urspr�ngliche um, alle �nderungen eines angewendeten Blueprints werden r�ckg�ngig gemacht.
	 * L�scht au�erdem die bisherigen Events
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
		events.clear();
		pathfindingMap = null;
	}
	
	
	

	
	/**
	 * Generiert alle RoutingEvents f�r die aktuelle Simulation.
	 */
	private void generateRoutings() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		//Generiert f�r jede Person RoutingEvents:
		ArrayList<HouseTile> houses = getHouseTiles();
		ArrayList<Event> tmpEvents = new ArrayList<Event>();
		for ( int i=0;i<FrameLauncher.townNumberPersons;i++ ) {
			Person p = new Person(-1d, -1d, null, statistics);
			generateRoutingForPerson(p, tmpEvents, random.nextInt((int) (FrameLauncher.townRuntime*(1-FrameLauncher.townPersonStopPuffer))));
		}
		
		//RoutingEvents hinzuf�gen:
		events.addAll(tmpEvents);
	}
	
	
	/**
	 * 
	 * TODO richtigen Algorithmus einbauen
	 * Generiert Routen f�r die Person p und f�gt diese der Liste list hinzu
	 * @param p die Person, auf welche sich die Routen beziehen
	 * @param list die Liste
	 * 
	 * @return TRUE, wenn ein Routing erfolgreich erzeugt wurde. Ansonsten false.
	 */
	private boolean generateRoutingForPerson(Person p, ArrayList<Event> list, int starttime) {
		
		if (personRoutingOption == PersonRoutingOption.RANDOM_START_RANDOM_END) {
			Tile origin = getRandomTile();
			Tile target = getRandomTileWithExclude(origin);

			//Wenn der Start oder Ende schiefgegangen ist, wird davon ausgegangen, dass der Mensch nie ein Ziel findet.
			if (origin.getNextStation() == null || target.getNextStation() == null) {
				List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
				Route r = pathToRoute(path); //Nochmal machen, um Fehler zu loggen
				if (r!=null) {
					Simulation.logger.warning("Komischer Fehler, sollte nie passieren");
				}
				return false;
			}
			
			/**
			 * Wenn Start und Ziel die gleiche Station anfahren wollen muss erneut ein Ziel ausgew�hlt werden.
			 * Das gleiche Ausw�hlen ist kein Fehler, muss jedoch einfach erneut ausprobiert werden.
			 * Daf�r wird jedoch einfach ein anderes Ziel anvisiert, es wird NICHT als Fehler geloggt.
			 * ACHTUNG: HIER K�NNTE DER ALGORITHMUS H�NGEN BLEIBEN
			 */
			while ( true ) {
				if (origin.getNextStation().getX() == target.getNextStation().getX() &&
					origin.getNextStation().getY() == target.getNextStation().getY() ) {
					target = getRandomTileNearStation();
				} else { //sonst ist alles super, Schleife abbrechen
					break;
				}
			}

			List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
			Route r = pathToRoute(path);
			if (r != null) {
				RoutingEvent re = new RoutingEvent(starttime, p, r);
				events.add(re);
				return true;
			}
			
			return false;
			
		} else if (personRoutingOption == PersonRoutingOption.HOUSE_START_RANDOM_END) {
			Tile origin = getHouseTileWithProbability_NumberPersons();
			Tile target = getRandomTileWithExclude(origin);
			
			//Wenn der Start oder Ende schiefgegangen ist, wird davon ausgegangen, dass der Mensch nie ein Ziel findet.
			if (origin.getNextStation() == null || target.getNextStation() == null) {
				List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
				Route r = pathToRoute(path); //Nochmal machen, um Fehler zu loggen
				if (r!=null) {
					Simulation.logger.warning("Komischer Fehler, sollte nie passieren");
				}
				return false;
			}
			
			/**
			 * Wenn Start und Ziel die gleiche Station anfahren wollen muss erneut ein Ziel ausgew�hlt werden.
			 * Das gleiche Ausw�hlen ist kein Fehler, muss jedoch einfach erneut ausprobiert werden.
			 * Daf�r wird jedoch einfach ein anderes Ziel anvisiert, es wird NICHT als Fehler geloggt.
			 * ACHTUNG: HIER K�NNTE DER ALGORITHMUS H�NGEN BLEIBEN
			 */
			while ( true ) {
				if (origin.getNextStation().getX() == target.getNextStation().getX() &&
					origin.getNextStation().getY() == target.getNextStation().getY() ) {
					target = getRandomTileNearStation();
				} else { //sonst ist alles super, Schleife abbrechen
					break;
				}
			}

			List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
			Route r = pathToRoute(path);
			if (r != null) {
				RoutingEvent re = new RoutingEvent(starttime, p, r);
				events.add(re);
				return true;
			}
			
			return false;
			
		} else if (personRoutingOption == PersonRoutingOption.HOUSE_START_HOUSE_END) {
			Tile origin = getHouseTileWithProbability_NumberPersons();
			Tile target = getHouseTileWithProbability_InterestFactor();
			int warn_counter=0;
			while (!(origin.getX() == target.getX() && origin.getY() == target.getY())) {
				target = getHouseTileWithProbability_InterestFactor();
				warn_counter++;
				if (warn_counter > 10000) {
					Simulation.logger.warning("HOUSE_START_HOUSE_END loop detected. Please fix");
					return false;
				}
			}
			
			
			//Wenn der Start oder Ende schiefgegangen ist, wird davon ausgegangen, dass der Mensch nie ein Ziel findet.
			if (origin.getNextStation() == null || target.getNextStation() == null) {
				List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
				Route r = pathToRoute(path); //Nochmal machen, um Fehler zu loggen
				if (r!=null) {
					Simulation.logger.warning("Komischer Fehler, sollte nie passieren");
				}
				return false;
			}
			
			/**
			 * Wenn Start und Ziel die gleiche Station anfahren wollen muss erneut ein Ziel ausgew�hlt werden.
			 * Das gleiche Ausw�hlen ist kein Fehler, muss jedoch einfach erneut ausprobiert werden.
			 * Daf�r wird jedoch einfach ein anderes Ziel anvisiert, es wird NICHT als Fehler geloggt.
			 * ACHTUNG: HIER K�NNTE DER ALGORITHMUS H�NGEN BLEIBEN
			 */
			while ( true ) {
				if (origin.getNextStation().getX() == target.getNextStation().getX() &&
					origin.getNextStation().getY() == target.getNextStation().getY() ) {
					target = getRandomTileNearStation();
				} else { //sonst ist alles super, Schleife abbrechen
					break;
				}
			}

			List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
			Route r = pathToRoute(path);
			if (r != null) {
				RoutingEvent re = new RoutingEvent(starttime, p, r);
				events.add(re);
				return true;
			}
			
			return false;
		} else {
			Simulation.logger.severe("Routing Option is invalid!");
			throw new java.lang.Error("Routing Option can not processed..");
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
	
	public GridCell[][] getPathfindingMap() {
		return pathfindingMap;
	}
	
	public List<GridCell> findPath(int x1, int y1, int x2, int y2) {
		return finder.findPath(x1, y1, x2, y2, navGrid);
	}
	
	public int getPathLength(int x1, int y1, int x2, int y2) {
		List<GridCell> a = findPath(x1, y1, x2, y2);
		return (a.size()+2);
	}
	public List<DefaultWeightedEdge> getPathForPerson(Tile origin, Tile target) {
		StreetTile originNextStation;
		StreetTile targetNextStation;
		
		originNextStation = origin.getNextStation();
		targetNextStation = target.getNextStation();
		

		if (originNextStation != null && targetNextStation != null) {
			
			if (originNextStation == targetNextStation) {
				statistics.addRouteSameTargets();
				return null;
			}
			
			Waypoint start = findWaypointInBlueprint((int) originNextStation.getX(), (int) originNextStation.getY());
			Waypoint end = findWaypointInBlueprint((int) targetNextStation.getX(), (int) targetNextStation.getY());
			
			if (start == null || end == null) {
				Simulation.logger.warning("Achtung, Start und Ziel konnte im Graphen nicht ermittelt werden. Start:"+start+" Koordinaten: "+originNextStation.getX()+":"+originNextStation.getY()+":Ziel:"+end+":"+targetNextStation.getX()+":"+targetNextStation.getY());
				return null;
			}

			List<DefaultWeightedEdge> shortest_path =   DijkstraShortestPath.findPathBetween(stationGraph, start, end);
			if (shortest_path == null) { //Kein Weg gefunden
				statistics.addRouteNotFound();
			} else {
				statistics.addRouteFound();
			}
			return shortest_path;
			
		} else {
			if (originNextStation == null) {
				statistics.addNoStationFound(origin);
			} 
			if (targetNextStation == null) {
				statistics.addNoStationFound(target);
			}
		}
		
		return null;
		
	}
	private int getLengthForPathPerson(List<DefaultWeightedEdge> list) {
		int result=-1;
		for (DefaultWeightedEdge w : list) {
			result += stationGraph.getEdgeWeight(w);
		}
		return result;
	}
	
	//Wird f�r die richtige Zuordnung des Graphen ben�tigt
	public Waypoint findWaypointInBlueprint(double x, double y) {
		for (Schedule s : blueprint.getSchedules()) {
			for (Waypoint w  : s.getStations()) {
				if (w.isSame((int) x, (int) y)) {
					return w;
				}
			}
		}
		return null;
	}
	
	private StreetTile findStationInBlueprint(Waypoint w) {
		if (tiles[(int)w.getX()][(int)w.getY()] instanceof StreetTile ) {
			StreetTile st = (StreetTile) tiles[(int)w.getX()][(int)w.getY()];
			if (!st.isStation()) {
				Logger.getGlobal().warning("findStationInBlueprint ist keine Station! (sondern Stra�e)");
				return null;
			}
			return st;
		} else {
			Logger.getGlobal().warning("findStationInBlueprint verweist auf ein Haus!");
			return null;
		}
	}

	
	private Route pathToRoute(List<DefaultWeightedEdge> path) {
		if (path == null) return null;
		Waypoint startW = stationGraph.getEdgeSource(path.get(0));
		Waypoint endW = stationGraph.getEdgeTarget(path.get(path.size()-1));
		StreetTile origin = findStationInBlueprint(startW);
		StreetTile target = findStationInBlueprint(endW);
		ArrayList<ChangeStation> stations = new ArrayList<ChangeStation>();
		
		Waypoint lastChangeStation = startW;
		int lastStationIndex = 0;
		ArrayList<Schedule> possibleLines = origin.getSchedules(); //M�gliche Linien vom letzten Einstieg
		SpecificSchedule sSchedule = null;

		for ( lastStationIndex=0;lastStationIndex<=path.size();lastStationIndex++) {
			
			//F�hrt diese Station direkt zum Ziel?
			ArrayList<Schedule> cutSet = new ArrayList<Schedule>(possibleLines);			
			cutSet.retainAll(findStationInBlueprint(endW).getSchedules());
			if (!cutSet.isEmpty()) { //Diese Station f�hrt direkt zum Ziel!
				Schedule schedule = cutSet.get(random.nextInt(cutSet.size()));
				BusDirection d = schedule.whichDirectionIsFaster(lastChangeStation, endW);
				if (d == BusDirection.NORMAL) {
					sSchedule = schedule.getScheduleNormal();
				} else {
					sSchedule = schedule.getScheduleReverse();
				}
				stations.add(new ChangeStation(lastChangeStation, sSchedule));
				break;
			}
			//Schnittmenge zur n�chsten Station bilden, um zu pr�fen, welche Linien weiterhin angefahren werden k�nnen

					cutSet = new ArrayList<Schedule>(possibleLines);
					cutSet.retainAll(findStationInBlueprint(stationGraph.getEdgeTarget(path.get(lastStationIndex))).getSchedules());

			if (cutSet.isEmpty()) { //Schnittmenge leer, Zeit umzusteigen!
				//Umsteigestation hinzuf�gen:
				//Daf�r eine m�gliche Linie aussuchen:
				int warn_counter = 0;
				doo:
				do {
					Schedule schedule = possibleLines.get(random.nextInt(possibleLines.size()));
					BusDirection d = schedule.whichDirectionIsFaster(lastChangeStation, stationGraph.getEdgeSource(path.get(lastStationIndex)));
				
				if (d == BusDirection.NORMAL) {
					sSchedule = schedule.getScheduleNormal();
					break doo;
				} else if (d == BusDirection.REVERSE){
					sSchedule = schedule.getScheduleReverse();
					break doo;
				} else if (d == null) {
					//Falsche Schedule wurde ausgew�hlt
					//possibleLines.remove(schedule);
					warn_counter++;
					if (warn_counter>1000) {
						System.err.println("LOOP DETECTED in pathToRoute!!");
					}
				}
				} while (true);
				stations.add(new ChangeStation(lastChangeStation, sSchedule));
				lastChangeStation = stationGraph.getEdgeSource(path.get(lastStationIndex));
				possibleLines = findStationInBlueprint(stationGraph.getEdgeSource(path.get(lastStationIndex))).getSchedules();
				//possibleLines.retainAll(findStationInBlueprint(stationGraph.getEdgeSource(path.get(lastStationIndex))).getSchedules());
			} else { //m�gliche Linien m�ssen eingeschr�nkt werden
				possibleLines = cutSet;
			}
		}
		//Letzte Station hinzuf�gen:
		if (sSchedule == null) {
			//Das kann passieren, wenn der Weg zu umst�ndlich ist. Also ein Cryple Error
			statistics.addCrypleError();
			System.out.println("CRYPLEEE");
			return null;
		}
		stations.add(new ChangeStation(endW, sSchedule));
		

		Route r = new Route(origin, target, stations);
		return r;
		
		}
	
		

	
	/**
	 * Gibt die k�rzeste SpecificSchedule zwischen zwei Stationen zur�ck (m�ssen nicht nebeneinander sein)
	 * @param station1
	 * @param station2
	 * @return ALLLLLLLLLLLLLLLLLLLLLT
	 */
	private SpecificSchedule getSpecificScheduleOfTwoStations(StreetTile station1, StreetTile station2) {
		/////////////////////////////ALT/////////////////////////////////////
		HashSet<Schedule> hs1 = new HashSet<Schedule>(station1.getSchedules());
		HashSet<Schedule> hs2 = new HashSet<Schedule>(station2.getSchedules());
		hs1.retainAll(hs2);
		if (hs1.isEmpty()) {
			Simulation.logger.warning("getSpecificScheduleOfTwoStations hat keine direkte Verbindung zwischen zwei Stationen gefunden.");
			return null;
		}
		//Jede Route durchkalkulieren, mit BusDirection.NORMAL und BusDirection.REVERSE
		SpecificSchedule shortest = null;
		float duration = Float.MAX_VALUE;
		for (Iterator<Schedule> it = hs1.iterator(); it.hasNext();){
			Schedule schedule = it.next();
			if (schedule.getStationIndex(station1.getX(), station1.getY()) == 0) {
				duration = schedule.getTimeForBusDirectionNormal(findWaypointInBlueprint(station1.getX(), station1.getY()), findWaypointInBlueprint(station2.getX(), station2.getY()));
				shortest = schedule.getScheduleNormal();
			} else if (schedule.getStationIndex(station1.getX(), station1.getY()) == schedule.getStations().size()-1 ) {
				duration = schedule.getTimeForBusDirectionReverse(findWaypointInBlueprint(station1.getX(), station1.getY()), findWaypointInBlueprint(station2.getX(), station2.getY()));
				shortest = schedule.getScheduleReverse();
			} else {
				float timeNormal = schedule.getTimeForBusDirectionNormal(findWaypointInBlueprint(station1.getX(), station1.getY()), findWaypointInBlueprint(station2.getX(), station2.getY()));
				float timeReverse = schedule.getTimeForBusDirectionReverse(findWaypointInBlueprint(station1.getX(), station1.getY()), findWaypointInBlueprint(station2.getX(), station2.getY()));
				
				if (timeNormal < duration) {
					duration = timeNormal;
					shortest = schedule.getScheduleNormal();
				}
				if (timeReverse < duration) {
					duration = timeReverse;
					shortest = schedule.getScheduleReverse();
				}
			}
		}
		

		return shortest;
	}
}
