package com.trafficsim.genericalgorithm;

public class MarkedChromosome {
	
	private Chromosome chromosome;
	private int genePos;
	
	public MarkedChromosome(Chromosome chromosome, int genePos) {
		if (chromosome == null) throw new NullPointerException("chromosome is null");
		if (!chromosome.containsGenePos(genePos)) throw new IllegalArgumentException("Chromosome does not contain gene pos " + genePos);
		
		this.chromosome = chromosome;
		this.genePos = genePos;
	}

	public Chromosome getChromosome() {
		return chromosome;
	}

	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}

	public int getGenePos() {
		return genePos;
	}

	public void getGenePos(int genePos) {
		this.genePos = genePos;
	}
	
	public int getMarkedGene() {
		return chromosome.getGene(getGenePos());
	}
	
	public void setMarkedGene(int gene) {
		chromosome.setGene(getGenePos(), gene);
	}
}
