package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import com.trafficsim.graphics.GUI.MouseEventType;
import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;

public class TownRenderer implements UIElement {

	public static final double BUS_SIZE = 0.5;
	
	private GUI gui;
	private Town town;
	
	private double tileSize;
	
	public TownRenderer(GUI gui, Town town) {
		this.gui = gui;
		this.town = town;
		
		tileSize = 1;
	}

	public void repaint(int screenW, int screenH, Graphics2D g) {
		Tile[][] tiles = town.getTiles();
		
		tileSize = Math.min(screenW / (double) town.getSizeX(), screenH / (double) town.getSizeY());
		
		for (int x = 0; x < town.getSizeX(); x++) {
			for (int y = 0; y < town.getSizeY(); y++) {
				Tile tile = tiles[x][y];
				int dx = (int) (x * tileSize);
				int dy = (int) (y * tileSize);
				int ds = (int) tileSize + 1;
				
				if (tile instanceof StreetTile) {
					StreetTile s = (StreetTile) tile;
					if (s.isStation()) {
						g.setColor(Color.LIGHT_GRAY);
					} else {
						g.setColor(Color.gray);
					}
				}
				if (tile instanceof HouseTile) {
					g.setColor(Color.green);
				}
				g.fillRect(dx, dy, ds, ds);
				if (tile instanceof HouseTile) {
					g.setColor(Color.white);
					g.drawString(String.valueOf(((HouseTile) tile).getNumberPersons()), dx+(int)(tileSize/2), dy+(int)(tileSize/2));
				} else if (tile instanceof StreetTile) {
					g.setColor(Color.white);
					g.drawString(String.valueOf(((StreetTile) tile).getPersons().size()), dx+(int)(tileSize/2), dy+(int)(tileSize/2));
				}
				
				g.setColor(Color.black);
				g.drawRect(dx, dy, ds, ds);
			}
		}
		
		//Busse zeichnen:
		for (Bus b : town.getBusses()) {
			g.setColor(Color.black);
			g.fillRect((int) (b.getX() * tileSize - tileSize * BUS_SIZE / 2), (int) (b.getY() * tileSize - tileSize * BUS_SIZE / 2), 
					(int) (tileSize * BUS_SIZE), (int) (tileSize * BUS_SIZE) );
		}
	}

	public boolean addMouseInputEvent(MouseEvent e, MouseEventType type) {
		if (type == GUI.MouseEventType.CLICKED) {
			// Check if user clicked on bus
			Iterator<Bus> busIt = town.getBusses().iterator();
			while (busIt.hasNext()) {
				Bus bus = busIt.next();
				
				int busMidX = (int) (bus.getX() * tileSize);
				int busMidY = (int) (bus.getY() * tileSize);
				int busSize = (int) (tileSize * BUS_SIZE);
				
				if (new Rectangle(busMidX - busSize / 2, busMidY - busSize / 2, busSize, busSize).contains(e.getPoint())) {
					BusInfoWindow w = new BusInfoWindow(gui, town, bus);
					w.setX(busMidX + busSize);
					w.setY(busMidY + busSize);
					w.setVisible(true);
					gui.addUIWindow(w);
					
					gui.repaint();
				}
			}
		}
		
		// Mouse events will always be processed by the town, so return "catched" every time
		return true;
	}
}
