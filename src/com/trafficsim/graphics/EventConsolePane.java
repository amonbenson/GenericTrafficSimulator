package com.trafficsim.graphics;

import java.awt.Graphics;
import java.util.ArrayList;

import com.trafficsim.town.BusCreationEvent;
import com.trafficsim.town.Event;
import com.trafficsim.town.Town;

public class EventConsolePane extends ConsolePane {
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	public EventConsolePane(FrameLauncher frameLauncherContext, Town town) {
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		ArrayList<String> lines = new ArrayList<String>();
		
		// Write the global time
		lines.add("%A0A0A0Info");
		lines.add("Global time (ticks): " + town.getTime());
		lines.add("");
		
		// Write all the key actions
		lines.add("%A0A0A0Key Actions");
		lines.add("Enter\tStart / Pause simulation");
		lines.add("U\tUpdate by one tick");
		lines.add("R\tReverse update by one tick");
		lines.add("");

		// Get all events and write them into the lines array
		lines.add("%A0A0A0Event List");
		lines.add("Time\tType");
		for (Event event : town.getEvents()) {
			String line = "";
			if (town.getTime() > event.getStartTime()) line = "%606060";
			line += event.getStartTime() + "\t" + event.getClass().getSimpleName();
			lines.add(line);
		}
		setLines(lines);
		
		// Repaint the super class
		super.paintComponent(g);
	}
}
