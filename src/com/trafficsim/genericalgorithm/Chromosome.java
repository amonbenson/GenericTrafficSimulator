package com.trafficsim.genericalgorithm;

import java.util.Random;
import java.util.logging.Logger;

import com.trafficsim.sim.Simulation;

public class Chromosome {

	private int[] genes;
	private int minGene, maxGene;

	public Chromosome(int[] genes, int minGene, int maxGene) {
		if (genes == null)
			throw new NullPointerException("Genes cannot be null!");
		if (minGene > maxGene)
			throw new IllegalArgumentException("Min gene must be smaller than max gene");

		if (minGene == maxGene)
			Simulation.logger.warning("min gene is the same number as max gene (" + minGene + ")");

		this.genes = genes;
		this.minGene = minGene;
		this.maxGene = maxGene;
	}

	public Chromosome(Random random, int length, int minGene, int maxGene) {
		this(new int[length], minGene, maxGene);
		
		// Set the genes to a random value (add +1 to the random, because the maxGene is inclusive)
		for (int i = 0; i < length; i++) {
			setGene(i, generateRandomGene(random, minGene, maxGene));
		}
	}

	public int getGene(int genePos) {
		if (genePos < 0)
			throw new ArrayIndexOutOfBoundsException("gene pos is too small (" + genePos + ")");
		if (genePos >= getLength())
			throw new ArrayIndexOutOfBoundsException("gene pos is too big (" + genePos + ")");

		return genes[genePos];
	}

	public void setGene(int genePos, int gene) {
		if (genePos < 0)
			throw new ArrayIndexOutOfBoundsException("gene pos is too small (" + genePos + ")");
		if (genePos >= getLength())
			throw new ArrayIndexOutOfBoundsException("gene pos is too big (" + genePos + ")");

		if (gene > maxGene)
			throw new IllegalArgumentException("gene is bigger than max gene (" + gene + " > " + maxGene + ")");
		if (gene < minGene)
			throw new IllegalArgumentException("gene is smaller than max gene (" + gene + " < " + minGene + ")");

		genes[genePos] = gene;
	}
	
	public int[] getGenes() {
		return genes;
	}

	public int getMinGene() {
		return minGene;
	}

	public void setMinGene(int minGene) {
		if (minGene > maxGene)
			throw new IllegalArgumentException("Min gene must be smaller than max gene");
		if (minGene == maxGene)
			Simulation.logger.warning("min gene is the same number as max gene (" + minGene + ")");
		
		this.minGene = minGene;
	}

	public int getMaxGene() {
		return maxGene;
	}

	public void setMaxGene(int maxGene) {
		if (minGene > maxGene)
			throw new IllegalArgumentException("Min gene must be smaller than max gene");
		if (minGene == maxGene)
			Simulation.logger.warning("min gene is the same number as max gene (" + minGene + ")");
		
		this.maxGene = maxGene;
	}

	public boolean containsGenePos(int genePos) {
		return genePos >= 0 && genePos < getLength();
	}

	public int getLength() {
		return genes.length;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (int gene : getGenes()) {
			output += gene + ",";
		}
		output = output.substring(0, output.length() - 1);
		return output;
	}
	
	public static int generateRandomGene(Random random, int min, int max) {
		// If we have the full integer range, the calculated bounds will be 0. Use the other function
		int bound = max - min + 1;
		if (bound == 0) return random.nextInt();
		
		return min + random.nextInt(bound);
	}
}
