package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.trafficsim.town.HouseTile;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;

public class FrameLauncher extends JComponent {
	
	private JFrame frame;
	private Town town;
	
	public FrameLauncher() {
		// TOWN ERSTELLEN
		town = new Town(2,2);
		Tile[][] tiles = new Tile[2][2];
		tiles[0][0] = new StreetTile(0, 0, 5f);
		tiles[0][1] = new StreetTile(0, 1, 2f);
		tiles[1][0] = new HouseTile(1, 0, 5);
		tiles[1][1] = new HouseTile(1, 1, 10);
		town.setTiles(tiles);
		
		// FRAME GEDÖNSE
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(this);
		
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		
		double tileSize = Math.min(getWidth() / town.getSizeX(), getHeight() / town.getSizeY());
		Tile[][] tiles = town.getTiles();
		
		for (int x = 0; x < town.getSizeX(); x++) {
			for (int y = 0; y < town.getSizeY(); y++) {
				Tile tile = tiles[x][y];
				int dx = (int) (x * tileSize);
				int dy = (int) (y * tileSize);
				int ds = (int) tileSize + 1;
				
				if (tile instanceof StreetTile) g.setColor(Color.gray);
				if (tile instanceof HouseTile) g.setColor(Color.green);
				g.fillRect(dx, dy, ds, ds);
				
				g.setColor(Color.black);
				g.drawRect(dx, dy, ds, ds);
			}
		}
	}
	
	public static void main(String[] args) {
		new FrameLauncher();
	}
}
