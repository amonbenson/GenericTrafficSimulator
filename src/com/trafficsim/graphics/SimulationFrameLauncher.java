package com.trafficsim.graphics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.genericalgorithm.BlueprintConverter;
import com.trafficsim.genericalgorithm.FrameLauncher;
import com.trafficsim.graphics.consolepane.InfoConsolePane;
import com.trafficsim.graphics.consolepane.PersonConsolePane;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.BusDirection;
import com.trafficsim.town.BusStartTime;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class SimulationFrameLauncher {
	
	private JFrame frame;
	
	// Town auto updater
	public AutoUpdater updater;
	
	// Town rendering
	private TownDesktopPane townDesktopPane;
	
	// Person list and event displaying consoles
	private InfoConsolePane infoConsolePane;
	private PersonConsolePane personConsolePane;
	
	public Simulation simulation;
	
	private Town town;
	
	public SimulationFrameLauncher() {
		// TOWN ERSTELLEN
		
		Random random = new Random(); //Seed ist optional
		
		
		float[][][] townLandscape = Simulation.testTown();
		town = new Town(townLandscape.length, townLandscape[0].length, random);
		simulation = new Simulation(town);
		town.generateTiles(townLandscape);
		Blueprint blueprint = new Blueprint(townLandscape);
		ArrayList<Schedule> schedules = new ArrayList<Schedule>(); //Liste mit allen Linien
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>(); //Alle jemals genutzten Punkte f�r Stationen d�rfen nur einmal erzeugt werden! Dies ist ein Hilfsarray f�r die interne Erzeugung und wird sp�ter nicht ben�tigt (ist optional).
		ArrayList<Waypoint> stations = new ArrayList<Waypoint>(); //Liste mit allen Stationen, welche generell vom Chromosom erzeugt werden.
		Waypoint w1 = new Waypoint(1.5, 1.5); //WICHTIG: Die Koordinaten m�ssen mit .5 aufh�ren. Ist einfach so.
		Waypoint w2 = new Waypoint(3.5, 1.5);
		Waypoint w3 = new Waypoint(3.5, 4.5);
		Waypoint w4 = new Waypoint(8.5, 4.5);
		Waypoint w5 = new Waypoint(2.5, 7.5);
		Waypoint w6 = new Waypoint(7.5, 1.5);
		Waypoint w7 = new Waypoint(7.5, 7.5);
		Waypoint w8 = new Waypoint(1.5, 4.5);
		
		waypoints.add(w1); //Das Zuf�gen dient nur zur Hilfe und ist optional
		waypoints.add(w2);
		waypoints.add(w3);
		waypoints.add(w4);
		waypoints.add(w5);
		waypoints.add(w6);
		waypoints.add(w7);
		waypoints.add(w8);
		
		Schedule s1 = null; //s1 steht f�r "schedule1"
		ArrayList<BusStartTime> s1StartTimes = new ArrayList<BusStartTime>();
		ArrayList<Waypoint> s1Stations = new ArrayList<Waypoint>();
		s1StartTimes.add(new BusStartTime(0, BusDirection.NORMAL));
		s1Stations.add(w1); //Hier MUSS auf die zuvor erzeugten Wegpunkte zur�ckgegriffen werden
		s1Stations.add(w2);
		s1Stations.add(w3);
		s1Stations.add(w4);
		s1Stations.add(w5);
		s1Stations.add(w6);
		s1 = new Schedule(s1Stations, s1StartTimes, 0, "1");
		schedules.add(s1);
		
		
		Schedule s2 = null;
		ArrayList<BusStartTime> s2StartTimes = new ArrayList<BusStartTime>();
		ArrayList<Waypoint> s2Stations = new ArrayList<Waypoint>();
		s2StartTimes.add(new BusStartTime(5, BusDirection.NORMAL));
		s2Stations.add(w1); //Hier MUSS auf die zuvor erzeugten Wegpunkte zur�ckgegriffen werden
		s2Stations.add(w3);
		s2Stations.add(w4);
		s2Stations.add(w8);
		s2 = new Schedule(s2Stations, s2StartTimes, 0, "2");
		schedules.add(s2);
		

		
		Schedule s3 = null;
		ArrayList<BusStartTime> s3StartTimes = new ArrayList<BusStartTime>();
		ArrayList<Waypoint> s3Stations = new ArrayList<Waypoint>();
		s3StartTimes.add(new BusStartTime(5, BusDirection.NORMAL));
		s3Stations.add(w7); //Hier MUSS auf die zuvor erzeugten Wegpunkte zur�ckgegriffen werden
		s3Stations.add(w8);
		s3Stations.add(w1);
		s3 = new Schedule(s3Stations, s3StartTimes, 0, "3");
		schedules.add(s3);

		stations.add(w1); //WICHTIG: dieser Punkt muss zuvor bereits erzeugt worden sein (siehe Schritt 6.2). Deswegen wird hier w1 verwendet.
		stations.add(w2);
		stations.add(w3);
		stations.add(w4);
		stations.add(w5);
		stations.add(w6);
		stations.add(w7);
		stations.add(w8);
		blueprint.setSchedules(schedules); //Alle Linien setzen
		blueprint.setStations(stations); //Alle Punkte setzen, auf welchen Stra�en zu Stationen umgewandelt werden sollen
		town.setBlueprint(blueprint);
		blueprint.generate(town);
		town.applyBlueprint();
		town.getStatistics().print(town);
		
		/*
		float[][][] townLandscape = Simulation.testTown();
		town = new Town(townLandscape.length, townLandscape[0].length, random);
		simulation = new Simulation(town);
		town.generateTiles(townLandscape);
		
		int[] chromo = {1, 2, 3, 0, 10004, 
			-1, 1, 3, 3, 1, //Stationen, die angefahren werden
			0, 10, //Startzeit, Richtung
			1, 10, 
			-1, -2, 
			-100, 0, 
			-1, 1};
		
		Blueprint testing = BlueprintConverter.convertStandard(chromo, townLandscape);
		System.out.println(testing);
		town.setBlueprint(testing);
		testing.generate(town);
		town.applyBlueprint();
		*/
		// Create all components
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		townDesktopPane = new TownDesktopPane(this, town);
		frame.add(townDesktopPane);
		
		personConsolePane = new PersonConsolePane(this, town);
		JScrollPane pcScroll = new JScrollPane(personConsolePane);
		pcScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pcScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(BorderLayout.EAST, pcScroll);
		
		infoConsolePane = new InfoConsolePane(this, town);
		JScrollPane ecScroll = new JScrollPane(infoConsolePane);
		ecScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ecScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.add(BorderLayout.WEST, ecScroll);

		frame.setSize(GraphicsFX.highDPI(1300), GraphicsFX.highDPI(800));
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
				simulation.getTown().getStatistics().print(town);
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

	public static void main(String[] args) {
		// Set the laf if we call the frame launcher from here
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't set LookAndFeel.");
			e.printStackTrace();
		}
		
		new SimulationFrameLauncher();
	}
	
}
