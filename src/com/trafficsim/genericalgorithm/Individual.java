package com.trafficsim.genericalgorithm;

public class Individual {
	public static long CURRENT_ID = 0;

	private long id;
	private int[] chromosome;
	private double fitness = -1;

	private long[] parentIDs;

	/**
	 * Initializes random individual.
	 * 
	 * This constructor assumes that the chromosome is made entirely of 0s and
	 * 1s, which may not always be the case, so make sure to modify as
	 * necessary. This constructor also assumes that a "random" chromosome means
	 * simply picking random zeroes and ones, which also may not be the case
	 * (for instance, in a traveling salesman problem, this would be an invalid
	 * solution).
	 * 
	 * @param chromosomeLength
	 *            The length of the individuals chromosome
	 */
	public Individual(int chromosomeLength) {
		this(null); // We can do this, because we'll overwrite the chromosome in
					// the next step. Apart from that, this is no good practice.

		this.chromosome = new int[chromosomeLength];
		for (int gene = 0; gene < chromosomeLength; gene++) {
			if (0.5 < Math.random()) {
				this.setGene(gene, 1);
			} else {
				this.setGene(gene, 0);
			}
		}

	}

	/**
	 * Initializes individual with specific chromosome
	 * 
	 * @param chromosome
	 *            The chromosome to give individual
	 */
	public Individual(int[] chromosome) {
		id = getNextID();
		// Create individual chromosome
		this.chromosome = chromosome;

		parentIDs = new long[0];
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
		if (parentIDs == null) throw new NullPointerException("Parent ids cannot be null");
		if (parentIDs.length > 2) throw new IllegalArgumentException("Individual can't have more than 2 parents (" + parentIDs.length + " given)");
		
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

	public long getID() {
		return id;
	}

	public static long getNextID() {
		return CURRENT_ID++;
	}
}
