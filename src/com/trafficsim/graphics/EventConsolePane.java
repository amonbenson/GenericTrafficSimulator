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
		clear();
		
		// Write the global time
		append("%A0A0A0Info");
		append("Global time (ticks): " + town.getTime());
		append("");
		
		// Write all the key actions
		append("%A0A0A0Key Actions");
		append("Enter\tStart / Pause simulation");
		append("U\tUpdate by one tick");
		append("R\tReverse update by one tick");
		append("");

		// Get all events and write them into the lines array
		append("%A0A0A0Event List");
		append("Time\tType");
		if (town.getEvents().isEmpty()) append("-\t-");
		for (Event event : town.getEvents()) {
			String line = "";
			if (town.getTime() > event.getStartTime()) line = "%606060";
			line += event.getStartTime() + "\t" + event.getClass().getSimpleName();
			append(line);
		}
		
		// Repaint the super class
		super.paintComponent(g);
	}
}
