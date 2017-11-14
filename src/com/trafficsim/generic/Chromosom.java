package com.trafficsim.generic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import com.trafficsim.sim.Simulation;
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
	 * Speichert f�r das Chromosom, welche IDs in Stationen umgewandelt werden sollen.
	 * Beim Erzeugen ist jeder Wert FALSE (keine Station)
	 */
	public boolean[] isStation;
	
	/**
	 * Liste mit allen Buslinien die existieren. Die Integer f�r die Stations sind die gleichen wie f�r die streetMaps
	 */
	public ArrayList<ChromosomSchedule> schedules;
	
	private HashMap<Integer, Point> streetMapGenericToSimulation;	
	private HashMap<Point, Integer> streetMapSimulationToGeneric;
	
	/**
	 * Gespeicherte Waypoints, diese d�rfen n�mlich nicht doppelt im System vorkommen
	 */
	private HashSet<Waypoint> waypoints; 
	
	//Wird sp�ter f�r die TOWN ben�tigt und in GENERATE umgerechnet
	private ArrayList<Schedule> schedulesForSimulation;
	private ArrayList<Point> stationsForSimulation;
	
	public Chromosom(float[][][] town) {
		streetMapGenericToSimulation = (HashMap<Integer, Point>) townToMappingIP(town);
		streetMapSimulationToGeneric = (HashMap<Point, Integer>) townToMappingPI(town);
		isStation = new boolean[streetMapGenericToSimulation.values().size()];
		for ( int i=0; i<isStation.length; i++) {
			isStation[i] = false;
		}
		
		schedules = new ArrayList<ChromosomSchedule>();
		
		schedulesForSimulation = new ArrayList<Schedule>();
		stationsForSimulation = new ArrayList<Point>();
		
		waypoints = new HashSet<Waypoint>();
	}
	
	public boolean[] getIsStation() {
		return isStation;
	}
	
	public HashMap<Integer, Point> getMapGenericSimulation() {
		return streetMapGenericToSimulation;
	}
	
	public HashMap<Point, Integer> getMapSimulationGeneric() {
		return streetMapSimulationToGeneric;
	}
	
	/*
	 * Diese Funktionen werden sp�ter f�r die Town ben�tigt
	 */
	public ArrayList<Schedule> getSchedules() {
		return schedulesForSimulation;
	}
	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedulesForSimulation = schedules;
	}
	public ArrayList<Point> getStations() {
		return stationsForSimulation;
	}
	public void setStations(ArrayList<Point> stations) {
		this.stationsForSimulation = stations;
	}
	
	
	/**
	 * Generiert anhand der gesetzen Daten die Wegpunkte und Schedules und Stationen usw..
	 * Nachdem diese Funktion aufgerufen werden, kann das Chromosom mithilfe town.setChromosom(c) angewandt werden
	 * 
	 * Die Town wird ben�tigt, um Zugriff auf die Waypoints f�r die richtige Graphenerstellung zu haben
	 */
	public void generate(Town town) {
		//Stationen generieren:
		for (int i=0;i<isStation.length;i++) {
			if (isStation[i] == true) {
				stationsForSimulation.add(new Point(streetMapGenericToSimulation.get(i)));
			}
		}
		int tmp=0;
		//Buslinien generieren:
		for (ChromosomSchedule sch : schedules) {
			System.out.println("Neue Buslinie");
			ArrayList<Waypoint> stations = new ArrayList<Waypoint>();
			System.out.println("Folgende Stationen werden angefahren");
			for (Integer i : sch.stations) {
				Point position = streetMapGenericToSimulation.get(i);
				System.out.println(position);
				Waypoint w = findWaypoint(position.x, position.y);
				stations.add(w);
			}
			
			Schedule forSimulation = new Schedule(stations, sch.busStartTimes, 0, sch.name); //Endlich Schedule erzeugen
			schedulesForSimulation.add(forSimulation); //Hinzuf�gen zum Chromosom
			
			
		}
	}
	
	/**
	 * Konvertiert eine Karte in das interne Format f�r den genetischen Algorithmus, welcher alle Stra�en in einem zweidimensionalen Array speichert
	 * @param town die Karte zum konvertieren
	 * @return Map<Integer, Point>, welche nur Stra�en beinhaltet. Der Integer einfach eine ID
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
	 * Macht das Gegenteil von der genannten Funktion, vertauscht deren Schl�ssel <-> Wert
	 * 
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
	public static Chromosom randomChromosom(float[][][] town, Random r) {
		Chromosom back = new Chromosom(town);
		
		float chanceForStation = 1/3f; //Wahrscheinlichkeit, dass eine Station entsteht
		float chanceForSchedulePerStreet = 1/4f; //Wahrscheinlichkeit, dass eine Buslinie erzeugt wird, pro Stra�enteil
		float chanceForStationAddedToSchedule = 1/2f; //Wahrscheinlichkeit, ob eine Station zu einer neu generierten Buslinie hinzugef�gt wird
		
		int maxNumberOfSchedules = 1; //Limit f�r die Anzahl der Schedules, kann sp�ter rausgenommen werden
		
		//Zwischen diesen beiden Werten (beides inklusiv) liegt die Anzahl an Bussen, welche f�r eine Linie generiert werden
		int minNumberOfBusses = 2;
		int maxNumberOfBusses = 2;
		//Zwischen diesem Wert liegt jede n�chste Startzeit f�r einen neuen Bus ( im Unterschied zum letzten Bus ) (beides inklusiv)
		int minStartTime = 20;
		int maxStartTime = 50;
		
		for (int i=0; i<back.isStation.length; i++) {
			if (r.nextFloat() <= chanceForStation) {
				back.isStation[i] = true;
				System.out.println("Station at: "+back.streetMapGenericToSimulation.get(i));
			}
		}
		
		
		for (int i=0; i<back.isStation.length; i++) {
			if (r.nextFloat() <= chanceForSchedulePerStreet && back.schedules.size() < maxNumberOfSchedules) {
				//Buslinie erzeugen
				ChromosomSchedule sch = new ChromosomSchedule();
				
				for (int i2=0; i2<back.isStation.length; i2++) { //Zuf�llig Stationen hinzuf�gen
					if (back.isStation[i2] == true) { //Wenn hier eine Station ist
						if (r.nextFloat() <= chanceForStationAddedToSchedule) { //F�ge diese Station hinzu
							sch.stations.add(i2);
						}
					}
				}
				//Wenn nicht genug Stationen vorhanden sind, m�ssen noch welche hinzugef�gt werden:
				if (sch.stations.size() == 0) {
					for (int i2=back.isStation.length-1; i2>=0; i2--) { //Zuf�llig Stationen hinzuf�gen
						if (back.isStation[i2] == true) { //Wenn hier eine Station ist
							sch.stations.add(i2);
						}
					}
				}

				if (sch.stations.size() == 1) {
					for (int i2=0; i2<back.isStation.length; i2++) { //Zuf�llig Stationen hinzuf�gen
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
					if (r.nextBoolean()) { //Hier zuf�llig die Richtung einstellen
						direction = BusDirection.REVERSE;
					}
					sch.busStartTimes.add(new BusStartTime(nextBusStartTime, direction));
				}
				
				back.schedules.add(sch); //Und den Fahrplan zum Chromosom hinzuf�gen
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
		System.out.println("Created "+x+":"+y);
		return back;
	}
	

}
