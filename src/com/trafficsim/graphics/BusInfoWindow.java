package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Person;
import com.trafficsim.town.Town;

public class BusInfoWindow extends UIWindow {
	
	public static final int PERSON_LIST_Y = 160;
	
	private Town town;
	private Bus bus;
	
	public BusInfoWindow(GUI gui, Town town, Bus bus) {
		super(gui);
		
		this.town = town;
		this.bus = bus;
	}

	@Override
	public void repaintContent(int cw, int ch, Graphics2D g) {
		g.setColor(Color.black);
		g.drawString("Bus Linie " + bus.getSchedule().getSchedule().getName(), 10, 40);
		g.drawString("x: " + (Math.round(bus.getX() * 10) / 10.0) + ", y:" + (Math.round(bus.getY() * 10) / 10.0), 10, 80);
		g.drawString("Anzahl Personen: "+bus.getPersons().size(), 10, 120);
		
		// Draw persons
		int yPos = PERSON_LIST_Y;
		Iterator<Person> it = bus.getPersons().iterator();
		while (it.hasNext()) {
			Person person = it.next();
			g.drawString(person.getID() + ": " + person.getName(), 10, yPos);
			
			yPos += 40;
		}
	}

	@Override
	public void mouseInputEvent(MouseEvent e, int tx, int ty, GUI.MouseEventType type) {
		
	}
}
