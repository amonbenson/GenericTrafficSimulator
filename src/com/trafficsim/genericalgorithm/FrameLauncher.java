package com.trafficsim.genericalgorithm;

import java.util.Random;
import java.util.logging.Level;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.generic.BlueprintSchedule;
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

	private GenericAlgorithm ga;

	/**
	 * The number of iterations, which the generic algorithm will get through,
	 * until it stops and returns the results.
	 */
	private int gaRuntime;

	/**
	 * The number of iterations, which a single town will be simulated. One
	 * iteration means one update call, which means one town tick. If the
	 * maximum number is reached, the town will stop simulating, and the fitness
	 * will be calculated.
	 * 
	 */
	private int townRuntime;

	/**
	 * Sets the time to sleep between two town updates. Should only be used for
	 * debbuging only, cause it slows down the generic algorithm.
	 */
	private long simulationTickSpeed;

	/**
	 * Represents the currently simulated town. The simulation will take place
	 * in a different thread, so the main thread can concentrate on drawing the
	 * graphics, where this town class can be used to show the process. However,
	 * it may be null, if nothing is simulated.
	 */
	private Town currentTown;

	/**
	 * The simulation framelauncher will create a jframe to show the current
	 * town.
	 */
	private SimulationFrameLauncher simFrameLauncher;

	/**
	 * The generic algorithm frame launcher will take care of showing the
	 * current ga's process and displaying a nice family tree of the
	 * individuals.
	 */
	private GAFrameLauncher gaFrameLauncher;

	public FrameLauncher() {

		Simulation.logger.setLevel(Level.OFF);
		GAFrameLauncher.logger.setLevel(Level.ALL);

		// Create the generic algorithm frame launcher
		gaFrameLauncher = new GAFrameLauncher();
		
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
		gaFrameLauncher.getFrame().setLocation(gaFrameLauncher.getFrame().getX() + GraphicsFX.highDPI(500), gaFrameLauncher.getFrame().getY() + GraphicsFX.highDPI(100));
		simFrameLauncher.getFrame().setLocation(simFrameLauncher.getFrame().getX(), GraphicsFX.highDPI(10));

		gaRuntime = 5; // Terminate after 1000 generations
		townRuntime = 500; // Calc fitness after townRuntime ticks of simulation
		simulationTickSpeed = 1; // DEBUGGING ONLY! Time bfor one simulation
									// tick

		// Create our genetic algorithm
		ga = new GenericAlgorithm(this, 5, 0.05, 0.95, 2);

		// Initialize population
		Population population = ga.initPopulation(5);
		
		// Set the gaframelauncher's ga
		gaFrameLauncher.setGenericAlgorithm(ga);

		// Evaluate population for the first time
		ga.evalPopulation(population);

		while (ga.isTerminationConditionMet(population) == false) {
			GAFrameLauncher.logger.info("GEN " + ga.getGeneration() + "    FIT " + population.getPopulationFitness());

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
	 * This class handles the town simulation. We will first create a new town
	 * and generate its blueprint from the cromosom. The chromosom is found in
	 * the
	 */
	public double simulate(Individual individual) {
		Simulation simulation;
		Town town;

		Random r = new Random();
		
		try {
			// Create a town simulation
			// TODO Make the real chromosom to town conversion
			simulation = new Simulation(new Town(10, 10, r));
			town = simulation.getTown();
			town.generateTiles(Simulation.testTown()); // Landschaftskarte

			Blueprint testing = Blueprint.randomChromosom(Simulation.testTown(), r);
			town.setBlueprint(testing);
			
			testing.generate(simulation.getTown());
			System.err.println(testing);
			town.applyBlueprint();
			
			
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

				Thread.sleep(simulationTickSpeed);
			}
		} catch (Exception ex) {
			Simulation.logger.severe("Town simulation failed! returning -1 for fitness");
			ex.printStackTrace();
			return -1;
		}

		currentTown = null;

		// Return the inverted average travel time as fitness TODO Don't do
		// that.
		double fitness = 1000.0 / town.getStatistics().getAverageTravelTime();
		return fitness;
	}

	public boolean isTerminationConditionMet(Population population) {
		// Terminate, when we have enough generations
		return ga.getGeneration() >= gaRuntime;
	}

	public static void main(String[] args) {
		new FrameLauncher();
	}
}
