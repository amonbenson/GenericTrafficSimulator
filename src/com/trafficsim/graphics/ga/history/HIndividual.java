package com.trafficsim.graphics.ga.history;

import java.util.List;

import com.trafficsim.genericalgorithm.Chromosome;

public class HIndividual {
	
	private long id;
	private int[] parentIndicies;
	
	private List<Chromosome> chromosomes;
	private double fitness;
	
	public HIndividual(long id, int[] parentIndicies, List<Chromosome> chromosomes, double fitness) {
		this.id = id;
		this.parentIndicies = parentIndicies;
		this.chromosomes = chromosomes;
		this.fitness = fitness;
	}

	public long getID() {
		return id;
	}

	public int[] getParentIndicies() {
		return parentIndicies;
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
}
