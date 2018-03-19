package com.trafficsim.genericalgorithm;

public class Units {

	public static final float SECONDS_PER_TICK = 3;
	public static final float METERS_PER_TILE = 300;

	public static float kmhToTilesPerTick(float kmh) {
		return kmh * SECONDS_PER_TICK / 3.6f / METERS_PER_TILE;
	}
	
	public static float tickdelayToSimseconds(float tickdelay) {
		return 1000 * SECONDS_PER_TICK / tickdelay;
	}
	
	public static float ticksToSeconds(float ticks) {
		return ticks * SECONDS_PER_TICK;
	}
	
	public static float hoursToTicks(float hours) {
		return hours * 3600 / SECONDS_PER_TICK;
	}
	
	public static long getSeconds(long seconds) {
		return seconds % 60;
	}
	
	public static long getMinutes(long seconds) {
		return seconds / 60 % 60;
	}
	
	public static long getHours(long seconds) {
		return seconds / 60 / 60;
	}
	
	public static String getSSMMHH(long seconds) {
		String s = "" + getSeconds(seconds);
		String m = "" + getMinutes(seconds);
		String h = "" + getHours(seconds);

		while (s.length() < 2) s = "0" + s;
		while (m.length() < 2) m = "0" + m;
		while (h.length() < 2) h = "0" + h;
		
		return h + ":" + m + ":" + s;
	}
}
