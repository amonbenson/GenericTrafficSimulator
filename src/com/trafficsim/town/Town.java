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
import com.trafficsim.sim.Simulation;

public class Town implements Updateable {
	
	// Generating person names
	private static final PersonNameGenerator personNameGenerator = new PersonNameGenerator();
	
	private Statistics statistics;
	
	private Tile[][] tiles; //Die Karte der Start
	private int sizeX, sizeY; //Größe der Stadt
	private Blueprint blueprint; //Blueprint, welches auf diese Stadt angewendet wurde.
	private ArrayList<Event> events; //Liste aller Events

	private int events_index; //aktuelle Position der Events
	private ArrayList<Bus> busses; //alle Busse der Stadt
	private ArrayList<Person> persons; //alle Personen der Stadt
	private long time; //aktuelle Zeit der Stadt
	private Random random; //jede Stadt besitzt einen eigenen Randomgenerator (so können bestimmte Szenarien erneut simuliert werden)
	
	//Wegfindungszeug für Busse:
	private GridCell[][] pathfindingMap = null; //die aktuelle Pathfindingkarte, wird bei applyBlueprint erzeugt
	private NavigationGrid<GridCell> navGrid = null;
	private GridFinderOptions opt = null;
	AStarGridFinder<GridCell> finder = null;
	
	//Wegfindungszeug für Personen:
	private SimpleDirectedWeightedGraph<Waypoint, DefaultWeightedEdge> stationGraph = null;
	
	public Town(int sizeX, int sizeY) {
		this(sizeX, sizeY, null, null, new Random());
	}
	
	public Town(int sizeX, int sizeY, Random random) {
		this(sizeX, sizeY, null, null, random);
	}
	
	public Town(int sizeX, int sizeY, Tile[][] tiles, Blueprint blueprint, Random random) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.tiles = tiles;
		this.blueprint = blueprint;
		this.random = random;
		
		events = new ArrayList<Event>();
		busses = new ArrayList<Bus>();
		persons = new ArrayList<Person>();
		time = 0;
		
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
	
	public Statistics getStatistics() {
		return statistics;
	}
	
	public Blueprint getBlueprint() {
		return blueprint;
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
	 * Setzt das Blueprint c als Eingabewert der Simulation. Dieses wird noch nicht angewendet, d.h. die Stadt wird bisher nicht verändert.
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
			
			//Pathfindingkarte erzeugen (für Busse):
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
			
			//Pathfindingkarte für Menschen erzeugen:
			stationGraph = new SimpleDirectedWeightedGraph<Waypoint, DefaultWeightedEdge>(DefaultWeightedEdge.class); 
			for ( Schedule s : blueprint.getSchedules()) { //Fügt alle Vertexe ein
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
					Simulation.logger.warning("Achtung, Station im Blueprint verweist nicht auf eine gültige Straßenkoordinate!"+" X: "+p.getX()+" Y: "+p.getY());
				} else { //korrekte Koordinate
					StreetTile street = (StreetTile) tiles[(int)p.getX()][(int)p.getY()];
					street.setToStation();
				}
			}
			//Buslinien einfügen:
			for ( Schedule schedule : blueprint.getSchedules()) {
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
			
			//statistics.print();
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
	 * Wandelt die Stadt wieder in den ursprünglichen Zustand um und löscht das aktuelle Blueprint
	 * @see #toNormal()
	 */
	public void resetBlueprint() {
		blueprint = null;
		toNormal();
	}
	
	/**
	 * Wandelt die Stadt wieder in die Ursprüngliche um, alle Änderungen eines angewendeten Blueprints werden rückgängig gemacht.
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
				Person p = new Person(-1d, -1d, house, statistics);
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
	 * 
	 * @return TRUE, wenn ein Routing erfolgreich erzeugt wurde. Ansonsten false.
	 */
	private boolean generateRoutingForPerson(Person p, ArrayList<Event> list) {
		
		Tile origin = getRandomTileWithExclude(p.getHouse());
		/*int newX = random.nextInt(sizeX-(StreetTile.AREA_STATION*2+1) );
		if (newX >= origin.getX()-(StreetTile.AREA_STATION*2+1)) {
			newX += StreetTile.AREA_STATION*2+1;
		}
		int newY = random.nextInt(sizeY-(StreetTile.AREA_STATION*2+1) );
		if (newY >= origin.getY()-(StreetTile.AREA_STATION*2+1)) {
			newY += StreetTile.AREA_STATION*2+1;
		}*/
		int newX = random.nextInt(sizeX);
		int newY = random.nextInt(sizeY);
		
		Tile target = tiles[newX][newY];
		
		List<DefaultWeightedEdge> path = getPathForPerson(origin, target);
		Route r = pathToRoute(path);
		if (r != null) {
			RoutingEvent re = new RoutingEvent(0, p, r);
			events.add(re);
			return true;
		}
		return false;
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
		
		originNextStation = origin.getNextStation(tiles);
		targetNextStation = target.getNextStation(tiles);
		
		if (originNextStation == targetNextStation) {
			statistics.addRouteSameTargets();
			return null;
		}
		if (originNextStation != null && targetNextStation != null) {
			
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
			statistics.addNoStationFound(origin.toWaypoint());
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
	
	//Wird für die richtige Zuordnung des Graphen benötigt
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
				Logger.getGlobal().warning("findStationInBlueprint ist keine Station! (sondern Straße)");
				return null;
			}
			return st;
		} else {
			Logger.getGlobal().warning("findStationInBlueprint verweist auf ein Haus!");
			return null;
		}
	}
	
	/**
	 * Kann <code>null</code> zurückgeben, wenn <code>null</code> der Parameter ist.
	 * Generiert anhand der algorithmischen Lösung die spezifische Route.
	 * 
	 * @param path
	 * @return
	 *
	private Route pathToRoute(List<DefaultWeightedEdge> path) {
		if (path == null) return null;
		Waypoint startW = stationGraph.getEdgeSource(path.get(0));
		Waypoint endW = stationGraph.getEdgeTarget(path.get(path.size()-1));
		StreetTile origin = findStationInBlueprint(startW);
		StreetTile target = findStationInBlueprint(endW);		
		ArrayList<ChangeStation> stations = new ArrayList<ChangeStation>();
		
		ArrayList<Schedule> lastSchedules = findStationInBlueprint(stationGraph.getEdgeSource(path.get(0))).getSchedules();
		
		SpecificSchedule first = null;
		
		Waypoint lastChangeStation = startW; //Die letzte Station ist am Anfang immer die Startstations
		
		//Als erstes die Buslinie finden, welche am weitesten zum Ziel kommt:
		for ( int i=0;i<path.size();i++) {
			StreetTile nextStation = findStationInBlueprint(stationGraph.getEdgeTarget(path.get(i)));
			System.out.println(nextStation);
			ArrayList<Schedule> hs1 = new ArrayList<Schedule>(lastSchedules); //Start
			HashSet<Schedule> hs2 = new HashSet<Schedule>(nextStation.getSchedules());
			hs1.retainAll(hs2); //Schnittmenge mit Stationen, welche in beiden Listen vorhanden sind
			if (hs1.isEmpty()) { //Alle Buslinien enden auf einmal.. also eine zufällige auswählen
				Schedule selected = lastSchedules.get(random.nextInt(lastSchedules.size()));
				
				if (selected.whichDirectionIsFaster(lastChangeStation, stationGraph.getEdgeSource(path.get(i))) == BusDirection.NORMAL ) {
					first = selected.getScheduleNormal();
				} else { //REVERSE
					first = selected.getScheduleReverse();
				}
				stations.add(new ChangeStation(startW, first)); //Anfangsrichtung hinzufügen
				lastChangeStation = findWaypointInBlueprint(nextStation.getX(), nextStation.getY());
			}
			
			if (hs1.size() == 1) { //letzte Busstation gefunden!
				Schedule selected = hs1.get(0);
				if (selected.whichDirectionIsFaster(lastChangeStation, stationGraph.getEdgeTarget(path.get(i))) == BusDirection.NORMAL ) {
					first = selected.getScheduleNormal();
				} else { //REVERSE
					first = selected.getScheduleReverse();
				}
				stations.add(new ChangeStation(lastChangeStation, first)); //Anfangsrichtung hinzufügen
				lastChangeStation = findWaypointInBlueprint(nextStation.getX(), nextStation.getY());
			
			lastSchedules = new ArrayList<Schedule>(nextStation.getSchedules());
			}
		}
			
	
		stations.add(new ChangeStation(endW, target.getSchedules().get(0).getScheduleReverse())); //Ende hinzufügen
		if (stations.size() >= 2) {
			Route r = new Route(origin, target, stations);
			return r;
		} else {
			Simulation.logger.warning("Pfad:"+path.toString());
			Simulation.logger.warning("Start:"+startW.toString());
			Simulation.logger.warning("Ende:"+endW.toString());
			Simulation.logger.warning("Start in B:"+origin.getX()+":"+origin.getY());
			Simulation.logger.warning("Ende in B:"+target.getX()+":"+target.getY());
			for ( ChangeStation s : stations ) {
				Simulation.logger.warning("ChangeStation:" + s+"\n\n");
				
			}
			Simulation.logger.warning("------------Stationsende------------");
			Simulation.logger.warning("Stationsgröße zu klein, bitte beheben");
			return null;
		}
	}*/

	
	private Route pathToRoute(List<DefaultWeightedEdge> path) {
		if (path == null) return null;
		Waypoint startW = stationGraph.getEdgeSource(path.get(0));
		Waypoint endW = stationGraph.getEdgeTarget(path.get(path.size()-1));
		StreetTile origin = findStationInBlueprint(startW);
		StreetTile target = findStationInBlueprint(endW);
		ArrayList<ChangeStation> stations = new ArrayList<ChangeStation>();
		
		Waypoint lastChangeStation = startW;
		int lastStationIndex = 0;
		ArrayList<Schedule> possibleLines = origin.getSchedules(); //Mögliche Linien vom letzten Einstieg
		SpecificSchedule sSchedule = null;
		
		for ( int i=lastStationIndex;i<path.size();i++) {
			
			//Führt diese Station direkt zum Ziel?
			ArrayList<Schedule> cutSet = new ArrayList<Schedule>(possibleLines);			
			cutSet.retainAll(findStationInBlueprint(endW).getSchedules());
			if (!cutSet.isEmpty()) { //Diese Station führt direkt zum Ziel!
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
			
			//Schnittmenge zur nächsten Station bilden, um zu prüfen, welche Linien weiterhin angefahren werden können
			cutSet = new ArrayList<Schedule>(possibleLines);
			cutSet.retainAll(findStationInBlueprint(stationGraph.getEdgeTarget(path.get(lastStationIndex))).getSchedules());
			if (cutSet.isEmpty()) { //Schnittmenge leer, Zeit umzusteigen!

				//Umsteigestation hinzufügen:
				//Dafür eine mögliche Linie aussuchen:
				Schedule schedule = possibleLines.get(random.nextInt(possibleLines.size()));
				BusDirection d = schedule.whichDirectionIsFaster(lastChangeStation, stationGraph.getEdgeSource(path.get(lastStationIndex)));
				if (d == BusDirection.NORMAL) {
					sSchedule = schedule.getScheduleNormal();
				} else {
					sSchedule = schedule.getScheduleReverse();
				}
				stations.add(new ChangeStation(lastChangeStation, sSchedule));
				lastChangeStation = stationGraph.getEdgeSource(path.get(lastStationIndex));
				possibleLines = findStationInBlueprint(stationGraph.getEdgeTarget(path.get(lastStationIndex))).getSchedules();
				
			} else { //mögliche Linien müssen eingeschränkt werden
				possibleLines = cutSet;
			}
		}
		//Letzte Station hinzufügen:
		if (sSchedule == null) {
			//Das kann passieren, wenn der Weg zu umständlich ist. Also ein Cryple Error
			statistics.addCrypleError();
			return null;
		}
		stations.add(new ChangeStation(endW, sSchedule));
		

		Route r = new Route(origin, target, stations);
		return r;
		
		}
	
		

	
	/**
	 * Gibt die kürzeste SpecificSchedule zwischen zwei Stationen zurück (müssen nicht nebeneinander sein)
	 * @param station1
	 * @param station2
	 * @return
	 */
	private SpecificSchedule getSpecificScheduleOfTwoStations(StreetTile station1, StreetTile station2) {
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
	
	public static PersonNameGenerator getPersonNameGenerator() {
		return personNameGenerator;
	}
}
