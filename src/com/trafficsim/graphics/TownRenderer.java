package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;

public class TownRenderer implements UIElement {

	private GUI gui;
	private Town town;
	
	public TownRenderer(GUI gui, Town town) {
		this.gui = gui;
		this.town = town;
	}

	public void repaint(int screenW, int screenH, Graphics2D g) {
		Tile[][] tiles = town.getTiles();
		
		int tileSize = Math.min(screenW / town.getSizeX(), screenH / town.getSizeY());
		
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
			g.fillRect((int) (b.getX() * tileSize - tileSize / 10), (int) (b.getY() * tileSize - tileSize / 10), 
					(int) (tileSize / 5), (int) (tileSize / 5) );
		}
	}
}
