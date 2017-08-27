package com.trafficsim.graphics;

import java.awt.Graphics;
import java.util.ArrayList;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Person;
import com.trafficsim.town.Town;

public class PersonConsolePane extends ConsolePane {
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	public PersonConsolePane(FrameLauncher frameLauncherContext, Town town) {
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		ArrayList<String> lines = new ArrayList<String>();
		
		// List all persons
		lines.add("%A0A0A0Person List");
		lines.add("ID\tName");
		lines.add("");
		lines.add("%C00000town.getPersons() funktioniert noch nicht?");
		lines.add("%C00000oder verwende ich das falsch?");
		for (Person person : town.getPersons()) {
			lines.add(person.getID() + "\t" + person.getName());
		}
		lines.add("");
		
		// List all busses
		lines.add("%A0A0A0Bus List");
		for (Bus bus : town.getBusses()) {
			lines.add(bus.getSchedule().getSchedule().getName());
		}
		setLines(lines);
		
		// Repaint the super class
		super.paintComponent(g);
	}
}
