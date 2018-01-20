package com.trafficsim.graphics.ga.history;

public class HIndividual {
	
	private long id;
	private int[] chromosome;
	private double fitness;
	
	public HIndividual(long id, int[] chromosome) {
		this.id = id;
		this.chromosome = chromosome;
	}

	public long getID() {
		return id;
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
