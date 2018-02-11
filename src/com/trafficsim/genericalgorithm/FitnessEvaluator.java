package com.trafficsim.genericalgorithm;

import com.trafficsim.town.Statistics;
import com.trafficsim.town.Town;

public class FitnessEvaluator {

	public static final double DIVIDEND = 1000.0;

	public static final double F_STATIONS_PENALTY = 20000;
	public static final double F_BUSSES_PENALTY = 100;

	public static final double F_PERSON_STILL_TRAVELLING_PENALTY = 3;
	
	public static double evaluate(Town town) {
		final Statistics statistics = town.getStatistics();
		final double travelTime = statistics.getAverageTravelTime(town);
		final int numStations = town.getBlueprint().getNumberStations();
		final int numBusses = town.getBlueprint().getNumberBusses();
		
		return DIVIDEND / (travelTime + F_STATIONS_PENALTY * numStations + F_BUSSES_PENALTY * numBusses);
	}

}
