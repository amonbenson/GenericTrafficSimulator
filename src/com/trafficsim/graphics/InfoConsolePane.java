package com.trafficsim.graphics;

import java.awt.Graphics;
import java.util.Iterator;

import com.trafficsim.town.Event;
import com.trafficsim.town.Statistics;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class InfoConsolePane extends ConsolePane {
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	public InfoConsolePane(FrameLauncher frameLauncherContext, Town town) {
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		clear();
		
		// Write the global time
		append("%A0A0A0Info");
		append("Time: " + town.getTime());
		append("Ticks per second: " + frameLauncherContext.updater.getTickSpeed());
		append("");
		
		// Write all the key actions
		append("%A0A0A0Key Actions");
		append("Enter\tStart / Pause simulation");
		append("U\tUpdate by one tick");
		append("+\tDecrease Speed");
		append("-\tIncrease Speed");
		append("");

		// Get all events and write them into the lines array
		append("%A0A0A0Event List");
		append("Time\tType");
		if (town.getEvents().isEmpty()) append("-\t-");

		long lastEventTime = 0;
		String lastEventName = "";
		int duplicateEventCount = 0;
		
		for (Event event : town.getEvents()) {
			String line = "";
			long eventTime = event.getStartTime();
			String eventName = event.getClass().getSimpleName();

			if (eventTime == lastEventTime && eventName.equals(lastEventName)) {
				duplicateEventCount++;
			} else {
				if (duplicateEventCount > 0) append("%a0a0a0\t\t\tx" + (duplicateEventCount + 1));
				duplicateEventCount = 0;
				
				if (town.getTime() > event.getStartTime()) line = "%606060";
				
				line += eventTime + "\t" + eventName;
				append(line);
			}

			lastEventTime = eventTime;
			lastEventName = eventName;
		}
		append("");
		
		// Draw the statistics
		append("%A0A0A0Statistics");

		Statistics s = town.getStatistics();
		append("Route found:\t" + s.getCounterRouteFound());
		append("No Station found:\t" + s.getCounterNoStationFound() + " (" + FrameLauncher
				.round(s.getCounterNoStationFound() / ((float) s.getErrorNoRoute() + s.getCounterRouteFound()) * 100, 2)
				+ "%)");
		append("No Route found:\t" + s.getCounterNoRouteFound() + " ("
				+ FrameLauncher.round(s.getCounterNoRouteFound() / ((float) s.getErrorNoRoute()) * 100, 2) + "%)");
		append("Route same targets:\t" + s.getCountRouteSameTargets() + " ("
				+ FrameLauncher.round(s.getCountRouteSameTargets() / ((float) s.getErrorNoRoute()) * 100, 2) + "%)");
		append("All Errors:\t" + s.getErrorNoRoute() + " ("
				+ FrameLauncher.round(
						(s.getErrorNoRoute() / (float) (s.getErrorNoRoute() + s.getCounterRouteFound())) * 100, 2)
				+ "%)");
		append("Transport Time:\t" + s.getMedianTravelTime());
		
		// Repaint the super class
		super.paintComponent(g);
	}

	@Override
	public void lineClicked(int line, String content) {
		
	}
}
