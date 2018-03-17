package com.trafficsim.genericalgorithm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.SimulationFrameLauncher;
import com.trafficsim.graphics.ga.GAFrameLauncher;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

/**
 * This is the main Framelauncher, that starts all the generic stuff and
 * simultates the towns. It consists of one generic algorithm, which will be
 * running until a set limit is reached. Also, we have a town, which will
 * represent the current town, that is simulated. When nothing is beeing
 * simulated, the town may, but doesn't need to be null.
 * 
 * @author Amon
 *
 */
public class FrameLauncher implements Simulator {
	
	public static FrameLauncher defaultFrameLauncher;

	public static Random random;

	/**
	 * Stellt ein, ob das Switchen zwischen Richtungen erlaubt sein soll
	 */
	public static final boolean shouldAllowAlternate = false;

	public static GenericAlgorithm ga;
	public static Population currentPopulation;
	public static boolean isGARunning = false;

	/**
	 * The number of iterations, which the generic algorithm will get through,
	 * until it stops and returns the results.
	 */
	public static int gaRuntime;

	/**
	 * The number of individuals in each generated population.
	 */
	public static int gaPopSize;

	/**
	 * The number of iterations, which a single town will be simulated. One
	 * iteration means one update call, which means one town tick. If the
	 * maximum number is reached, the town will stop simulating, and the fitness
	 * will be calculated.
	 * 
	 */
	public static int townRuntime;

	/**
	 * Stellt ein, wie viele Personen �ber die gesamte Generation generiert
	 * werden
	 */
	public static int townNumberPersons;

	public static float townPersonStopPuffer;

	/**
	 * Sets the time to sleep between two town updates. Should only be used for
	 * debbuging only, cause it slows down the generic algorithm.
	 */
	public static long simulationTickSpeed;

	public static int chromoStationLength;
	public static int chromoScheduleCount;
	public static int chromoScheduleStationLength;
	public static int chromoScheduleStartTimeLength;
	public static int chromoScheduleShouldAlternate;
	public static int chromoCount;

	/**
	 * Represents the currently simulated town. The simulation will take place
	 * in a different thread, so the main thread can concentrate on drawing the
	 * graphics, where this town class can be used to show the process. However,
	 * it may be null, if nothing is simulated.
	 */
	public static Town currentTown;

	/**
	 * The simulation framelauncher will create a jframe to show the current
	 * town.
	 */
	public static SimulationFrameLauncher simFrameLauncher;

	/**
	 * The generic algorithm frame launcher will take care of showing the
	 * current ga's process and displaying a nice family tree of the
	 * individuals.
	 */
	public static GAFrameLauncher gaFrameLauncher;

	public static JFrame fitGraph;

	public static float[][][] map;

	public FrameLauncher() throws InterruptedException {
		// Init random
		random = new Random(System.currentTimeMillis());

		// Heatmap:	blue:	0=house, 255=street
		//			green:	ifhouse: numpersons, ifstreet: speed
		//			red:	interest (how many persons want to go there)
		// Load map and set area station (args: file, populationMin/Max, speedMin/Max, interestMin/Max)
		//map = Simulation.loadHeatMap("res/heatmap.png", 0, 100, Units.kmhToTilesPerTick(10), Units.kmhToTilesPerTick(50), 0, 10);
		map = Simulation.testTown();
		// Tile.AREA_STATION = 4;

		// Logger stuff
		Simulation.logger.setLevel(Level.ALL);
		GAFrameLauncher.logger.setLevel(Level.ALL);

		// Set laf
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't set LookAndFeel.");
			e.printStackTrace();
		}

		// Retrieve the font after setting the laf
		GraphicsFX.retrieveFont();

		// Create the generic algorithm frame launcher
		gaFrameLauncher = new GAFrameLauncher();
		gaFrameLauncher.setFrameLauncherContext(this);

		// Simple fitness graph display
		fitGraph = new JFrame("Fitness");
		fitGraph.add(new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				g.setColor(Color.black);
				g.fillRect(0, 0, getWidth(), getHeight());
				((Graphics2D) g).setStroke(new BasicStroke(3));

				if (gaFrameLauncher.descendantTreePane == null) return;
				final GenerationHistory history = gaFrameLauncher.descendantTreePane.getHistory();
				if (history == null) return;
				if (history.getAvgFitnessHistory().size() <= 1) return;

				final List<Double> avgFitnesses = history.getAvgFitnessHistory();
				final List<Double> maxFitnesses = history.getMaxFitnessHistory();
				final double max = Math.max(0.001, history.getMaxFitness());

				for (int x = 0; x < avgFitnesses.size(); x++) {
					int yAvg = (int) ((1 - avgFitnesses.get(x) / max) * getHeight());
					int yMax = (int) ((1 - maxFitnesses.get(x) / max) * getHeight());
					int x2 = x, yAvg2 = yAvg, yMax2 = yAvg;
					if (x > 0) {
						x2 = x - 1;
						yAvg2 = (int) ((1 - avgFitnesses.get(x2) / max) * getHeight());
						yMax2 = (int) ((1 - maxFitnesses.get(x2) / max) * getHeight());
					}

					// Draw it!
					g.setColor(Color.white);
					g.drawLine(x * getWidth() / (avgFitnesses.size() - 1), yAvg,
							x2 * getWidth() / (avgFitnesses.size() - 1), yAvg2);

					g.setColor(Color.red);
					g.drawLine(x * getWidth() / (maxFitnesses.size() - 1), yMax,
							x2 * getWidth() / (maxFitnesses.size() - 1), yMax2);
				}
			}
		});

		fitGraph.setSize(GraphicsFX.highDPI(300), GraphicsFX.highDPI(300));
		fitGraph.setLocationRelativeTo(null);
		fitGraph.setAlwaysOnTop(true);
		fitGraph.setVisible(true);

		// Create the simulation frame launcher and create an automatic update
		// thread
		simFrameLauncher = new SimulationFrameLauncher();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						// Check if the execution is beeing blocked
						while (gaFrameLauncher.isBlockGA())
							Thread.sleep(200);

						// Render town desktop pane in fixed time steps
						simFrameLauncher.getTownDesktopPane().repaint();
						Thread.sleep(simFrameLauncher.updater.getTickSpeed());

					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();

		// Create a dummy simulation to set the town's tile transform
		Simulation dummyS = new Simulation(new Town(map.length, map[0].length, random));
		Town dummyT = dummyS.getTown();
		dummyT.generateTiles(map);
		simFrameLauncher.setSimulation(dummyS);

		// Move the frames a bit arround
		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int gaFrameHeight = GraphicsFX.highDPI(300);
		simFrameLauncher.getFrame().setLocation(0, 0);
		simFrameLauncher.getFrame().setSize(screen.width, screen.height - gaFrameHeight);
		gaFrameLauncher.getFrame().setLocation(0, screen.height - gaFrameHeight);
		gaFrameLauncher.getFrame().setSize(screen.width, gaFrameHeight - GraphicsFX.highDPI(50));
		fitGraph.setLocation(screen.width - fitGraph.getWidth() - GraphicsFX.highDPI(20), screen.height - fitGraph.getHeight() - gaFrameLauncher.getFrame().getHeight() - GraphicsFX.highDPI(50));

		gaRuntime = 5000; // Terminate after n generations
		gaPopSize = 20; // Individuals per population
		townRuntime = (int) Units.hoursToTicks(24); // Calc fitness after n ticks of simulation
		simulationTickSpeed = -1; // DEBUGGING ONLY! Time for one simulation
									// tick
		// Anzahl an Verkehrsaufkommen, welches vorhanden sein soll
		townNumberPersons = 2000;
		// "Pufferzone" in Prozent, in diesem Bereich sollen zum Ende der
		// Simulation keine Personen mehr erzeugt werden
		townPersonStopPuffer = 0.2f; // Puffer liegt also bei den letzten 20%

		// Init the chromosome length values
		chromoStationLength = Blueprint.townToMappingIP(map).size(); // Calculates
																		// street
																		// count
		chromoScheduleCount = 5; // Maximum number of Schedules in a Town
		chromoScheduleStationLength = 5; // Maximum number of stations per
											// Schedule
		chromoScheduleStartTimeLength = 5 * 2; // Maximum number of start times
												// per Schedule

		chromoScheduleShouldAlternate = 1; // Boolean, if the schedule should
											// alternate
		chromoCount = 1 + chromoScheduleCount * 3; // Number of chromosomes per
													// individual (1 for the
													// station list, 3
													// for each schedule)

		// Max start time and min delay
		final int maxStartTime = 50;

		// Declare the chromosome lengths, min genes and max genes
		final int[] chromosomeLengths = new int[chromoCount];
		final int[] minGenes = new int[chromoCount];
		final int[] maxGenes = new int[chromoCount];

		// Initialize these
		chromosomeLengths[0] = chromoStationLength;
		minGenes[0] = Integer.MIN_VALUE;
		maxGenes[0] = Integer.MAX_VALUE;

		for (int i = 1; i < chromoCount; i += 3) {
			// Schedule station / start times lengths
			chromosomeLengths[i] = chromoScheduleStationLength;
			chromosomeLengths[i + 1] = chromoScheduleStartTimeLength;
			chromosomeLengths[i + 2] = chromoScheduleShouldAlternate;

			// Schedule station range
			minGenes[i] = 0;
			maxGenes[i] = chromoStationLength - 1;

			// Schedule start time range
			minGenes[i + 1] = -maxStartTime;
			maxGenes[i + 1] = maxStartTime;

			// Schedule should alternate range
			minGenes[i + 2] = Integer.MIN_VALUE;
			maxGenes[i + 2] = Integer.MAX_VALUE;
		}

		// Create our genetic algorithm (from 2nd argument on: mutationRate, crossoverRate, crossoverSwapProbability, elitismCount)
		ga = new GenericAlgorithm(this, gaPopSize, 0.1, 0.85, 0.2, 4, random);

		// Initialize population
		currentPopulation = ga.initPopulation(chromosomeLengths, minGenes, maxGenes);
		Individual i = currentPopulation.getIndividuals()[0];

		// Set the gaframelauncher's ga
		gaFrameLauncher.setGenericAlgorithm(ga);

		// Evaluate population for the first time
		isGARunning = true;
		ga.evalPopulation(currentPopulation);

		while (ga.isTerminationConditionMet(currentPopulation) == false) {
			// Check if the execution is beeing blocked
			while (gaFrameLauncher.isBlockGA()) {
				isGARunning = false;
				Thread.sleep(200);
			}
			isGARunning = true;

			// Apply crossover
			currentPopulation = ga.crossoverPopulation(currentPopulation);

			// Apply mutation
			currentPopulation = ga.mutatePopulation(currentPopulation);

			// Evaluate population
			ga.evalPopulation(currentPopulation);
		}
		isGARunning = false;

		GAFrameLauncher.logger.info("Found solution in " + ga.getGeneration() + " generations");
		GAFrameLauncher.logger.info("Best solution: " + currentPopulation.getFittest(0).toString());
	}

	/**
	 * This class handles the town simulation. We will first create a new town
	 * and generate its blueprint from the chromosom. The chromosom is found in
	 * the
	 */
	public double simulate(Individual individual) {
		Simulation simulation;
		Town town;

		try {
			// Create a town simulation
			simulation = new Simulation(new Town(map.length, map[0].length, random));
			town = simulation.getTown();
			town.generateTiles(map); // Landschaftskarte

			Blueprint testing = BlueprintConverter.convert(individual.getChromosomes(), map, random);
			town.setBlueprint(testing);
			testing.generate(simulation.getTown());
			town.applyBlueprint();

		} catch (Exception ex) {
			// Town generation not possible. return fitness of -1.
			Simulation.logger.warning("Town creation failed! returning -1 for fitness");
			ex.printStackTrace();
			return -1;
		}

		// Simulate the town and get its fitness
		currentTown = town;
		simFrameLauncher.setSimulation(simulation);

		try {
			while (town.getTime() < townRuntime) { // Run simulation for some
													// ticks
				// Update town by one tick
				town.update();

				if (simulationTickSpeed > 0) {
					Thread.sleep(simulationTickSpeed);
				}
			}
		} catch (Exception ex) {
			Simulation.logger.severe("Town simulation failed! returning -1 for fitness");
			ex.printStackTrace();
			return -1;
		}

		// Get the fitness
		double fitness = FitnessEvaluator.evaluate(town);
		town = null;

		currentTown = null;
		return fitness;
	}

	public boolean isTerminationConditionMet(Population population) {
		// Terminate, when we have enough generations
		return ga.getGeneration() >= gaRuntime;
	}
	
	public static void saveState() {
		if (isGARunning) {
			JOptionPane.showMessageDialog(null, "GA l�uft noch!", "Save State", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		String filename = JOptionPane.showInputDialog(null, "Titel eingeben", "Save State", JOptionPane.INFORMATION_MESSAGE);
		if (filename == null) return;
		if (filename.isEmpty()) return;
		
		// Gain a file
		File file = new File(FrameLauncher.class.getClassLoader().getResource("res/saves/").getFile() + filename + ".trafficsim");

		// Overwrite?
		if (file.exists()) {
			int ans = JOptionPane.showConfirmDialog(null, "Soll die vorhandene State �berschrieben werden?", "Save State", JOptionPane.YES_NO_OPTION);
			if (ans != JOptionPane.YES_OPTION) return;
		}
		
		// Save that thang!
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			
			out.writeObject(currentPopulation);
			out.writeObject(map);
			
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "State konnte nicht gespeichert werden.", "Save State", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void loadState() {
		if (isGARunning) {
			JOptionPane.showMessageDialog(null, "GA l�uft noch!", "Save State", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		File path = new File(FrameLauncher.class.getClassLoader().getResource("res/saves/").getFile());
		File[] files = path.listFiles();
		
		JList list = new JList(files);
		list.setCellRenderer(new ListCellRenderer<File>() {
			public Component getListCellRendererComponent(JList<? extends File> list, File value, int index,
					boolean isSelected, boolean cellHasFocus) {
				JLabel l = new JLabel(value.getName().replace(".trafficsim", ""));
				l.setOpaque(true);
				if (isSelected) l.setBackground(Color.lightGray);
				return l;
			}
		});
		JOptionPane.showMessageDialog(null, list, "Load State", JOptionPane.PLAIN_MESSAGE);
		
		int selectedIndex = list.getSelectedIndex();
		if (selectedIndex < 0 || selectedIndex >= files.length) return;
		
		File file = files[selectedIndex];
		
		// Load that thang!
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			currentPopulation = (Population) in.readObject();
			map = (float[][][]) in.readObject();
			
			
			in.close();
			
			// Readd GA to gaFrameLauncher
			ga.removeGenericAlgorithmWatcher(gaFrameLauncher);
			gaFrameLauncher.setGenericAlgorithm(ga);
			
			// Update all
			simFrameLauncher.getFrame().repaint();
			gaFrameLauncher.getFrame().repaint();
			
			gaFrameLauncher.unblockGA();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "State konnte nicht geladen werden.", "Load State", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		defaultFrameLauncher = new FrameLauncher();
	}
}
