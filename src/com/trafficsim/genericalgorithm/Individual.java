package com.trafficsim.genericalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Individual {
	public static long CURRENT_ID = 0;

	private long id;
	private long[] parentIDs;

	private List<Chromosome> chromosomes;

	private double fitness;

	public Individual(Random random, int[] chromosomeLengths, int[] minGenes, int[] maxGenes) {
		this(new ArrayList<Chromosome>());
		
		if (chromosomeLengths.length != minGenes.length || minGenes.length != maxGenes.length)
			throw new IllegalArgumentException("chromosome lengths, min gene and max gene must be the same length");
		
		// Add random chromosomes
		for (int i = 0; i < chromosomeLengths.length; i++) {
			Chromosome c = new Chromosome(random, chromosomeLengths[i], minGenes[i], maxGenes[i]);
			addChromosome(c);
		}
	}

	public Individual(List<Chromosome> chromosomes) {
		id = getNextID();
		parentIDs = new long[0];

		this.chromosomes = chromosomes;

		fitness = -1;
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public Chromosome getChromosome(int index) {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");
		if (index < 0)
			throw new ArrayIndexOutOfBoundsException("Chromosome index too small");
		if (index >= chromosomes.size())
			throw new ArrayIndexOutOfBoundsException("Chromosome index too big");

		return chromosomes.get(index);
	}

	public Chromosome setChromosome(int index, Chromosome chromosome) {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");
		if (chromosome == null)
			throw new NullPointerException("Chromosome is null");
		if (index < 0)
			throw new ArrayIndexOutOfBoundsException("Chromosome index too small");
		if (index >= chromosomes.size())
			throw new ArrayIndexOutOfBoundsException("Chromosome index too big");

		return chromosomes.set(index, chromosome);
	}

	public MarkedChromosome getChromosomeAt(int genePos) {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");
		
		if (genePos < 0) return null;

		for (Iterator<Chromosome> it = chromosomes.iterator(); it.hasNext();) {
			Chromosome c = it.next();
			
			if (genePos >= c.getLength()) {
				genePos -= c.getLength();
				continue;
			}
			
			return new MarkedChromosome(c, genePos);
		}
		
		return null;
	}

	public void addChromosome(Chromosome c) {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");

		chromosomes.add(c);
	}

	public void removeChromosome(Chromosome c) {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");

		chromosomes.remove(c);
	}

	public int getChromosomeCount() {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");

		return chromosomes.size();
	}

	public void setGene(int genePos, int gene) {
		MarkedChromosome mc = getChromosomeAt(genePos);
		if (mc == null) throw new IndexOutOfBoundsException("no chromosomes at gene " + genePos);
		
		mc.setMarkedGene(gene);
	}

	public int getGene(int genePos) {
		MarkedChromosome mc = getChromosomeAt(genePos);
		if (mc == null) throw new IndexOutOfBoundsException("no chromosomes at gene " + genePos);
		
		return mc.getMarkedGene();
	}
	
	public int getGeneCount() {
		if (chromosomes == null)
			throw new NullPointerException("Chromosomes list is null");
		
		int length = 0;
		for (Chromosome chromosome : chromosomes) length += chromosome.getLength();
		return length;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

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

	public String toString() {
		if (chromosomes == null) return "null";
		
		String output = "";
		for (Chromosome c : chromosomes) {
			if (c == null) output += "null";
			else output += c;
			
			output += " ";
		}
		return output;
	}

	public long getID() {
		return id;
	}
	
	public Individual getStencil() {
		List<Chromosome> chromosomes = new ArrayList<Chromosome>();
		
		// Copy each chromosome
		for (int i = 0; i < getChromosomeCount(); i++) {
			Chromosome c = getChromosome(i);
			chromosomes.add(new Chromosome(new int[c.getLength()], c.getMinGene(), c.getMaxGene()));
		}
		
		// Return a new individual from these chromosomes
		return new Individual(chromosomes);
	}

	public static long getNextID() {
		return CURRENT_ID++;
	}
}
