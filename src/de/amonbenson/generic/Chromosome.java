package de.amonbenson.generic;

import java.util.Random;

public class Chromosome {

	private static long currentID;
	
	private long id;
	
	private String dna;
	private double fitness;

	public Chromosome(Random random, int dnaSize) {
		this();
		
		// Initialize the dna as a random string
		String randomDNA = "";
		for (int i = 0; i < dnaSize; i++) {
			randomDNA += random.nextBoolean() ? "1" : "0";
		}
		setDNA(randomDNA);
	}

	public Chromosome() {
		this("");
	}

	public Chromosome(String dna) {
		this.id = nextID();
		
		this.dna = dna;
		this.fitness = 0.0;
	}

	public String getDNA() {
		return dna;
	}

	public void setDNA(String dna) {
		this.dna = dna;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public long getID() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + dna + " fit: " + fitness;
	}
	
	private static long nextID() {
		return currentID++ - 1;
	}
}
