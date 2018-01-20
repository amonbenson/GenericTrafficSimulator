package com.trafficsim.graphics.ga;

import java.util.logging.Logger;

import javax.swing.JFrame;

import com.trafficsim.genericalgorithm.GenericAlgorithm;
import com.trafficsim.genericalgorithm.GenericAlgorithmWatcher;
import com.trafficsim.genericalgorithm.Individual;
import com.trafficsim.genericalgorithm.Population;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;

public class GAFrameLauncher implements GenericAlgorithmWatcher {

	public static final Logger logger = Logger.getLogger(GAFrameLauncher.class.getName());

	private GenericAlgorithm ga;
	private GenerationHistory history;

	private JFrame frame;
	private DescendantTreePane descendantTreePane;

	private boolean blockGA; // If this is true, the execution of the generic
								// algorithm will be blocked.

	public GAFrameLauncher() {
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		descendantTreePane = new DescendantTreePane();
		frame.add(descendantTreePane);

		frame.setAlwaysOnTop(true);
		frame.setSize(GraphicsFX.highDPI(800), GraphicsFX.highDPI(600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setGenericAlgorithm(GenericAlgorithm ga) {
		if (ga == null) {
			GAFrameLauncher.logger.warning("Setting generic algorithm to null.");
			return;
		}

		this.ga = ga;
		ga.addGenericAlgorithmWatcher(this);
		history = new GenerationHistory(ga.getPopulationSize());
		descendantTreePane.setHistory(history);
	}

	public void blockGA() {
		blockGA = true;
	}

	public void crossover(Population before, Population after, Individual parent1, Individual parent2,
			Individual offspring) {
		GAFrameLauncher.logger.fine("GAWatcher: applying crossover");
	}

	public void crossoverDone(Population before, Population after) {
		GAFrameLauncher.logger.fine("GAWatcher: done applying crossover");
	}

	public void mutation(Population population, int individualIndex, int geneIndex) {
		GAFrameLauncher.logger.fine("GAWatcher: applying mutation");
	}

	public void mutationDone(Population before, Population after) {
		GAFrameLauncher.logger.fine("GAWatcher: done applying mutation");
	}

	public void populationEvaluated(int generation, Population population) {
		GAFrameLauncher.logger.fine("GAWatcher: done evaluating population");
		
		// Enter the next generation
		history.nextGeneration();
		HPopulation g = history.getCurrentPopulation();
		
		int elitecount = 0;
		// TODO: WARNING! POPULATION MUST BE EVALUATED BEFORE (!) ELITE COUNT IS CALCULATED!!! pls change that in the future.
		for (Individual individual : population.getIndividuals()) {
			HIndividual i = new HIndividual(individual.getID(), individual.getParentIDs(), individual.getChromosome());
			g.addIndividual(i);
		}
		
		// Update the descendant tree
		descendantTreePane.repaint();
	}

	public JFrame getFrame() {
		return frame;
	}

	public GenerationHistory getGenerationHistory() {
		return history;
	}
}