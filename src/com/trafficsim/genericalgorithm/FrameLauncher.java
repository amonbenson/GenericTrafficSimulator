package com.trafficsim.genericalgorithm;

import java.util.Random;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.SimulationFrameLauncher;
import com.trafficsim.graphics.ga.GAFrameLauncher;
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

	public Random random;
	
	private GenericAlgorithm ga;

	/**
	 * The number of iterations, which the generic algorithm will get through, until
	 * it stops and returns the results.
	 */
	private int gaRuntime;
	
	/**
	 * The number of individuals in each generated population.
	 */
	private int gaPopSize;

	/**
	 * The number of iterations, which a single town will be simulated. One
	 * iteration means one update call, which means one town tick. If the maximum
	 * number is reached, the town will stop simulating, and the fitness will be
	 * calculated.
	 * 
	 */
	private int townRuntime;

	/**
	 * Sets the time to sleep between two town updates. Should only be used for
	 * debbuging only, cause it slows down the generic algorithm.
	 */
	private long simulationTickSpeed;

	public static int chromoStationLength;
	public static int chromoScheduleCount;
	public static int chromoScheduleStationLength;
	public static int chromoScheduleStartTimeLength;
	public static int chromoScheduleMinDelayLength;
	public static int chromoCount;

	/**
	 * Represents the currently simulated town. The simulation will take place in a
	 * different thread, so the main thread can concentrate on drawing the graphics,
	 * where this town class can be used to show the process. However, it may be
	 * null, if nothing is simulated.
	 */
	private Town currentTown;

	/**
	 * The simulation framelauncher will create a jframe to show the current town.
	 */
	public SimulationFrameLauncher simFrameLauncher;

	/**
	 * The generic algorithm frame launcher will take care of showing the current
	 * ga's process and displaying a nice family tree of the individuals.
	 */
	public GAFrameLauncher gaFrameLauncher;
	
	public static float[][][] map = Simulation.testTown();

	public FrameLauncher() throws InterruptedException {

		random = new Random(1);
		
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
		gaFrameLauncher.descendantTreePane.setFrameLauncherContext(this);

		// Create the simulation frame launcher and create an automatic update thread
		simFrameLauncher = new SimulationFrameLauncher();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {

						simFrameLauncher.getTownDesktopPane().repaint();
						Thread.sleep(simFrameLauncher.updater.getTickSpeed());

					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();

		// Move the frames a bit arround
		gaFrameLauncher.getFrame().setLocation(gaFrameLauncher.getFrame().getX() + GraphicsFX.highDPI(300),
				gaFrameLauncher.getFrame().getY() + GraphicsFX.highDPI(100));
		simFrameLauncher.getFrame().setLocation(GraphicsFX.highDPI(10), GraphicsFX.highDPI(10));

		gaRuntime = 150; // Terminate after n generations
		gaPopSize = 10; // Individuals per population
		townRuntime = 100; // Calc fitness after n ticks of simulation
		simulationTickSpeed = -1; // DEBUGGING ONLY! Time for one simulation
									// tick

		// Init the chromosome length values
		chromoStationLength = Blueprint.townToMappingIP(map).size(); // Calculates street count
		chromoScheduleCount = 4; // Maximum number of Schedules in a Town
		chromoScheduleStationLength = 5; // Maximum number of stations per Schedule
		chromoScheduleStartTimeLength = 5 * 2; // Maximum number of start times per Schedule
		chromoScheduleMinDelayLength = 1; // Min delay value (only one value)
		chromoCount = 1 + chromoScheduleCount * 3; // Number of chromosomes per individual (1 for the station list, 2
													// for each schedule)

		// Max start time and min delay
		final int maxStartTime = 50;
		final int maxMinDelay = 50;
		
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
			chromosomeLengths[i + 2] = chromoScheduleMinDelayLength;

			// Schedule station range
			minGenes[i] = 0;
			maxGenes[i] = chromoStationLength - 1;
			
			// Schedule start time range
			minGenes[i + 1] = -maxStartTime;
			maxGenes[i + 1] = maxStartTime;
			
			// Schedule min delay range
			minGenes[i + 2] = -maxMinDelay;
			maxGenes[i + 2] = maxMinDelay;
		}

		// Create our genetic algorithm
		
		ga = new GenericAlgorithm(this, gaPopSize, 0.05, 0.95, 2, random);

		// Initialize population
		Population population = ga.initPopulation(chromosomeLengths, minGenes, maxGenes);
		Individual i = population.getIndividuals()[0];

		// Set the gaframelauncher's ga
		gaFrameLauncher.setGenericAlgorithm(ga);

		// Evaluate population for the first time
		ga.evalPopulation(population);

		while (ga.isTerminationConditionMet(population) == false) {
			// Check if the execution is beeing blocked
			while (gaFrameLauncher.isBlockGA()) Thread.sleep(200);
			
			// Apply crossover
			population = ga.crossoverPopulation(population);

			// Apply mutation
			population = ga.mutatePopulation(population);

			// Evaluate population
			ga.evalPopulation(population);
		}

		GAFrameLauncher.logger.info("Found solution in " + ga.getGeneration() + " generations");
		GAFrameLauncher.logger.info("Best solution: " + population.getFittest(0).toString());
	}

	/**
	 * This class handles the town simulation. We will first create a new town and
	 * generate its blueprint from the chromosom. The chromosom is found in the
	 */
	public double simulate(Individual individual) {
		Simulation simulation;
		Town town;

		try {
			// Create a town simulation
			// TODO Make the real chromosom to town conversion
			simulation = new Simulation(new Town(map.length, map[0].length, random));
			town = simulation.getTown();
			town.generateTiles(map); // Landschaftskarte

			Blueprint testing = BlueprintConverter.convert(individual.getChromosomes(), map, random);
			town.setBlueprint(testing);
			testing.generate(simulation.getTown());
			town.applyBlueprint();

			System.out.println(testing.getNumberBusses());
			System.out.println(testing.getNumberStations());
			
		} catch (Exception ex) {
			// Town generation not possible. return fitness of -1.
			Simulation.logger.severe("Town creation failed! returning -1 for fitness");
			ex.printStackTrace();
			return -1;
		}

		// Simulate the town and get its fitness
		// TODO Make a real fitness function
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

		// Return the inverted average travel time as fitness TODO Don't do
		// that.
		double fitness = 1 / town.getStatistics().getAverageTravelTime(currentTown);

		currentTown = null;
		return fitness;
	}

	public boolean isTerminationConditionMet(Population population) {
		// Terminate, when we have enough generations
		return ga.getGeneration() >= gaRuntime;
	}

	public static void main(String[] args) throws InterruptedException {
		new FrameLauncher();
	}
}
