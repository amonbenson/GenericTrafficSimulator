package com.trafficsim.graphics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Person;
import com.trafficsim.town.Town;

public class PersonConsolePane extends ConsolePane {
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	private int linePersonList, lineBusList; // These define the line (y position) where each list actually starts (without headline and stuff)
	
	public PersonConsolePane(FrameLauncher frameLauncherContext, Town town) {
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
		
		linePersonList = 0;
		lineBusList = 0;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		clear();
		
		// List all persons
		append("%A0A0A0Person List (" + town.getPersons().size() + ")");
		append("ID\tName");
		linePersonList = getNumLines();
		
		if (town.getPersons().isEmpty()) append("-\t-");
		Iterator<Person> personIt = town.getPersons().iterator();
		while (personIt.hasNext()) {
			Person person = personIt.next();
			append(person.getID() + "\t" + person.getName());
		}
		append("");
		
		// List all busses
		append("%A0A0A0Bus List (" + town.getBusses().size() + ")");
		lineBusList = getNumLines();
		
		if (town.getBusses().isEmpty()) append("-");
		Iterator<Bus> busIt = town.getBusses().iterator();
		while (busIt.hasNext()) {
			Bus bus = busIt.next();
			append(bus.getSchedule().getSchedule().getName());
		}
		
		// Repaint the super class
		super.paintComponent(g);
	}

	@Override
	public void lineClicked(int line, String content) {
		if (line >= linePersonList && line < linePersonList + town.getPersons().size()) {
			Person person = town.getPersons().get(line - linePersonList);
			frameLauncherContext.getTownDesktopPane().createPersonInfoFrame(person, 0, 0);
		}
		
		if (line >= lineBusList && line < lineBusList + town.getBusses().size()) {
			Bus bus = town.getBusses().get(line - lineBusList);
			frameLauncherContext.getTownDesktopPane().createBusInfoFrame(bus, 0, 0);
		}
	}
}
