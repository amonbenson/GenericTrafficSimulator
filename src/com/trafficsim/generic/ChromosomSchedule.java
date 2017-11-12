package com.trafficsim.generic;

import java.util.ArrayList;
import java.util.Random;

import com.trafficsim.town.BusStartTime;

public class ChromosomSchedule {
	
	public ArrayList<Integer> stations;
	public ArrayList<BusStartTime> busStartTimes;	
	public String name;
	
	/**
	 * Erzeugt einen Fahrplan mit einer zufälligen Zahl zwischen 1 und 1000 als Busnamen
	 */
	public ChromosomSchedule() {
		this(String.valueOf(getRandomBusNumber()));
	}
	
	public ChromosomSchedule(String name) {
		this.name = name;
		stations = new ArrayList<Integer>();
		busStartTimes = new ArrayList<BusStartTime>();
	}

	
	
	
	public static int getRandomBusNumber() {
		Random r = new Random();
		return r.nextInt(1000);
	}
	
}
