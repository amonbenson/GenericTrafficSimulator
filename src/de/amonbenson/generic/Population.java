package de.amonbenson.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Population<T> {

	public static final int MAX_BREED_ITERATIONS = 1000;

	public double deathByNaturalSelection = 0.5;
	public double mutationSwapProp = 0.05;
	public double mutationScrambleProp = 0.01;
	public double mutationInverseProp = 0.01;

	public int mutationSwapDistance = 5;
	public int mutationScrambleDistance = 5;
	public int mutationInverseDistance = 5;

	private int size;
	private List<Chromosome> chromosomes;
	private List<T> individuals;
	private double fitnessSum;

	private Random random;

	public Population(int size) {
		this(createEmptyChromosomesList(size));
	}

	public Population(List<Chromosome> chromosomes) {
		this(new Random(), chromosomes);
	}

	public Population(Random random, List<Chromosome> chromosomes) {
		if (chromosomes == null)
			throw new IllegalArgumentException("Chromosomes list mustn't be null.");
		if (chromosomes.size() == 0)
			throw new IllegalArgumentException("Chromosomes list mustn't be empty.");

		this.random = random;
		this.chromosomes = chromosomes;
		size = chromosomes.size();
	}

	public abstract T createIndividual(boolean[] dna);
	public abstract double simulateIndividual(T individual);

	public void createGeneration() {
		individuals = new ArrayList<T>();

		for (Chromosome chromosome : chromosomes) {
			if (chromosome.getDNA() == null)
				throw new NullPointerException("Chromosome DNA must not be null.");

			// Create an individual from the dna
			T individual = createIndividual(chromosome.getDNA());

			if (individual == null)
				throw new NullPointerException("Individual must not be null.");

			// Add this individual to the list
			individuals.add(individual);

		}
	}

	public void simulateGeneration() {
		if (chromosomes == null)
			throw new NullPointerException("No chromosomes defined.");
		if (individuals == null)
			throw new NullPointerException("No individuals defined. Use applyDNA first.");
		if (chromosomes.size() != individuals.size() || chromosomes.size() != size)
			throw new IllegalStateException("Chromosomes and individuals must be the same size as population.");

		// Sum all fitnesses together. Will be used to pick parents later.
		double fitnessSum = 0;

		for (int i = 0; i < size; i++) {
			// Get the chromosome and corresponding individual
			Chromosome chromosome = chromosomes.get(i);
			T individual = individuals.get(i);

			if (chromosome == null)
				throw new NullPointerException("Chromosome must not be null.");
			if (individual == null)
				throw new NullPointerException("Individual must not be null.");

			// Simulate the individual and get the fitness
			double fitness = simulateIndividual(individual);
			if (fitness < 0)
				fitness = 0;
			chromosome.setFitness(fitness);

			fitnessSum += fitness;
		}

		this.fitnessSum = fitnessSum;
	}

	public void breedNewGeneration() {
		// Sort the chromosomes by their fitness
		chromosomes.sort(new ChromosomeComparator());

		// Remove the unfittest chromosomes (natural selection)
		chromosomes = chromosomes.subList((int) (chromosomes.size() * deathByNaturalSelection), chromosomes.size());

		// Now do some crossover and mutation
		while (chromosomes.size() < size) {
			Chromosome mom = selectParent();
			Chromosome dad = selectParent();

			// If either of the parents is null or they are the same, we
			// continue
			if (mom == null || dad == null || mom == dad)
				continue;

			// Get the dna from mom and dad. If they ar none or not the same
			// size, continue
			boolean[] dnaMom = mom.getDNA(), dnaDad = dad.getDNA();
			if (dnaMom == null || dnaDad == null)
				continue;
			if (dnaMom.length != dnaDad.length)
				continue;

			// Make the child dna
			boolean[] dnaChild = new boolean[dnaMom.length];

			// Now, lets cross over
			int crs1 = random.nextInt(dnaChild.length);
			int crs2 = random.nextInt(dnaChild.length);

			for (int i = 0; i < dnaChild.length; i++) {
				if (i < Math.min(crs1, crs2) || i >= Math.max(crs1,  crs2))
					dnaChild[i] = dnaMom[i];
				else
					dnaChild[i] = dnaDad[i];
			}

			// Now, lets do some swapping, scrambling and inverting
			if (random.nextDouble() < mutationSwapProp) {
				int swp1 = random.nextInt(dnaChild.length - mutationSwapDistance);
				int swp2 = swp1 + mutationSwapDistance;

				;
			}

			if (random.nextDouble() < mutationScrambleProp) {
				int scb1 = random.nextInt(dnaLength - mutationScrambleDistance);
				int scb2 = scb1 + mutationScrambleDistance;

				char[] scramble = dnaChild.substring(scb1, scb2).toCharArray();
				shuffleCharArray(scramble);

				dnaChild = dnaChild.substring(0, scb1) + String.valueOf(scramble) + dnaChild.substring(scb2);
			}

			if (random.nextDouble() < mutationInverseProp) {
				int inv1 = random.nextInt(dnaLength - mutationInverseDistance);
				int inv2 = inv1 + mutationInverseDistance;
			}

			// Finally, some real mutation (changing 0s to 1s and vice versa).
			for (int i = 0; i < dnaChild.length(); i++) {
				if (random.nextDouble() < 1.0 / dnaChild.length()) {
					char replaceChar = '0';
					if (dnaChild.charAt(i) == '0') replaceChar = '1';
					
					dnaChild = dnaChild.substring(0, i) + replaceChar + dnaChild.substring(i + 1);
				}
			}

			// Add the child chromosom
			Chromosome child = new Chromosome();
			child.setDNA(dnaChild);
			chromosomes.add(child);
		}
	}

	private Chromosome selectParent() {
		// If the fitnessSum is 0, we just choose a true random chromosome
		if (fitnessSum == 0) {
			return chromosomes.get(random.nextInt(chromosomes.size()));
		}

		// Choose a weightet random chromosome
		double p = 0;
		double r = random.nextDouble() * fitnessSum;

		for (Chromosome c : chromosomes) {
			p += c.getFitness();
			if (p > r)
				return c;
		}

		return null;
	}

	private void shuffleCharArray(char[] ar) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			char a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	public void initChromosomesRandom(int dnaSize) {
		if (chromosomes == null)
			return;
		for (int i = 0; i < chromosomes.size(); i++)
			chromosomes.set(i, new Chromosome(random, dnaSize));
	}

	private static List<Chromosome> createEmptyChromosomesList(int size) {
		List<Chromosome> chromosomes = new ArrayList<Chromosome>();
		for (int i = 0; i < size; i++)
			chromosomes.add(new Chromosome(""));
		return chromosomes;
	}

	public int getSize() {
		return size;
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public List<T> getIndividuals() {
		return individuals;
	}

	public double getFitnessSum() {
		return fitnessSum;
	}
}
