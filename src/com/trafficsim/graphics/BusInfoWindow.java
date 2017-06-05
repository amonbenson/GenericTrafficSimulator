package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Town;

public class BusInfoWindow extends UIWindow {
	
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
	}

	@Override
	public void mouseInputEvent(MouseEvent e, int tx, int ty, GUI.MouseEventType type) {
		
	}
}
