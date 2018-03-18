package com.trafficsim.graphics.consolepane;

import java.awt.Graphics;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.trafficsim.genericalgorithm.Chromosome;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.DescendantTreePane;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;

public class GAInfoConsolePane extends ConsolePane {

	private DescendantTreePane treePane;

	public GAInfoConsolePane(DescendantTreePane treePane) {
		this.treePane = treePane;
	}

	@Override
	public void paintComponent(Graphics g) {
		clear();
		
		// Don't draw console if tree is null
		if (treePane == null) {
			append("No generic algorithm.");
			super.paintComponent(g);
			return;
		}
		
		if (treePane.getHistory() == null || treePane.getHistory().getCurrentPopulation() == null) {
			append("Waiting for the first generation.");
			super.paintComponent(g);
			return;
		}
		
		// Draw general info
		HPopulation currentPop = treePane.getHistory().getCurrentPopulation();
		append("%A0A0A0Current Population");
		append("Generation:\t" + currentPop.getGeneration());
		append("Fitness:\t" + currentPop.getFitness());
		append("");
		
		// Draw individual info
		append("%A0A0A0Individual Info");
		HIndividual selI = treePane.getSelectedIndividual();
		if (selI == null) {
			append("-");
			super.paintComponent(g);
			return;
		}

		append("ID:\t" + selI.getID());
		append("Chromosome:\t...");
		append("Fitness:\t" + selI.getFitness());

		// Repaint the super class
		super.paintComponent(g);
	}

	@Override
	public void lineClicked(int line, String content) {
		if (line == 6) {
			HIndividual selI = treePane.getSelectedIndividual();
			if (selI == null) return;
			
			String s = "";
			for (Chromosome c : selI.getChromosomes()) s += c + "\n";
			
			JDialog dialog = new JDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			JTextArea area = new JTextArea(s);
			area.setEditable(false);
			area.setFont(GraphicsFX.FONT_DEFAULT);
			dialog.add(new JScrollPane(area));
			
			dialog.setSize(GraphicsFX.highDPI(800), GraphicsFX.highDPI(300));
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
	}
}
