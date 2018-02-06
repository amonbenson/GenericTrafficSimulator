package com.trafficsim.genericalgorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Waypoint;

public class BlueprintConverter {
	public static Blueprint convert(List<Chromosome> chromosomes, float[][][] map) {
		Blueprint back = new Blueprint(map);

		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> stations = new ArrayList<Waypoint>();
		Map<Point, Integer> mappingPI = Blueprint.townToMappingPI(map);
		Map<Integer, Point> mappingIP = Blueprint.townToMappingIP(map);

		// Jeder Wegpunkt darf nur einmal erzeugt werden, daher diese Liste
		ArrayList<Waypoint> tmpWaypoints = new ArrayList<Waypoint>();

		// Stationen setzen:
		final Chromosome stationsChromo = chromosomes.get(0);
		for (int i = 0; i < stationsChromo.getLength(); i++) {
			if (stationsChromo.getGene(i) % 2 == 0) { // Gerade Zahl - Station setzen
				Point p = mappingIP.get(i);
				if (p == null) {
					System.out.println("Diesen Index " + i + " gibt es nicht, keine Station gesetzt.");
					continue;
				}
				tmpWaypoints.add(new Waypoint(p.x + 0.5, p.y + 0.5)); // 0.5 ist wichtig, muss so sein
				stations.add(tmpWaypoints.get(tmpWaypoints.size() - 1)); // diesen Wegpunkt hinzuf�gen
			} else { // Ungerade Zahl - keine Station setzen

			}
		}

		// Schedules erzeugen:
		for (int scheduleIndex = 0; scheduleIndex < FrameLauncher.chromoScheduleCount; scheduleIndex++) {

			final Chromosome usedStationsChromo = chromosomes.get(1 + scheduleIndex * 3);
			final Chromosome startTimeChromo = chromosomes.get(2 + scheduleIndex * 3);
			final Chromosome minDelayChromo = chromosomes.get(3 + scheduleIndex * 3);

			Schedule s = null;
			ArrayList<Waypoint> usedStations = new ArrayList<Waypoint>();
			ArrayList<BusStartTime> busStartTimes = new ArrayList<BusStartTime>();

			// usedStations hinzuf�gen
			for (int i = 0; i < FrameLauncher.chromoScheduleStationLength; i++) {
				Point p = mappingIP.get(usedStationsChromo.getGene(i));
				// Pr�fen, ob es diesen Punkt �berhaupt gibt:
				if (p == null) {
					// Gibts nicht.
					continue;
				} else {

				}

				// Pr�fen, ob dieser Wegpunkt auf eine wirkliche Station im Chromosom verweist
				if (tmpWaypoints.contains(new Waypoint(p.x + 0.5, p.y + 0.5))) { // Wenn der Punkt valid ist alles gut
					// Diesen Punkt suchen:
					Waypoint toAdd = null;
					for (Waypoint t : tmpWaypoints) {
						if (t.isSame(new Waypoint(p.x + 0.5, p.y + 0.5))) {
							toAdd = t;
							break;
						}
					}
					if (toAdd != null) {
						usedStations.add(toAdd);
					} else {
						System.out.println("Komischer fehler");
					}
				} else { // ansonsten ist es ein Fehler
					// Bisher wird die Station dann einfach ignoriert und nicht angefahren
				}
			}
			
			// Startzeit hinzuf�gen
			for (int i = 0; i < startTimeChromo.getLength(); i += 2) {
				int startTime = startTimeChromo.getGene(i);
				int direction = startTimeChromo.getGene(i + 1);
				if (startTime > 0) { // Wenn die Startzeit kleiner 0 ist soll sie nicht hinzugef�gt werden
					BusDirection d;
					if (direction % 2 == 0) { // Bei gerader Zahl soll die Richtung NORMAL sein
						d = BusDirection.NORMAL;
					} else {
						d = BusDirection.REVERSE;
					}
					busStartTimes.add(new BusStartTime(startTime, d));
				} else {
					// Nicht hinzuf�gen
				}
			}

			// angefahrene Stationen filtern, eine Station kann nicht zweimal hintereinander
			// angefahren werden:
			Iterator<Waypoint> iterator = usedStations.iterator();
			Waypoint last = null;
			while (iterator.hasNext()) {
				Waypoint element = iterator.next();
				if (last != null) {
					if (last.isSame(element)) {
						iterator.remove();
					}
				}
				last = element;
			}
			
			// minDelay aus dem Chromosom herausfinden
			int minDelay = minDelayChromo.getGene(0);

			// Schedule nur erzeugen, wenn zwei Stationen angefahren werden und es
			// mindestens eine Startzeit gibt.
			if (usedStations.size() >= 2 && busStartTimes.size() >= 1) {
				s = new Schedule(usedStations, busStartTimes, minDelay, "NAME einf�gen, bitte :/");
				schedules.add(s);
			} else {

			}
		}

		back.setSchedules(schedules);
		back.setStations(stations);
		return back;
	}
}
