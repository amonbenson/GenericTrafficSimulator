package com.trafficsim.graphics.ga;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.trafficsim.genericalgorithm.GenericAlgorithm;
import com.trafficsim.genericalgorithm.GenericAlgorithmWatcher;
import com.trafficsim.genericalgorithm.Individual;
import com.trafficsim.genericalgorithm.Population;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;

public class GAFrameLauncher implements GenericAlgorithmWatcher {

	public static final int HISTORY_LENGTH = 10;
	
	public static final Logger logger = Logger.getLogger(GAFrameLauncher.class.getName());

	private GenericAlgorithm ga;
	private GenerationHistory history;

	private JFrame frame;
	private JScrollPane dtPaneScroller;
	private DescendantTreePane descendantTreePane;

	private JToolBar toolBar;
	private JToggleButton pauseButton;
	
	private volatile boolean blockGA; // If this is true, the execution of the generic
								// algorithm will be blocked.

	public GAFrameLauncher() {
		frame = new JFrame("Generic Traffic Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Toolbar
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		pauseButton = new JToggleButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blockGA = pauseButton.isSelected();
			}
		});
		toolBar.add(pauseButton);
		
		frame.add(BorderLayout.NORTH, toolBar);
		
		// Descendant Pane
		descendantTreePane = new DescendantTreePane();
		dtPaneScroller = new JScrollPane(descendantTreePane);
		dtPaneScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		dtPaneScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(BorderLayout.CENTER, dtPaneScroller);

		frame.setAlwaysOnTop(true);
		frame.setSize(GraphicsFX.highDPI(400), GraphicsFX.highDPI(300));
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
		history = new GenerationHistory(ga.getPopulationSize(), HISTORY_LENGTH);
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

	public void mutation(Population population, int individualIndex, int chomosomeIndex, int geneIndex) {
		GAFrameLauncher.logger.fine("GAWatcher: applying mutation");
	}

	public void mutationDone(Population before, Population after) {
		GAFrameLauncher.logger.fine("GAWatcher: done applying mutation");
	}

	public void populationEvaluated(int generation, Population population) {
		GAFrameLauncher.logger.fine("GAWatcher: done evaluating population");
		
		// Enter the next generation
		history.nextGeneration();
		HPopulation pop = history.getCurrentPopulation();
		HPopulation prevPop = history.getPreviousPopulation();
		
		int elitecount = 0;
		// TODO: WARNING! POPULATION MUST BE EVALUATED BEFORE(!) ELITE COUNT IS CALCULATED!!! pls change that in the future.
		for (Individual individual : population.getIndividuals()) {
			// Get the parent ids from our previous generation and convert them to indices
			int[] parentIndices = null;
			if (prevPop != null) parentIndices = prevPop.idsToIndices(individual.getParentIDs());
			
			// Create a new hindividual (graphical representation of a ga individual)
			HIndividual i = new HIndividual(individual.getID(), parentIndices, individual.getChromosomes());
			pop.addIndividual(i);
		}
		
		// Update the generation, population fitness and elite count
		pop.setGeneration(generation);
		pop.setFitness(population.getPopulationFitness());
		pop.setEliteLimit(ga.getElitismCount());
		
		// Update the descendant tree
		descendantTreePane.repaint();
		dtPaneScroller.revalidate();
	}

	public JFrame getFrame() {
		return frame;
	}

	public GenerationHistory getGenerationHistory() {
		return history;
	}

	public boolean isBlockGA() {
		return blockGA;
	}
}