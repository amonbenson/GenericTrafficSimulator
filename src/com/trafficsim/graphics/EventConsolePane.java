package com.trafficsim.graphics;

import java.awt.Graphics;
import java.util.ArrayList;

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
		// Get all events and write them into the lines array
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("Start\tEvent Type");
		for (Event event : town.getEvents()) {
			String line = "%FFFFFF";
			if (town.getTime() > event.getStartTime()) line = "%FF0000";
			line += event.getStartTime() + "\t" + event.getClass().getSimpleName();
			lines.add(line);
		}
		setLines(lines);
		
		// Repaint the super class
		super.paintComponent(g);
	}
}
