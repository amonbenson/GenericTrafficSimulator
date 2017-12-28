package de.amonbenson.generic;

import java.util.Comparator;

public class ChromosomeComparator implements Comparator<Chromosome> {

	public int compare(Chromosome chr1, Chromosome chr2) {
		return Double.compare(chr1.getFitness(), chr2.getFitness());
	}

}
