package com.trafficsim.genericalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Individual {
	public static long CURRENT_ID = 0;

	private long id;
	private long[] parentIDs;
	
	private List<Chromosome> chromosomes;

	private double fitness;

	public Individual(Random random, int[][] chromosomes, int[] minGenes, int[] maxGenes) {
		this(null);
	}

	public Individual(Chromosome[] chromosomes) {
		id = getNextID();
		parentIDs = new long[0];
		
		this.chromosomes = Arrays.asList(chromosomes);
		
		fitness = -1;
	}

	/**
	 * Gets individual's chromosome
	 * 
	 * @return The individual's chromosome
	 */
	public int[] getChromosome() {
		return this.chromosome;
	}

	/**
	 * USE WITH CARE
	 * 
	 * @param data
	 * @return
	 */
	public void setChromosome(int[] data) {
		chromosome = data;
	}

	/**
	 * Gets individual's chromosome length
	 * 
	 * @return The individual's chromosome length
	 */
	public int getChromosomeLength() {
		return this.chromosome.length;
	}

	/**
	 * Set gene at offset
	 * 
	 * @param gene
	 * @param offset
	 * @return gene
	 */
	public void setGene(int offset, int gene) {
		if (gene < geneMin || gene >= geneMax)
			throw new IllegalArgumentException("Gene must be in bounds (" + geneMin + " to " + (geneMax - 1) + ")");
		this.chromosome[offset] = gene;
	}

	/**
	 * Get gene at offset
	 * 
	 * @param offset
	 * @return gene
	 */
	public int getGene(int offset) {
		return this.chromosome[offset];
	}

	/**
	 * Store individual's fitness
	 * 
	 * @param fitness
	 *            The individuals fitness
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Gets individual's fitness
	 * 
	 * @return The individual's fitness
	 */
	public double getFitness() {
		return this.fitness;
	}

	public void setParentIDs(long[] parentIDs) {
		if (parentIDs == null)
			throw new NullPointerException("Parent ids cannot be null");
		if (parentIDs.length > 2)
			throw new IllegalArgumentException(
					"Individual can't have more than 2 parents (" + parentIDs.length + " given)");

		this.parentIDs = parentIDs;
	}

	public long[] getParentIDs() {
		return parentIDs;
	}

	/**
	 * Display the chromosome as a string.
	 * 
	 * @return string representation of the chromosome
	 */
	public String toString() {
		String output = "";
		for (int gene = 0; gene < this.chromosome.length; gene++) {
			output += this.chromosome[gene];
		}
		return output;
	}

	public int getGeneMin() {
		return geneMin;
	}

	public void setGeneMin(int geneMin) {
		this.geneMin = geneMin;
	}

	public int getGeneMax() {
		return geneMax;
	}

	public void setGeneMax(int geneMax) {
		this.geneMax = geneMax;
	}

	public long getID() {
		return id;
	}

	public static long getNextID() {
		return CURRENT_ID++;
	}
}
