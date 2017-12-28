package de.amonbenson.generic;

public class Test {
	public Test() {
		TestPopulation p = new TestPopulation(10); // Create a test population with n individuals
		p.initChromosomesRandom(20); // Init all chromosomes randomly with lengths of m
		
		p.deathByNaturalSelection = 0.5;
		p.mutationSwapProp = 0.05;
		p.mutationScrambleProp = 0.01;
		p.mutationInverseProp = 0.01;
		
		p.mutationSwapDistance = 4;
		p.mutationScrambleDistance = 4;
		p.mutationSwapDistance = 4;
		
		while (true) {
			// Create a generation of individuals from the chromosomes' dna
			p.createGeneration();
			
			// Simulate this generation and get the fitnesses
			p.simulateGeneration();
			
			for (int i = 0; i < p.getSize(); i++) {
				Chromosome c = p.getChromosomes().get(i);
				
				System.out.print(c + "      ");
			}
			System.out.println("\n");
			
			// Apply natural selection and create a new generation
			p.breedNewGeneration();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Test();
	}
	
	private class TestPopulation extends Population<Integer> {

		public TestPopulation(int size) {
			super(size);
		}

		@Override
		public Integer createIndividual(String dna) {
			char[] ca = dna.toCharArray();
			
			int f = 0;
			for (int i = 0; i < dna.length(); i++) {
				if (i % 5 == 0 && ca[i] == '0') f += 100;
				if (i % 5 == 1 && ca[i] == '1') f += 100;
			}
			return f;
		}

		@Override
		public double simulateIndividual(Integer individual) {
			double fitness = individual * 10.0;
			return fitness;
		}
		
	}
}
