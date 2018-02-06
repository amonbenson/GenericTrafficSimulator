package com.trafficsim.genericalgorithm;

public interface GenericAlgorithmWatcher {
	public void crossover(Population before, Population after, Individual parent1, Individual parent2, Individual offspring);
	public void crossoverDone(Population before, Population after);
	
	public void mutation(Population population, int individualIndex, int chromosomeIndex, int geneIndex);
	public void mutationDone(Population before, Population after);
	
	public void populationEvaluated(int generation, Population population);
}
