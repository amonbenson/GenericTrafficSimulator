package com.trafficsim.genericalgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * The GeneticAlgorithm class is our main abstraction for managing the
 * operations of the genetic algorithm. This class is meant to be
 * problem-specific, meaning that (for instance) the "calcFitness" method may
 * need to change from problem to problem.
 * 
 * This class concerns itself mostly with population-level operations, but also
 * problem-specific operations such as calculating fitness, testing for
 * termination criteria, and managing mutation and crossover operations (which
 * generally need to be problem-specific as well).
 * 
 * Generally, GeneticAlgorithm might be better suited as an abstract class or an
 * interface, rather than a concrete class as below. A GeneticAlgorithm
 * interface would require implementation of methods such as
 * "isTerminationConditionMet", "calcFitness", "mutatePopulation", etc, and a
 * concrete class would be defined to solve a particular problem domain. For
 * instance, the concrete class "TravelingSalesmanGeneticAlgorithm" would
 * implement the "GeneticAlgorithm" interface. This is not the approach we've
 * chosen, however, so that we can keep each chapter's examples as simple and
 * concrete as possible.
 * 
 * @author bkanber
 *
 */
public class GenericAlgorithm {

	private Simulator simulator;
	private List<GenericAlgorithmWatcher> watchers;

	private int populationSize;

	private double mutationRate;
	private double crossoverRate;
	private int elitismCount;

	private int generation;

	public GenericAlgorithm(Simulator simulator, int populationSize, double mutationRate, double crossoverRate,
			int elitismCount) {
		this.simulator = simulator;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.elitismCount = elitismCount;

		watchers = new ArrayList<GenericAlgorithmWatcher>();
		generation = 0;
	}

	/**
	 * Initialize population
	 * 
	 * @param chromosomeLength
	 *            The length of the individuals chromosome
	 * @return population The initial population generated
	 */
	public Population initPopulation(int chromosomeLength) {
		// Initialize population
		Population population = new Population(this.populationSize, chromosomeLength);
		return population;
	}

	/**
	 * Simulate an individual and return its fitness
	 * 
	 * The individual class will contain the chromosom, from which the
	 * simulation can be generated. The simulation has to be ran on this thread
	 * and return the fitness value, whenever it has finished simulating.
	 * 
	 * @param individual
	 *            the individual to evaluate
	 * @return double The fitness value for this individual
	 */
	public double calcFitness(Individual individual) {
		return simulator.simulate(individual);
	}

	/**
	 * Evaluate the whole population
	 * 
	 * Essentially, loop over the individuals in the population, calculate the
	 * fitness for each, and then calculate the entire population's fitness. The
	 * population's fitness may or may not be important, but what is important
	 * here is making sure that each individual gets evaluated.
	 * 
	 * @param population
	 *            the population to evaluate
	 */
	public void evalPopulation(Population population) {
		double populationFitness = 0;

		// Loop over population evaluating individuals and summing the
		// population fitness
		for (Individual individual : population.getIndividuals()) {
			double fitness = calcFitness(individual);
			individual.setFitness(fitness);
			populationFitness += fitness;
		}

		population.setPopulationFitness(populationFitness);
		generation++;

		for (GenericAlgorithmWatcher watcher : watchers)
			watcher.populationEvaluated(generation, population);
	}

	/**
	 * Check if population has met termination condition
	 * 
	 * We will just pass the values to our simulator, which will handle the
	 * evaluation based on the optimization problem.
	 * 
	 * @param population
	 *            The population which should be evaluated
	 * @return boolean True if termination condition met, otherwise, false
	 */
	public boolean isTerminationConditionMet(Population population) {
		return simulator.isTerminationConditionMet(population);
	}

	/**
	 * Select parent for crossover
	 * 
	 * @param population
	 *            The population to select parent from
	 * @return The individual selected as a parent
	 */
	public Individual selectParent(Population population) {
		// Get individuals
		Individual individuals[] = population.getIndividuals();

		// Spin roulette wheel
		double populationFitness = population.getPopulationFitness();
		double rouletteWheelPosition = Math.random() * populationFitness;

		// Find parent
		double spinWheel = 0;
		for (Individual individual : individuals) {
			spinWheel += individual.getFitness();
			if (spinWheel >= rouletteWheelPosition) {
				return individual;
			}
		}
		return individuals[population.size() - 1];
	}

	/**
	 * Apply crossover to population
	 * 
	 * Crossover, more colloquially considered "mating", takes the population
	 * and blends individuals to create new offspring. It is hoped that when two
	 * individuals crossover that their offspring will have the strongest
	 * qualities of each of the parents. Of course, it's possible that an
	 * offspring will end up with the weakest qualities of each parent.
	 * 
	 * This method considers both the GeneticAlgorithm instance's crossoverRate
	 * and the elitismCount.
	 * 
	 * The type of crossover we perform depends on the problem domain. We don't
	 * want to create invalid solutions with crossover, so this method will need
	 * to be changed for different types of problems.
	 * 
	 * This particular crossover method selects random genes from each parent.
	 * 
	 * @param population
	 *            The population to apply crossover to
	 * @return The new population
	 */
	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent1 = population.getFittest(populationIndex);

			// Apply crossover to this individual?
			if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {
				// Initialize offspring
				Individual offspring = new Individual(parent1.getChromosomeLength());

				// Find second parent
				Individual parent2 = selectParent(population);

				// Loop over genome
				for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength(); geneIndex++) {
					// Use half of parent1's genes and half of parent2's genes
					if (0.5 > Math.random()) {
						offspring.setGene(geneIndex, parent1.getGene(geneIndex));
					} else {
						offspring.setGene(geneIndex, parent2.getGene(geneIndex));
					}
				}

				// Call the watcher
				for (GenericAlgorithmWatcher watcher : watchers)
					watcher.crossover(population, newPopulation, parent1, parent2, offspring);

				// Add offspring to new population
				newPopulation.setIndividual(populationIndex, offspring);
			} else {
				// Add individual to new population without applying crossover
				newPopulation.setIndividual(populationIndex, parent1);
			}
		}
		
		// Call the watcher and return
		for (GenericAlgorithmWatcher watcher : watchers)
			watcher.crossoverDone(population, newPopulation);
		return newPopulation;
	}

	/**
	 * Apply mutation to population
	 * 
	 * Mutation affects individuals rather than the population. We look at each
	 * individual in the population, and if they're lucky enough (or unlucky, as
	 * it were), apply some randomness to their chromosome. Like crossover, the
	 * type of mutation applied depends on the specific problem we're solving.
	 * In this case, we simply randomly flip 0s to 1s and vice versa.
	 * 
	 * This method will consider the GeneticAlgorithm instance's mutationRate
	 * and elitismCount
	 * 
	 * @param population
	 *            The population to apply mutation to
	 * @return The mutated population
	 */
	public Population mutatePopulation(Population population) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Loop over individual's genes
			for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
				// Skip mutation if this is an elite individual
				if (populationIndex > this.elitismCount) {
					// Does this gene need mutation?
					if (this.mutationRate > Math.random()) {
						// Get new gene
						int newGene = 1;
						if (individual.getGene(geneIndex) == 1) {
							newGene = 0;
						}
						
						// Mutate gene
						individual.setGene(geneIndex, newGene);
						
						// Call the watcher
						for (GenericAlgorithmWatcher watcher : watchers)
							watcher.mutation(population, populationIndex, geneIndex);
					}
				}
			}

			// Add individual to population
			newPopulation.setIndividual(populationIndex, individual);
		}

		// Call the watcher and return mutated population
		for (GenericAlgorithmWatcher watcher : watchers)
			watcher.mutationDone(population, newPopulation);
		return newPopulation;
	}

	public void addGenericAlgorithmWatcher(GenericAlgorithmWatcher watcher) {
		if (watcher == null)
			throw new NullPointerException("Watcher cannot be null.");
		watchers.add(watcher);
	}

	public void removeGenericAlgorithmWatcher(GenericAlgorithmWatcher watcher) {
		if (watcher == null)
			throw new NullPointerException("Watcher cannot be null.");
		watchers.remove(watcher);
	}

	public int getGeneration() {
		return generation;
	}
}
