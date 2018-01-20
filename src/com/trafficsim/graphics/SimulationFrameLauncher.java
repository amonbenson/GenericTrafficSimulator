package com.trafficsim.graphics;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.graphics.consolepane.InfoConsolePane;
import com.trafficsim.graphics.consolepane.PersonConsolePane;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

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
		
		float[][][] townLandscape = Simulation.testTown();
		Town town = new Town(townLandscape.length, townLandscape[0].length);
		simulation = new Simulation(town);
		town.generateTiles(townLandscape);
		Blueprint blueprint = new Blueprint(townLandscape);
		ArrayList<Schedule> schedules = new ArrayList<Schedule>(); //Liste mit allen Linien
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>(); //Alle jemals genutzten Punkte für Stationen dürfen nur einmal erzeugt werden! Dies ist ein Hilfsarray für die interne Erzeugung und wird später nicht benötigt (ist optional).
		ArrayList<Waypoint> stations = new ArrayList<Waypoint>(); //Liste mit allen Stationen, welche generell vom Chromosom erzeugt werden.
		Waypoint w1 = new Waypoint(1.5, 1.5); //WICHTIG: Die Koordinaten müssen mit .5 aufhören. Ist einfach so.
		Waypoint w2 = new Waypoint(3.5, 1.5);
		Waypoint w3 = new Waypoint(3.5, 4.5);
		Waypoint w4 = new Waypoint(9.5, 4.5);
		waypoints.add(w1); //Das Zufügen dient nur zur Hilfe und ist optional
		waypoints.add(w2);
		waypoints.add(w3);
		waypoints.add(w4);
		Schedule s1 = null; //s1 steht für "schedule1"
		ArrayList<BusStartTime> s1StartTimes = new ArrayList<BusStartTime>();
		ArrayList<Waypoint> s1Stations = new ArrayList<Waypoint>();
		s1StartTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		s1Stations.add(w1); //Hier MUSS auf die zuvor erzeugten Wegpunkte zurückgegriffen werden
		s1Stations.add(w3);
		s1Stations.add(w4);
		s1 = new Schedule(s1Stations, s1StartTimes, 0, "Name");
		schedules.add(s1);
		stations.add(w1); //WICHTIG: dieser Punkt muss zuvor bereits erzeugt worden sein (siehe Schritt 6.2). Deswegen wird hier w1 verwendet.
		stations.add(w2);
		stations.add(w3);
		stations.add(w4);
		blueprint.setSchedules(schedules); //Alle Linien setzen
		blueprint.setStations(waypoints); //Alle Punkte setzen, auf welchen Straßen zu Stationen umgewandelt werden sollen
		town.setBlueprint(blueprint);
		blueprint.generate(town);
		town.applyBlueprint();
		

		
		
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
		updater = new AutoUpdater(this);
		
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
