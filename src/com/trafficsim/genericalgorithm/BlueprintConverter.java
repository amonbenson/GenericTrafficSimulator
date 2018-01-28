package com.trafficsim.genericalgorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Waypoint;

public class BlueprintConverter {
	public static Blueprint convert(int[] chromosome, float[][][] map) {
		Blueprint back = new Blueprint(map);

		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> stations = new ArrayList<Waypoint>();
		Map<Point, Integer> mappingPI = Blueprint.townToMappingPI(map);
		
		//Stationen setzen:
		
		
		back.setSchedules(schedules);
		back.setStations(stations);
		return back;
	}
}
