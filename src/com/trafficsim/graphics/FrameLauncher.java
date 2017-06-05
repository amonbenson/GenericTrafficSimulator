package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Bus;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class FrameLauncher {
	
	private JFrame frame;
	private GUI gui;
	
	public Simulation simulation;
	
	public FrameLauncher() {
		// TOWN ERSTELLEN
		simulation = new Simulation( new Town(7, 6));
		simulation.getTown().generateTiles(Simulation.randomTown(7, 6)); //Landschaftskarte
		
		Chromosom c = new Chromosom();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(0.5, 0.5));
		waypoints.add(new Waypoint(3.5, 0.5));
		waypoints.add(new Waypoint(3.5, 3.5));
		waypoints.add(new Waypoint(3.5, 0.5));
		ArrayList<BusStartTime> startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(5, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "187"));
		
		waypoints = new ArrayList<Waypoint>();
		waypoints.add(new Waypoint(0.5, 5.5));		
		waypoints.add(new Waypoint(3.5, 5.5));
		waypoints.add(new Waypoint(3.5, 3.5));
		waypoints.add(new Waypoint(5.5, 3.5));
		waypoints.add(new Waypoint(3.5, 3.5));
		waypoints.add(new Waypoint(3.5, 5.5));		
		
		startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "188"));
		c.setSchedules(schedules);
		
		ArrayList<Point> stations = new ArrayList<Point>();
		stations.add(new Point(0, 0));
		stations.add(new Point(3, 0));
		stations.add(new Point(3, 3));
		stations.add(new Point(3, 5));
		stations.add(new Point(5, 3));
		stations.add(new Point(0, 5));
		c.setStations(stations);
		
		simulation.getTown().setChromosom(c);
		simulation.getTown().applyChromosom();
		
		// FRAME GEDÖNSE
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gui = new GUI();
		gui.setSimulation(simulation);
		frame.add(gui);

		frame.setSize(800, 800);
		if (Toolkit.getDefaultToolkit().getScreenResolution() >= 240) { // High DPI
			frame.setSize(frame.getWidth() * 2, frame.getHeight() * 2);
			gui.setFont(gui.getFont().deriveFont(gui.getFont().getSize() * 4.0f));
		}
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		gui.initListeners();
	}
	
	public static void main(String[] args) throws InterruptedException {
		new FrameLauncher();
	}
	
}
