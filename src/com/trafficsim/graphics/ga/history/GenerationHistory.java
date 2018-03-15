package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.List;

public class GenerationHistory {

	private List<HPopulation> populations;
	private int populationSize;

	private int maxPopulationCount;

	private List<Double> avgFitnessHistory;
	private List<Double> maxFitnessHistory;
	private double maxFitness;

	public GenerationHistory(int populationSize, int maxPopulationCount) {
		this.populationSize = populationSize;
		populations = new ArrayList<HPopulation>();

		this.maxPopulationCount = maxPopulationCount;

		avgFitnessHistory = new ArrayList<Double>();
		maxFitnessHistory = new ArrayList<Double>();
		maxFitness = 0;
	}

	public HPopulation getCurrentPopulation() {
		if (populations.isEmpty())
			return null;
		return populations.get(populations.size() - 1);
	}

	public HPopulation getPreviousPopulation() {
		if (populations.size() <= 1)
			return null;
		return populations.get(populations.size() - 2);
	}

	public void nextGeneration() {
		populations.add(new HPopulation(populationSize));
		if (populations.size() > maxPopulationCount) populations.remove(0);
	}
	
	public void addPopulationFitnessHistory(double avgFitness, double maxFitness) {
		avgFitnessHistory.add(avgFitness);
		maxFitnessHistory.add(maxFitness);
		
		// Update maximum fitness value
		if (avgFitness > this.maxFitness) this.maxFitness = avgFitness;
		if (maxFitness > this.maxFitness) this.maxFitness = maxFitness;
	}

	public HPopulation getNthPopulation(int n) {
		return populations.get(n);
	}

	public List<Double> getAvgFitnessHistory() {
		return avgFitnessHistory;
	}

	public List<Double> getMaxFitnessHistory() {
		return maxFitnessHistory;
	}

	public double getMaxFitness() {
		return maxFitness;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getPopulationCount() {
		return populations.size();
	}

	public int getMaxPopulationCount() {
		return maxPopulationCount;
	}
}
