package com.trafficsim.graphics;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
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
	
	// high dpi
	public static final boolean IS_HIGH_DPI = Toolkit.getDefaultToolkit().getScreenResolution() >= 216;
	
	private JFrame frame;
	
	// Town auto updater
	private AutoUpdater updater;
	
	// Town rendering
	private TownDesktopPane townDesktopPane;
	
	// Person list and event displaying consoles
	private EventConsolePane eventConsolePane;
	private PersonConsolePane personConsolePane;
	
	public Simulation simulation;
	
	public FrameLauncher() {
		// TOWN ERSTELLEN
		simulation = new Simulation( new Town(7, 6));
		simulation.getTown().generateTiles(Simulation.randomTown(7, 6)); //Landschaftskarte
		
		// BUS SCHEDULES ERSTELLEN
		Chromosom c = new Chromosom();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		
		Waypoint w1 = new Waypoint(0.5, 0.5);
		Waypoint w2 = new Waypoint(1.5, 2.5);
		Waypoint w3 = new Waypoint(3.5, 3.5);
		Waypoint w4 = new Waypoint(5.5, 3.5);
		Waypoint w5 = new Waypoint(5.5, 5.5);
		waypoints.add(w4);
		waypoints.add(w5);
		ArrayList<BusStartTime> startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(5, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "187"));

		waypoints = new ArrayList<Waypoint>();
		waypoints.add(w1);
		waypoints.add(w2);
		waypoints.add(w3);
		waypoints.add(w4);
		
		startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "188"));
		c.setSchedules(schedules);
		
		waypoints = new ArrayList<Waypoint>();
		waypoints.add(w1);
		waypoints.add(w2);
		waypoints.add(w3);
		startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "100"));
		

		
		ArrayList<Point> stations = new ArrayList<Point>();
		stations.add(new Point(0, 0));
		stations.add(new Point(1, 2));
		stations.add(new Point(3, 3));
		stations.add(new Point(5, 3));
		stations.add(new Point(5, 5));
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
		
		personConsolePane = new PersonConsolePane(this, simulation.getTown());
		JScrollPane pcScroll = new JScrollPane(personConsolePane);
		pcScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(BorderLayout.EAST, pcScroll);
		
		eventConsolePane = new EventConsolePane(this, simulation.getTown());
		JScrollPane ecScroll = new JScrollPane(eventConsolePane);
		ecScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ecScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(BorderLayout.WEST, ecScroll);

		frame.setSize(highDPI(1300), highDPI(800));
		frame.setLocationRelativeTo(null);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
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

	public PersonConsolePane getPersonConsolePane() {
		return personConsolePane;
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
