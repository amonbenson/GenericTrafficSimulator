package com.trafficsim.graphics.ga.history;

public class HIndividual {
	
	private long id;
	private long[] parentIDs;
	
	private int[] chromosome;
	private double fitness;
	
	public HIndividual(long id, long[] parentIDs, int[] chromosome) {
		this.id = id;
		this.parentIDs = parentIDs;
		this.chromosome = chromosome;
	}

	public long getID() {
		return id;
	}

	public long[] getParentIDs() {
		return parentIDs;
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
