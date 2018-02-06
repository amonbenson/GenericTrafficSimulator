package com.trafficsim.genericalgorithm;

/**
 * This is our main class used to run the genetic algorithm.
 * 
 * This case is one of the simplest problems we can solve: the objective is to
 * end up with an individual whose chromosome is all ones.
 * 
 * The simplicity of this problem makes the GeneticAlgorithm class'
 * "calcFitness" method very simple. We'll just count the number of ones in the
 * chromosome and use that as the fitness score. Similarly, the
 * "isTerminationConditionMet" method in the GeneticAlgorithm class for this
 * example is very simple: if the fitness score (ie, number of ones) is the same
 * as the length of the chromosome (ie, we're all ones), we're done!
 * 
 * @author bkanber
 *
 */
public class Test {

	public Test() {
		// Create GA object
		GenericAlgorithm ga = new GenericAlgorithm(new BasicSimulator(), 10, 0.05, 0.95, 2);
		ga.addGenericAlgorithmWatcher(new BasicGenericAlgorithmWatcher());

		// Initialize population
		int[] chromosomeLengths = new int[] {2, 3, 6};
		int[] minGenes = new int[] {-5, -10, 0};
		int[] maxGenes = new int[] {8, 100, 1};
		Population population = ga.initPopulation(chromosomeLengths, minGenes, maxGenes);

		// Evaluate population for the first time
		ga.evalPopulation(population);

		while (ga.isTerminationConditionMet(population) == false) {
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
	
	class BasicGenericAlgorithmWatcher implements GenericAlgorithmWatcher {

		public void crossover(Population before, Population after, Individual parent1, Individual parent2,
				Individual offspring) {
			System.out.println("We will now perform a crossover:");
			System.out.println("   " + parent1);
			System.out.println(" + " + parent2);
			System.out.println("-> " + offspring);
			System.out.println();
		}

		public void crossoverDone(Population before, Population after) {
			System.out.println("The crossover is done. These are the new individuals");
			System.out.println("old   " + before);
			System.out.println("new   " + after);
			System.out.println();
		}

		public void mutation(Population population, int individualIndex, int chromosomeIndex, int geneIndex) {
			System.out.println("We will now perform a mutation on individual no. " + individualIndex);
			System.out.println("We will change gene no. " + chromosomeIndex + "." + geneIndex + ":");
			System.out.println(population.getIndividual(individualIndex));
			System.out.println();
		}

		public void mutationDone(Population before, Population after) {
			System.out.println("The mutation is done. These are the new individuals");
			System.out.println("old   " + before);
			System.out.println("new   " + after);
			System.out.println();
		}

		public void populationEvaluated(int generation, Population population) {
			System.out.println("We are now in generation " + generation + ". The new population is:");
			System.out.println(population);
			System.out.println();
		}
		
	}

	class BasicSimulator implements Simulator {
		public double simulate(Individual individual) {
			double sum = 0;
			for (int i = 0; i < individual.getGeneCount(); i++) {
				if (individual.getGene(i) == 1) sum++;
			}
			
			return sum / individual.getGeneCount();
		}

		public boolean isTerminationConditionMet(Population population) {
			// Check if any individual reached a fitness of 1. This will
			// indicate, that the algorithm has found the bes solution (all 1s)
			// for this specific individual.
			for (Individual individual : population.getIndividuals()) {
				if (individual.getFitness() == 1) {
					return true;
				}
			}
			
			return false;
		}
	}

	public static void main(String[] args) {
		new Test();
	}
}