package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.List;

public class HPopulation {

	private List<HIndividual> individuals;

	private int eliteLimit = 2;
	private int populationSize;

	public HPopulation(int populationSize) {
		this.populationSize = populationSize;
		individuals = new ArrayList<HIndividual>();
	}

	public void addIndividual(HIndividual individual) {
		if (individuals.size() >= populationSize)
			throw new IllegalStateException("Maximum number of individuals (" + populationSize + ") already reached.");
		
		individuals.add(individual);
	}

	public boolean isElite(int index) {
		return index < eliteLimit;
	}

	public int getEliteLimit() {
		return eliteLimit;
	}

	public void setEliteLimit(int eliteLimit) {
		this.eliteLimit = eliteLimit;
	}
}
