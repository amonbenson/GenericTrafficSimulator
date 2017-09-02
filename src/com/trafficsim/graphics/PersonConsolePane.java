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
		clear();
		
		// List all persons
		append("%A0A0A0Person List");
		append("ID\tName");
		if (town.getPersons().isEmpty()) append("-\t-");
		for (Person person : town.getPersons()) {
			append(person.getID() + "\t" + person.getName());
		}
		append("");
		
		// List all busses
		append("%A0A0A0Bus List");
		if (town.getBusses().isEmpty()) append("-");
		for (Bus bus : town.getBusses()) {
			append(bus.getSchedule().getSchedule().getName());
		}
		
		// Repaint the super class
		super.paintComponent(g);
	}
}
