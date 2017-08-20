package com.trafficsim.graphics;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class FrameLauncher {
	
	// low dpi
	public static final boolean IS_HIGH_DPI = Toolkit.getDefaultToolkit().getScreenResolution() >= 240;
	
	private JFrame frame;
	
	// Town auto updater
	private AutoUpdater updater;
	
	// Town rendering
	private TownDesktopPane townDesktopPane;
	
	// Event displaying
	private EventConsolePane eventConsolePane;
	
	public Simulation simulation;
	
	public FrameLauncher() {
		// TOWN ERSTELLEN
		simulation = new Simulation( new Town(7, 6));
		simulation.getTown().generateTiles(Simulation.randomTown(7, 6)); //Landschaftskarte
		
		// BUS SCHEDULES ERSTELLEN
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
		
		// Set laf
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't set LookAndFeel.");
			e.printStackTrace();
		}
		
		// Create all components
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		townDesktopPane = new TownDesktopPane(this, simulation.getTown());
		frame.add(townDesktopPane);
		
		eventConsolePane = new EventConsolePane(this, simulation.getTown());
		frame.add(BorderLayout.EAST, eventConsolePane);

		frame.setSize(highDPI(800), highDPI(600));
		frame.setLocationRelativeTo(null);
		
		// Init the auto updater
		updater = new AutoUpdater(frame, simulation.getTown());
		
		// Key bindings
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("U"), "town update");
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("R"), "town revert");
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "town autoupdate");

		townDesktopPane.getActionMap().put("town update", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updater.stop();
				simulation.getTown().update();
				townDesktopPane.repaint();
			}
		});
		townDesktopPane.getActionMap().put("town revert", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updater.stop();
				simulation.getTown().revert();
				townDesktopPane.repaint();
			}
		});
		townDesktopPane.getActionMap().put("town autoupdate", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (updater.isRunning()) updater.stop();
				else updater.start();
			}
		});
		
		// Make visible
		frame.setVisible(true);
	}
	
	public JFrame getFrame() {
		return frame;
	}

	public TownDesktopPane getTownDesktopPane() {
		return townDesktopPane;
	}

	public EventConsolePane getEventConsolePane() {
		return eventConsolePane;
	}
	
	
	public static int highDPI(int value) {
		if (IS_HIGH_DPI) return value * 2;
		return value;
	}

	public static void main(String[] args) throws InterruptedException {
		new FrameLauncher();
	}
	
}
