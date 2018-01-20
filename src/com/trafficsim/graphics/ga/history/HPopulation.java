package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.List;

public class HPopulation {

	private List<HIndividual> individuals;

	private int eliteLimit;
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

	public int getIndividualCount() {
		return individuals.size();
	}

	public HIndividual getIndividual(int index) {
		if (index < 0 || index > individuals.size() - 1)
			throw new IndexOutOfBoundsException("Index must be greater than or equal to 0 and smaller than individual count.");

		return individuals.get(index);
	}
	
	public int indexOf(HIndividual individual) {
		return individuals.indexOf(individual);
	}
	
	public int indexOf(long id) {
		for (int i = 0; i < individuals.size(); i++) {
			if (getIndividual(i).getID() == id) return i;
		}
		return -1;
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
