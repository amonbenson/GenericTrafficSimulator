package com.trafficsim.genericalgorithm;

import java.util.ArrayList;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Statistics;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class FitnessEvaluator {

	public static final double DIVIDEND = 100000.0;

	public static final double F_TRAVEL_TIME_PENALTY = 0.1;
	public static final double F_STATIONS_PENALTY = 12666;
	public static final double F_BUSSES_PENALTY = 7273;
	public static final double F_SAME_PATH_PENALTY = 160;
	// public static final double F_PERSON_STILL_TRAVELLING_PENALTY = 3;

	public static double evaluate(Town town) {
		final Statistics statistics = town.getStatistics();
		final double travelTime = statistics.getAverageTravelTime(town);
		final int numStations = town.getBlueprint().getNumberStations();
		final int numBusses = town.getBlueprint().getNumberBusses();

		// Check for doubled routes
		// Compare every bus to every other
		int samePathCount = 0;
		for (int i = 0; i < town.getBusses().size(); i++) {
			for (int j = i + 1; j < town.getBusses().size(); j++) {
				Bus b1 = town.getBusses().get(i);
				Bus b2 = town.getBusses().get(j);
				if (b1.getSchedule().getSchedule().getName() == b2.getSchedule().getSchedule().getName()) continue; // Same
																													// bus
																													// line!

				Schedule s1 = b1.getSchedule().getSchedule();
				Schedule s2 = b2.getSchedule().getSchedule();

				// Compare every path of one bus to every of the other one
				for (int k = 0; k < s1.getWaypointSize() - 1; k++) {
					for (int l = 0; l < s2.getWaypointSize() - 1; l++) {
						Waypoint p11 = s1.getWaypoint(k);
						Waypoint p12 = s1.getWaypoint(k + 1);
						Waypoint p21 = s2.getWaypoint(l);
						Waypoint p22 = s2.getWaypoint(l + 1);

						// Check if the path between the two stations is the
						// same (or the same reversed)
						if (p11.isSame(p21) && p21.isSame(p22) || p11.isSame(p22) && p12.isSame(p21)) {
							samePathCount++;
						}
					}
				}
			}
		}

		//System.out.println(travelTime + " " + numStations + " " + numBusses + " " + samePathCount);
		return DIVIDEND / (F_TRAVEL_TIME_PENALTY * travelTime + F_STATIONS_PENALTY * numStations
				+ F_BUSSES_PENALTY * numBusses + F_SAME_PATH_PENALTY * samePathCount);
	}

}
