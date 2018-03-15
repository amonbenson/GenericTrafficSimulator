package com.trafficsim.graphics.consolepane;

import java.awt.Graphics;

import com.trafficsim.genericalgorithm.Units;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.SimulationFrameLauncher;
import com.trafficsim.town.Event;
import com.trafficsim.town.Statistics;
import com.trafficsim.town.Town;

public class InfoConsolePane extends ConsolePane {

	private SimulationFrameLauncher frameLauncherContext;
	private Town town;

	public InfoConsolePane(SimulationFrameLauncher frameLauncherContext, Town town) {
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
	}

	@Override
	public void paintComponent(Graphics g) {
		clear();
		
		// Don't draw console if town is null
		if (town == null) {
			append("No town.");
			super.paintComponent(g);
			return;
		}

		// Write the global time
		append("%A0A0A0Info");
		append("Time:\t" + Units.getSSMMHH(town.getTime()) + " h");
		append("Speed Scale:\t" + (int) Units.tickdelayToSimseconds(frameLauncherContext.updater.getTickSpeed()) + "x Realtime");
		append("");

		// Write all the key actions
		append("%A0A0A0Key Actions");
		append("Enter\tStart / Pause simulation");
		append("U\tUpdate by one tick");
		append("+\tIncrease Speed Scale");
		append("-\tDecrease Speed Scale");
		append("");

		// Get all events and write them into the lines array
		append("%A0A0A0Event List (" + town.getEvents().size() + ")");
		append("Time\tType");
		if (town.getEvents().isEmpty())
			append("-\t-");

		long lastEventTime = 0;
		String lastEventName = "";
		int duplicateEventCount = 0;

		for (Event event : town.getEvents()) {
			if (town.getTime() > event.getStartTime())
				continue;
			
			String line = "";
			long eventTime = event.getStartTime();
			String eventName = event.getClass().getSimpleName();

			if (eventTime == lastEventTime && eventName.equals(lastEventName)) {
				duplicateEventCount++;
			} else {
				if (duplicateEventCount > 0)
					getLines().set(getLines().size() - 1, getLines().get(getLines().size() - 1) + "  x" + (duplicateEventCount + 1));
				duplicateEventCount = 0;

				line += Units.getSSMMHH(eventTime) + " h\t" + eventName;
				append(line);
			}

			lastEventTime = eventTime;
			lastEventName = eventName;
		}
		append("");

		// Draw the statistics
		/*append("%A0A0A0Statistics");

		Statistics s = town.getStatistics();
		append("Route found:\t" + s.getCounterRouteFound());
		append("No Station found:\t" + s.getCounterNoStationFound() + " (" + GraphicsFX
				.round(s.getCounterNoStationFound() / ((float) s.getErrorNoRoute() + s.getCounterRouteFound()) * 100, 2)
				+ "%)");
		// append("No Route found:\t" + s.getCounterNoRouteFound() + " ("
		// + SimulationFrameLauncher.round(s.getCounterNoRouteFound() / ((float)
		// s.getErrorNoRoute()) * 100, 2) + "%)");
		// append("Route same targets:\t" + s.getCountRouteSameTargets() + " ("
		// + SimulationFrameLauncher.round(s.getCountRouteSameTargets() /
		// ((float) s.getErrorNoRoute()) * 100, 2) + "%)");
		append("All Errors:\t" + s.getErrorNoRoute() + " ("
				+ GraphicsFX.round(
						(s.getErrorNoRoute() / (float) (s.getErrorNoRoute() + s.getCounterRouteFound())) * 100, 2)
				+ "%)");
		append("Transport Time:\t" + s.getAverageTravelTime(town));*/

		// Repaint the super class
		super.paintComponent(g);
	}

	@Override
	public void lineClicked(int line, String content) {

	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}
}
