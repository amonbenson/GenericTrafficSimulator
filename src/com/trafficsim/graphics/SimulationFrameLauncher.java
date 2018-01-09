package com.trafficsim.graphics;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.trafficsim.generic.Chromosom;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class SimulationFrameLauncher {
	
	// high dpi
	public static final boolean IS_HIGH_DPI = Toolkit.getDefaultToolkit().getScreenResolution() >= 216;
	
	private JFrame frame;
	
	// Town auto updater
	public AutoUpdater updater;
	
	// Town rendering
	private TownDesktopPane townDesktopPane;
	
	// Person list and event displaying consoles
	private InfoConsolePane infoConsolePane;
	private PersonConsolePane personConsolePane;
	
	public Simulation simulation;
	
	public SimulationFrameLauncher() {
		// TOWN ERSTELLEN
		simulation = new Simulation( new Town(10, 10));
		simulation.getTown().generateTiles(Simulation.testTown()); //Landschaftskarte
		
		
		Chromosom testing = Chromosom.randomChromosom(Simulation.testTown());
		simulation.getTown().setChromosom(testing);

		testing.generate(simulation.getTown());
		simulation.getTown().applyChromosom();
		
		
		// BUS SCHEDULES ERSTELLEN
		/*
		Chromosom c = new Chromosom();
		ArrayList<Schedule> schedules = new ArrayList<Schedule>();
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		/*
		Waypoint w1 = new Waypoint(0.5, 0.5);
		Waypoint w2 = new Waypoint(0.5, 2.5);
		Waypoint w3 = new Waypoint(0.5, 5.5);
		Waypoint w4 = new Waypoint(1.5, 7.5);
		Waypoint w5 = new Waypoint(0.5, 9.5);
		Waypoint w6 = new Waypoint(3.5, 5.5);
		Waypoint w7 = new Waypoint(5.5, 6.5);
		Waypoint w8 = new Waypoint(6.5, 9.5);
		
		waypoints.add(w1);
		waypoints.add(w2);
		waypoints.add(w3);
		waypoints.add(w4);
		waypoints.add(w5);
		ArrayList<BusStartTime> startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(5, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "187"));

		waypoints = new ArrayList<Waypoint>();
		waypoints.add(w4);
		waypoints.add(w7);
		
		startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "188"));
		
		
		waypoints = new ArrayList<Waypoint>();
		waypoints.add(w5);
		waypoints.add(w6);
		waypoints.add(w7);
		waypoints.add(w8);
		startTimes = new ArrayList<BusStartTime>();
		startTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		schedules.add(new Schedule(waypoints, startTimes, 0, "100"));
		
		c.setSchedules(schedules);
		
		ArrayList<Point> stations = new ArrayList<Point>();
		/*
		stations.add(new Point(0, 0));
		stations.add(new Point(0, 2));
		stations.add(new Point(0, 5));
		stations.add(new Point(1, 7));
		stations.add(new Point(0, 9));
		stations.add(new Point(3, 5));
		stations.add(new Point(5, 6));
		stations.add(new Point(6, 9));
		c.setStations(stations);
		simulation.getTown().setChromosom(c);
		simulation.getTown().applyChromosom();
		*/
		
		
		
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
		
		infoConsolePane = new InfoConsolePane(this, simulation.getTown());
		JScrollPane ecScroll = new JScrollPane(infoConsolePane);
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
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "town autoupdate");
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("PLUS"), "town autoupdate faster");
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("MINUS"), "town autoupdate slower");
		townDesktopPane.getInputMap().put(KeyStroke.getKeyStroke("S"), "print statistics");
		
		townDesktopPane.getActionMap().put("town update", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updater.stop();
				simulation.getTown().update();
				townDesktopPane.repaint();
			}
		});
		townDesktopPane.getActionMap().put("town autoupdate", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (updater.isRunning()) updater.stop();
				else updater.start();
			}
		});
		townDesktopPane.getActionMap().put("town autoupdate faster", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updater.faster();
				infoConsolePane.repaint();
			}
		});
		townDesktopPane.getActionMap().put("town autoupdate slower", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updater.slower();
				infoConsolePane.repaint();
			}
		});
		townDesktopPane.getActionMap().put("print statistics", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				simulation.getTown().getStatistics().print();
			}
		});
		
		// Make visible
		frame.setVisible(true);
		
		// Update scroll bar size
		personConsolePane.updatePreferredSize();
		infoConsolePane.updatePreferredSize();
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

	public InfoConsolePane getInfoConsolePane() {
		return infoConsolePane;
	}
	
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		if (simulation == null) return;
		
		Town town = simulation.getTown();
		if (town == null) return;
		
		townDesktopPane.setTown(town);
		infoConsolePane.setTown(town);
		personConsolePane.setTown(town);
	}
	
	
	public static int highDPI(int value) {
		if (IS_HIGH_DPI) return value * 2;
		return value;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	public static void main(String[] args) throws InterruptedException {
		new SimulationFrameLauncher();
	}
	
}
