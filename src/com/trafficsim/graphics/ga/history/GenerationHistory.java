package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.List;

public class GenerationHistory {
	
	private List<HPopulation> populations;
	private int populationSize;
	
	public GenerationHistory(int populationSize) {
		this.populationSize = populationSize;
		populations = new ArrayList<HPopulation>();
	}
	
	public HPopulation getCurrentPopulation() {
		if (populations.isEmpty()) return null;
		return populations.get(populations.size() - 1);
	}
	
	public void nextGeneration() {
		populations.add(new HPopulation(populationSize));
	}
	
	public HPopulation getNthPopulation(int n) {
		return populations.get(n);
	}
	
	public int getPopulationCount() {
		return populations.size();
	}

	public int getPopulationSize() {
		return populationSize;
	}
}
