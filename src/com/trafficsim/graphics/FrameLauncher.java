package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Bus;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Route;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class FrameLauncher extends JComponent {
	
	private JFrame frame;
	private Simulation simulation;
	
	private int tileSize;
	
	public FrameLauncher() {
		// TOWN ERSTELLEN
		simulation = new Simulation( new Town(7, 7));
		simulation.getTown().generateTiles(Simulation.randomTown(7, 7));
		Chromosom c = new Chromosom();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(2.5, 2.5));
		waypoints.add(new Waypoint(2.5, 4.5));
		waypoints.add(new Waypoint(4.5, 4.5));
		waypoints.add(new Waypoint(4.5, 2.5));
		ArrayList<BusStartTime> startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "187"));
		c.setSchedules(schedules);
		
		ArrayList<Point> stations = new ArrayList<Point>();
		stations.add(new Point(4, 2));
		stations.add(new Point(2, 4));
		stations.add(new Point(2, 2));
		stations.add(new Point(4, 4));
		c.setStations(stations);
		
		simulation.getTown().setChromosom(c);
		simulation.getTown().applyChromosom();
		
		// FRAME GEDÖNSE
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(this);
		
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		tileSize = Math.min(getWidth() / simulation.getTown().getSizeX(), getHeight() / simulation.getTown().getSizeY());
	}
	
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		
		Tile[][] tiles = simulation.getTown().getTiles();
		
		for (int x = 0; x < simulation.getTown().getSizeX(); x++) {
			for (int y = 0; y < simulation.getTown().getSizeY(); y++) {
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
		for (Bus b : simulation.getTown().getBusses()) {
			g.setColor(Color.black);
			g.fillRect((int) (b.getX() * tileSize - tileSize / 10), (int) (b.getY() * tileSize - tileSize / 10), 
					(int) (tileSize / 5), (int) (tileSize / 5) );
			g.setColor(Color.white);
			g.drawString("X:"+b.getX(), (int) (b.getX() * tileSize), (int) (b.getY() * tileSize));
			g.drawString("Y:"+b.getY(), (int) (b.getX() * tileSize), (int) (b.getY() * tileSize)+20);
			
		}
	
		

	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
		//Temporärer KeyListener, soll noch ausgelagert werden:
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
					frame.simulation.getTown().update();
					frame.repaint();
				}
			}
		}
		
		
		FrameLauncher frame = new FrameLauncher();
		frame.setFocusable(true);
		frame.simulation.getTown().init();
		frame.addKeyListener(new KListener(frame));
	}
	

}
