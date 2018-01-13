package de.amonbenson.generic;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.graphics.SimulationFrameLauncher;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class FrameLauncher implements Simulator {
	
	private GeneticAlgorithm ga;
	
	private int gaRuntime, townRuntime;
	
	private Town currentTown; // Currently simulated town, may be null
	private SimulationFrameLauncher framelauncher;
	
	public FrameLauncher() {
		framelauncher = new SimulationFrameLauncher();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						
						framelauncher.getTownDesktopPane().repaint();
						Thread.sleep(1000/35);
						
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
		
		gaRuntime = 1000; // Terminate after 1000 generations
		townRuntime = 2000; // Calc fitness after 5000 ticks
		
		// Create our genetic algorithm
		ga = new GeneticAlgorithm(this, 10, 0.05, 0.95, 2);

		// Initialize population
		Population population = ga.initPopulation(5);

		// Evaluate population for the first time
		ga.evalPopulation(population);

		while (ga.isTerminationConditionMet(population) == false) {
			System.out.println("gen " + ga.getGeneration() + "    fit " + population.getPopulationFitness());
			
			// Apply crossover
			population = ga.crossoverPopulation(population);

			// Apply mutation
			population = ga.mutatePopulation(population);

			// Evaluate population
			ga.evalPopulation(population);
		}
		
		System.out.println("Found solution in " + ga.getGeneration() + " generations");
		System.out.println("Best solution: " + population.getFittest(0).toString());
	}

	public double simulate(Individual individual) {
		Simulation simulation;
		Town town;
		
		try {
			// Create a town simulation
			// TODO Make the real chromosom to town conversion
			simulation = new Simulation( new Town(10, 10));
			town = simulation.getTown();
			town.generateTiles(Simulation.testTown()); //Landschaftskarte
			
			Blueprint testing = Blueprint.randomBlueprint(Simulation.testTown());
			town.setBlueprint(testing);
	
			testing.generate(simulation.getTown());
			town.applyBlueprint();
			
		} catch (IllegalArgumentException ex) {
			// Town generation not possible. return fitness of -1.
			return -1;
		}
		
		// Simulate the town and get its fitness
		// TODO Make a real fitness function
		currentTown = town;
		framelauncher.setSimulation(simulation);
		
		while (town.getTime() < townRuntime) { // Run simulation for some ticks
			// Update town by one tick
			town.update();
		}
		
		currentTown = null;
		
		// Return the inverted average travel time as fitness TODO Don't do that.
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
