package com.trafficsim.genericalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericAlgorithm {

	private Simulator simulator;
	private List<GenericAlgorithmWatcher> watchers;

	private int populationSize;

	private double mutationRate;
	private double crossoverRate;
	private double crossoverSwapProbability;
	private int elitismCount;

	private int generation;

	private Random random;

	public GenericAlgorithm(Simulator simulator, int populationSize, double mutationRate, double crossoverRate, double crossoverSwapProbability,
			int elitismCount) {
		this(simulator, populationSize, mutationRate, crossoverRate, crossoverSwapProbability, elitismCount, new Random());
	}

	public GenericAlgorithm(Simulator simulator, int populationSize, double mutationRate, double crossoverRate, double crossoverSwapProbability,
			int elitismCount, long seed) {
		this(simulator, populationSize, mutationRate, crossoverRate, crossoverSwapProbability, elitismCount, new Random(seed));
	}

	public GenericAlgorithm(Simulator simulator, int populationSize, double mutationRate, double crossoverRate, double crossoverSwapProbability,
			int elitismCount, Random random) {
		this.simulator = simulator;
		this.populationSize = populationSize;
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.crossoverSwapProbability = crossoverSwapProbability;
		this.elitismCount = elitismCount;
		this.random = random;

		watchers = new ArrayList<GenericAlgorithmWatcher>();
		generation = 0;
	}

	/**
	 * Initialize population
	 * 
	 * @param chromosomeLength
	 *            The length of the individuals chromosome
	 * @param geneMin
	 *            Minimal value for a gene (inclusive)
	 * @param geneMax
	 *            Maximal value for a gene (exclusive)
	 * 
	 * @return population The initial population generated
	 */
	public Population initPopulation(int[] chromosomeLengths, int minGenes[], int maxGenes[]) {
		// Initialize population
		Population population = new Population(random, this.populationSize, chromosomeLengths, minGenes, maxGenes);
		return population;
	}

	/**
	 * Simulate an individual and return its fitness
	 * 
	 * The individual class will contain the chromosom, from which the simulation
	 * can be generated. The simulation has to be ran on this thread and return the
	 * fitness value, whenever it has finished simulating.
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
	 * population's fitness may or may not be important, but what is important here
	 * is making sure that each individual gets evaluated.
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
		double rouletteWheelPosition = random.nextFloat() * populationFitness;

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

	public Population crossoverPopulation(Population population) {
		// Create new population
		Population newPopulation = new Population(population.size());

		// Loop over current population by fitness
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual parent1 = population.getFittest(populationIndex);

			// Apply crossover to this individual?
			if (this.crossoverRate > random.nextFloat() && populationIndex >= this.elitismCount) {
				// Initialize offspring
				Individual offspring = parent1.getStencil();

				// Find second parent
				Individual parent2 = selectParent(population);


				// Loop over individual's chromosomes
				for (int chromosomeIndex = 0; chromosomeIndex < offspring.getChromosomeCount(); chromosomeIndex++) {
					Chromosome chromosomePa1 = parent1.getChromosome(chromosomeIndex);
					Chromosome chromosomePa2 = parent2.getChromosome(chromosomeIndex);
					Chromosome chromosomeOff = offspring.getChromosome(chromosomeIndex);

					// Loop over the chromosome's genes
					boolean useParent1 = 0.5 > random.nextFloat();
					for (int geneIndex = 0; geneIndex < chromosomeOff.getLength(); geneIndex++) {
						// Use half of parent1's genes and half of parent2's genes
						if (useParent1) {
							chromosomeOff.setGene(geneIndex, chromosomePa1.getGene(geneIndex));
						} else {
							chromosomeOff.setGene(geneIndex, chromosomePa2.getGene(geneIndex));
						}
						
						if (random.nextDouble() > crossoverSwapProbability) useParent1 = !useParent1;
					}
				}

				// Call the watcher
				for (GenericAlgorithmWatcher watcher : watchers)
					watcher.crossover(population, newPopulation, parent1, parent2, offspring);

				// Set the parent ids
				offspring.setParentIDs(new long[] { parent1.getID(), parent2.getID() });

				// Add offspring to new population
				newPopulation.setIndividual(populationIndex, offspring);
			} else {
				// Set the parent to itself
				parent1.setParentIDs(new long[] { parent1.getID() });

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
	 * individual in the population, and if they're lucky enough (or unlucky, as it
	 * were), apply some randomness to their chromosome. Like crossover, the type of
	 * mutation applied depends on the specific problem we're solving. In this case,
	 * we simply randomly flip 0s to 1s and vice versa.
	 * 
	 * This method will consider the GeneticAlgorithm instance's mutationRate and
	 * elitismCount
	 * 
	 * @param population
	 *            The population to apply mutation to
	 * @return The mutated population
	 */
	public Population mutatePopulation(Population population) {
		// Initialize new population
		Population newPopulation = new Population(this.populationSize);

		// Loop over current population by fitness (but start after elitism count to
		// prevent the elite from mutating)
		for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
			Individual individual = population.getFittest(populationIndex);

			// Only apply mutation if the index is over the elite (but still add the individual after the if clause)
			if (populationIndex > elitismCount) {
			
				// Loop over individual's chromosomes
				for (int chromosomeIndex = 0; chromosomeIndex < individual.getChromosomeCount(); chromosomeIndex++) {
					Chromosome chromosome = individual.getChromosome(chromosomeIndex);
	
					// Loop over the chromosome's genes
					for (int geneIndex = 0; geneIndex < chromosome.getLength(); geneIndex++) {
	
						// Does this gene need mutation?
						if (this.mutationRate > random.nextFloat()) {
							// Get new gene
							int newGene = Chromosome.generateRandomGene(random, chromosome.getMinGene(),
									chromosome.getMaxGene());
	
							// Mutate gene
							chromosome.setGene(geneIndex, newGene);
	
							// Call the watcher
							for (GenericAlgorithmWatcher watcher : watchers)
								watcher.mutation(population, populationIndex, chromosomeIndex, geneIndex);
						}
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

	public int getPopulationSize() {
		return populationSize;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public int getElitismCount() {
		return elitismCount;
	}
}
