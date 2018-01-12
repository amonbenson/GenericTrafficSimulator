package com.trafficsim.generic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

/**
 * Als erstes muss das Chromosom generieren, wo Stationen gebaut werden sollen.
 * Danach erstellt das Chromosom Buslinien mit Elementen aus den gebauten Stationen.
 * 
 * @author Luca
 *
 */
public class Chromosom {

	/**
	 * Speichert für das Chromosom, welche internen IDs in Stationen umgewandelt werden sollen.
	 * Um herauszufinden, welche dieser IDs (oder in der weiteren Dokumentation auch Index genannt) auf welchen Punkt auf der Karte verweist, 
	 * kann die HashMap streetMapGenericToSimulation genutzt werden.
	 * Wenn herausgefunden werden soll, welcher Punkt welche ID in diesem Array hat, kann die HashMap streetMapSimulationToGeneric verwendet werden.
	 * 
	 * Beim Erzeugen ist jeder Wert FALSE (keine Station).
	 * 
	 */
	public boolean[] isStation;
	
	/**
	 * Liste mit allen Buslinien die existieren. Die Integer für die Stations sind die gleichen wie für die streetMaps
	 */
	public ArrayList<ChromosomSchedule> schedules;
	/**
	 * Diese HashMap ist der Nachweis für das Array <code>isStation</code>.
	 * Wenn herausgefunden werden soll, welcher Index aus <code>isStation</code> auf welchen Wegpunkt verweist, kann hier nachgeschaut werden.
	 * Der Key ist dabei der Index von isStation, das Resultat der Punkt auf der zweidimensionalen Karte.
	 * 
	 * <code>streetMapGenericToSimulation</code> wird im Konstruktor automatisch erzeugt (@see townToMappingIP(float[][][]) ) und kann dann sofort verwendet werden.
	 * 
	 */
	private HashMap<Integer, Point> streetMapGenericToSimulation;
	/**
	 * Diese HashMap macht das Umgekehrte von <code>streetMapGenericToSimulation</code>, als Key dient der Punkt auf der Karte, welcher in einen Index für <code>isStation</code> umgewandelt werden soll.
	 * 
	 * <code>streetMapSimulationToGeneric</code> wird im Konstruktor automatisch erzeugt (@see townToMappingPI(float[][][]) ) und kann dann sofort verwendet werden.
	 */
	private HashMap<Point, Integer> streetMapSimulationToGeneric;
	
	/**
	 * Gespeicherte Waypoints, diese dürfen nämlich nicht doppelt im System vorkommen
	 */
	private HashSet<Waypoint> waypoints; 
	
	/*
	 * Wird später für die TOWN benötigt und in GENERATE umgerechnet.
	 * Die beiden Werte werden über <code>setStations(ArrayList<Point>)</code> und 
	 * <code>setSchedules(ArrayList<Waypoint)</code> von außen gesetzt.
	 */
	private ArrayList<Schedule> schedulesForSimulation;
	private ArrayList<Waypoint> stationsForSimulation;
	
	public Chromosom(float[][][] town) {
		streetMapGenericToSimulation = (HashMap<Integer, Point>) townToMappingIP(town);
		streetMapSimulationToGeneric = (HashMap<Point, Integer>) townToMappingPI(town);
		isStation = new boolean[streetMapGenericToSimulation.values().size()];
		for ( int i=0; i<isStation.length; i++) {
			isStation[i] = false;
		}
		schedules = new ArrayList<ChromosomSchedule>();
		
		schedulesForSimulation = new ArrayList<Schedule>();
		stationsForSimulation = new ArrayList<Waypoint>();
		
		waypoints = new HashSet<Waypoint>();
	}
	
	/**
	 * Gibt das Array zurück, welches angibt, wo auf den Straßen die Stationen sind.
	 */
	public boolean[] getIsStation() {
		return isStation;
	}
	/**
	 * Gibt die "Übersetzung" für <code>getIsStation()</code>, welche Indexes davon in reguläre Punkte übersetzt, zurück.
	 */
	public HashMap<Integer, Point> getMapGenericSimulation() {
		return streetMapGenericToSimulation;
	}
	/**
	 * Gibt die "Übersetzung" für <code>getIsStation()</code>, welche reguläre Punkte in die internen Indexes übersetzt, zurück.
	 */
	public HashMap<Point, Integer> getMapSimulationGeneric() {
		return streetMapSimulationToGeneric;
	}
	
	/*
	 * Diese Funktionen werden später für die Town benötigt
	 */
	public ArrayList<Schedule> getSchedules() {
		return schedulesForSimulation;
	}
	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedulesForSimulation = schedules;
	}
	public ArrayList<Waypoint> getStations() {
		return stationsForSimulation;
	}
	public void setStations(ArrayList<Waypoint> stations) {
		this.stationsForSimulation = stations;
	}
	
	
	/**
	 * Generiert anhand der gesetzen Daten die Wegpunkte und Schedules und Stationen usw..
	 * Nachdem diese Funktion aufgerufen werden, kann das Chromosom mithilfe town.setChromosom(c) angewandt werden
	 * 
	 * Die Town wird benötigt, um Zugriff auf die Waypoints für die richtige Graphenerstellung zu haben
	 */
	public void generate(Town town) {
		//Stationen generieren:
		for (int i=0;i<isStation.length;i++) {
			if (isStation[i] == true) {
				stationsForSimulation.add(new Waypoint(streetMapGenericToSimulation.get(i).getX(), streetMapGenericToSimulation.get(i).getY()));
			}
		}
		int tmp=0;
		//Buslinien generieren:
		for (ChromosomSchedule sch : schedules) {
			//System.out.println("Neue Buslinie");
			ArrayList<Waypoint> stations = new ArrayList<Waypoint>();
			System.out.println("Linie " + sch.name + ": Folgende Stationen werden angefahren");

			

			for (Integer i : sch.stations) {
				Point position = streetMapGenericToSimulation.get(i);
				//System.out.println(position);
				Waypoint w = findWaypoint(position.x, position.y);
				stations.add(w);
			}
			
			Schedule forSimulation = new Schedule(stations, sch.busStartTimes, 0, sch.name); //Endlich Schedule erzeugen
			schedulesForSimulation.add(forSimulation); //Hinzufügen zum Chromosom
			
			
		}
	}
	
	/**
	 * Konvertiert eine Karte in das interne Format für den genetischen Algorithmus, welcher alle Straßen in einem eindimensionalen Array speichert.
	 * Namenserklärung: IP - Integer -> Point (Der Index ist Key, als Value dient der Wegpunkt mit der Straße)
	 * @param town die Karte zum konvertieren
	 * @return Map<Integer, Point>, welche nur Straßen beinhaltet. Der Integer einfach eine ID
	 */
	public static Map<Integer, Point> townToMappingIP(float[][][] town) {
		Map<Integer, Point> back = new HashMap<Integer, Point>();
		int counter = 0;
		
		for (int x = 0; x < town.length; x++) {
			for (int y = 0; y < town[0].length; y++) {
				if (town[x][y][0] == 0) {
					back.put(counter, new Point(x, y));
					counter++;
				}
			}
		}
		
		return back;
	}

	/**
	 * @see townToMappingIP(float[][][])
	 * Macht das Gegenteil von der genannten Funktion, vertauscht deren Schlüssel <-> Wert
	 * 
	 * 	 * Namenserklärung: IP - Integer -> Point (Der Wegpunkt mit der Straße ist Key, als Value dient der Index von <code>isStation</code>)
	 */
	public static Map<Point, Integer> townToMappingPI(float[][][] town) {
		Map<Point, Integer> back = new HashMap<Point, Integer>();
		
		int counter = 0;
		
		for (int x = 0; x < town.length; x++) {
			for (int y = 0; y < town[0].length; y++) {
				if (town[x][y][0] == 0) {
					back.put(new Point(x, y), counter);
					counter++;
				}
			}
		}
		
		return back;
	}
	
	
	public static Chromosom randomChromosom(float[][][] town) {
		return randomChromosom(town, new Random());
	}
	
	/**
	 * Hier wird ein zufälliges Chromosom erzeugt
	 * @param town
	 * @param r
	 * @return
	 */
	public static Chromosom randomChromosom(float[][][] town, Random r) {
		Chromosom back = new Chromosom(town);
		
		float chanceForStation = 1/3f; //Wahrscheinlichkeit, dass eine Station entsteht
		float chanceForSchedulePerStreet = 1/4f; //Wahrscheinlichkeit, dass eine Buslinie erzeugt wird, pro Straßenteil
		float chanceForStationAddedToSchedule = 1/2f; //Wahrscheinlichkeit, ob eine Station zu einer neu generierten Buslinie hinzugefügt wird
		
		int maxNumberOfSchedules = 3; //Limit für die Anzahl der Schedules, kann später rausgenommen werden
		
		//Zwischen diesen beiden Werten (beides inklusiv) liegt die Anzahl an Bussen, welche für eine Linie generiert werden
		int minNumberOfBusses = 1;
		int maxNumberOfBusses = 1;
		//Zwischen diesem Wert liegt jede nächste Startzeit für einen neuen Bus ( im Unterschied zum letzten Bus ) (beides inklusiv)
		int minStartTime = 20;
		int maxStartTime = 50;
		
		//Generiere zufällige Stationen
		for (int i=0; i<back.isStation.length; i++) {
			if (r.nextFloat() <= chanceForStation) {
				back.isStation[i] = true;
				//System.out.println("Station at: "+back.streetMapGenericToSimulation.get(i));
			}
		}
		
		
		for (int i=0; i<back.isStation.length; i++) {
			if (r.nextFloat() <= chanceForSchedulePerStreet && back.schedules.size() < maxNumberOfSchedules) {
				//Buslinie erzeugen
				ChromosomSchedule sch = new ChromosomSchedule();
				
				for (int i2=0; i2<back.isStation.length; i2++) { //Zufällig Stationen hinzufügen
					if (back.isStation[i2] == true) { //Wenn hier eine Station ist
						if (r.nextFloat() <= chanceForStationAddedToSchedule) { //Füge diese Station hinzu
							sch.stations.add(i2);
						}
					}
				}
				//Wenn nicht genug Stationen vorhanden sind, müssen noch welche hinzugefügt werden:
				if (sch.stations.size() == 0) {
					for (int i2=back.isStation.length-1; i2>=0; i2--) { //Zufällig Stationen hinzufügen
						if (back.isStation[i2] == true) { //Wenn hier eine Station ist
							sch.stations.add(i2);
						}
					}
				}

				if (sch.stations.size() == 1) {
					for (int i2=0; i2<back.isStation.length; i2++) { //Zufällig Stationen hinzufügen
						if (back.isStation[i2] == true) { //Wenn hier eine Station ist
							sch.stations.add(i2);
						}
					}
				}
				
				int numberOfBusses = r.nextInt(maxNumberOfBusses+1-minNumberOfBusses)+minNumberOfBusses; //Anzahl an Bussen berechnen
				int lastBusStartTime = 0; //letzte Startzeit ist einfach 0
				sch.busStartTimes.add(new BusStartTime(lastBusStartTime, ( r.nextBoolean() ? BusDirection.NORMAL: BusDirection.REVERSE )));
				for (int i2=1; i2<numberOfBusses;i2++) {
					int nextBusStartTime = lastBusStartTime + r.nextInt(maxStartTime+1-minStartTime)+minStartTime;
					BusDirection direction = BusDirection.NORMAL;
					if (r.nextBoolean()) { //Hier zufällig die Richtung einstellen
						direction = BusDirection.REVERSE;
					}
					sch.busStartTimes.add(new BusStartTime(nextBusStartTime, direction));
				}
				
				back.schedules.add(sch); //Und den Fahrplan zum Chromosom hinzufügen
			}
		}
		return back;
	}
	
	public Waypoint findWaypoint(int x, int y) {
		for (Waypoint w : waypoints) {
			if (w.isSame(x, y)) {
				System.out.println("Found "+x+":"+y);
				return w;
			}
		}
		Waypoint back = new Waypoint(x+0.5, y+0.5); //0.5, weil die Busse immer in der Mitte fahren sollen
		waypoints.add(back);
		//System.out.println("Created "+x+":"+y);
		return back;
	}
	
}
