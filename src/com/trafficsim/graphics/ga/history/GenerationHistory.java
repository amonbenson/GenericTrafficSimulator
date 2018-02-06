package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.List;

public class GenerationHistory {

	private List<HPopulation> populations;
	private int populationSize;

	private int maxPopulationCount;

	public GenerationHistory(int populationSize, int maxPopulationCount) {
		this.populationSize = populationSize;
		populations = new ArrayList<HPopulation>();

		this.maxPopulationCount = maxPopulationCount;
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

	public HPopulation getNthPopulation(int n) {
		return populations.get(n);
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
