package com.trafficsim.graphics.ga.history;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HPopulation {

	private List<HIndividual> individuals;

	private int eliteLimit;
	private double fitness;
	private int populationSize;
	private int generation;

	public HPopulation(int populationSize) {
		this.eliteLimit = 0;
		this.fitness = 0;
		this.populationSize = populationSize;
		this.individuals = new ArrayList<HIndividual>();
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

	public void sortIndividuals() {
		individuals.sort(new Comparator<HIndividual>() {
			public int compare(HIndividual i1, HIndividual i2) {
				// We want biggest first, so negate the whole thing
				return -Double.compare(i1.getFitness(), i2.getFitness());
			}
		});
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
	
	public int[] idsToIndices(long[] ids) {
		if (ids == null) return new int[0]; // If no ids specified, return an empty array
		
		int[] indices = new int[ids.length];
		for (int i = 0; i < ids.length; i++) {
			indices[i] = indexOf(ids[i]); // Will be -1 if id wasn't found
		}
		
		return indices;
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

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}
}
