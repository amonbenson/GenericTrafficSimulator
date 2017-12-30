package de.amonbenson.generic;

import java.util.Random;

public class Chromosome {

	private static long currentID;
	
	private long id;
	
	private boolean[] dna;
	private double fitness;

	public Chromosome(Random random, int dnaSize) {
		this();
		
		// Initialize the dna as a random string
		boolean[] randomDNA = new boolean[dnaSize];
		for (int i = 0; i < dnaSize; i++) {
			randomDNA[i] = random.nextBoolean();
		}
		setDNA(randomDNA);
	}

	public Chromosome() {
		this(new boolean[0]);
	}

	public Chromosome(boolean[] dna) {
		this.id = nextID();
		
		this.dna = dna;
		this.fitness = 0.0;
	}

	public boolean[] getDNA() {
		return dna;
	}

	public void setDNA(boolean[] dna) {
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
