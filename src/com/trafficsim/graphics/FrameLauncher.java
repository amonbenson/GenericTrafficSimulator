package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

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
		Bus bus = new Bus(1, 1, 5);
		bus.getWaypoints().add(new Waypoint(0, 1));
		town.getBusses().add(bus);
		
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
		
		//Busse zeichnen:
		for (Bus b : town.getBusses()) {
			g.setColor(Color.black);
			g.fillRect((int) (b.getX() * tileSize - tileSize / 10), (int) (b.getY() * tileSize - tileSize / 10), 
					(int) (tileSize / 5), (int) (tileSize / 5) );
		}
	
		

	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
		//Temporärer KeyListener:
		class KListener implements KeyListener {
			private FrameLauncher frame;

			public KListener(FrameLauncher frame) {
				this.frame = frame;
			}
			
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					frame.town.update();
					frame.repaint();
				}
			}
		}
		
		FrameLauncher frame = new FrameLauncher();
		frame.setFocusable(true);
		frame.addKeyListener(new KListener(frame));
	}
	

}
