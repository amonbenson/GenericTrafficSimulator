package com.trafficsim.generic;

import java.util.ArrayList;
import java.util.Random;

import com.trafficsim.town.BusStartTime;

public class BlueprintSchedule {
	
	public ArrayList<Integer> stations;
	public ArrayList<BusStartTime> busStartTimes;	
	public String name;
	
	/**
	 * Erzeugt einen Fahrplan mit einer zufälligen Zahl zwischen 1 und 1000 als Busnamen
	 */
	public BlueprintSchedule(Random r) {
		this(String.valueOf(getRandomBusNumber(r)));
	}
	
	public BlueprintSchedule(String name) {
		this.name = name;
		stations = new ArrayList<Integer>();
		busStartTimes = new ArrayList<BusStartTime>();
	}

	
	public ArrayList<BusStartTime> getBusStartTimes() {
		return busStartTimes;
	}
	
	public static int getRandomBusNumber(Random r) {
		return r.nextInt(1000);
	}
	
}
