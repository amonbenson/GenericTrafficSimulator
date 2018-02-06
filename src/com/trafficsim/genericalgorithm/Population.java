package com.trafficsim.genericalgorithm;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Population {
	private Individual population[];
	private double populationFitness = -1;

	public Population(int populationSize) {
		// Initial empty population
		this.population = new Individual[populationSize];
	}

	public Population(Random random, int populationSize, int[] chromosomeLengths, int[] minGenes, int[] maxGenes) {
		// Initialize the population as an array of individuals
		this(populationSize);

		// Create each individual in turn
		for (int individualCount = 0; individualCount < populationSize; individualCount++) {
			// Create an individual, initializing its chromosome to the given
			// length
			Individual individual = new Individual(random, chromosomeLengths, minGenes, maxGenes);
			// Add individual to population
			this.population[individualCount] = individual;
		}
	}

	public Individual[] getIndividuals() {
		return this.population;
	}

	public Individual getFittest(int offset) {
		// Order population by fitness
		Arrays.sort(this.population, new Comparator<Individual>() {
			public int compare(Individual o1, Individual o2) {
				if (o1.getFitness() > o2.getFitness()) {
					return -1;
				} else if (o1.getFitness() < o2.getFitness()) {
					return 1;
				}
				return 0;
			}
		});

		// Return the fittest individual
		return this.population[offset];
	}

	public void setPopulationFitness(double fitness) {
		this.populationFitness = fitness;
	}

	public double getPopulationFitness() {
		return this.populationFitness;
	}

	public int size() {
		return this.population.length;
	}

	public Individual setIndividual(int offset, Individual individual) {
		return population[offset] = individual;
	}

	public Individual getIndividual(int offset) {
		return population[offset];
	}

	public int indexOf(Individual individual) {
		for (int i = 0; i < population.length; i++) {
			if (population[i] == individual)
				return i;
		}
		return -1;
	}

	public void shuffle() {
		Random rnd = new Random();
		for (int i = population.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			Individual a = population[index];
			population[index] = population[i];
			population[i] = a;
		}
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < population.length; i++) {
			if (i > 0)
				s += " ";
			s += population[i];
		}
		return s;
	}
}