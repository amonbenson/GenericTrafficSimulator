package com.trafficsim.graphics.ga.history;

public class HIndividual {
	
	private long id;
	private int[] parentIndicies;
	
	private int[] chromosome;
	private double fitness;
	
	public HIndividual(long id, int[] parentIndicies, int[] chromosome) {
		this.id = id;
		this.parentIndicies = parentIndicies;
		this.chromosome = chromosome;
	}

	public long getID() {
		return id;
	}

	public int[] getParentIndicies() {
		return parentIndicies;
	}

	public int[] getChromosome() {
		return chromosome;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
