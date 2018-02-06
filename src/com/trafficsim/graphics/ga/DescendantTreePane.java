package com.trafficsim.graphics.ga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JComponent;

import com.trafficsim.genericalgorithm.Chromosome;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;

public class DescendantTreePane extends JComponent implements MouseListener, MouseMotionListener {

	public static final Color COL_BG = new Color(70, 70, 70);
	public static final Color COL_FG = new Color(255, 255, 255);
	public static final Color COL_ACCENT = new Color(239, 108, 38);
	public static final Color COL_ACCENT_2 = new Color(239, 51, 37);

	public static final int NODE_HEIGHT = GraphicsFX.highDPI(25);
	public static final int NODE_WIDTH = GraphicsFX.highDPI(50);
	public static final int NODE_MARGIN_X = GraphicsFX.highDPI(10);
	public static final int NODE_MARGIN_Y = GraphicsFX.highDPI(70);
	public static final int NODE_ARC = GraphicsFX.highDPI(10);

	GenerationHistory history;

	public DescendantTreePane() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setFont(GraphicsFX.FONT_DEFAULT);

		// Draw background
		g.setColor(COL_BG);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (history == null)
			return;

		int nx, ny;

		for (int y = 0; y < history.getPopulationCount(); y++) {
			ny = getNodeY(y);
			
			// Continue if offscreen
			if (ny + NODE_HEIGHT + NODE_MARGIN_Y < 0) continue;
			if (ny > getHeight()) continue;
			
			HPopulation p = history.getNthPopulation(y);

			HPopulation prevP = null;
			if (y > 0)
				prevP = history.getNthPopulation(y - 1);

			int iCount = p.getIndividualCount();

			for (int x = -1; x < iCount; x++) {
				nx = getNodeX(x);

				String txt = "";
				if (x >= 0) {
					HIndividual i = p.getIndividual(x);
					
					// Draw parent connections
					if (prevP != null) {
						g.setStroke(
								new BasicStroke(GraphicsFX.highDPI(2), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
						if (p.isElite(x))
							g.setColor(COL_ACCENT_2);
						else
							g.setColor(COL_ACCENT);

						int[] parentIndices = i.getParentIndicies();
						if (parentIndices == null) {
							GAFrameLauncher.logger.warning("parent indices are null, should be at least an empty array.");
						} else {
							for (int parentIndex : parentIndices) {
								if (parentIndex == -1) {
									GAFrameLauncher.logger.warning("Parent ID wasn't found in previous generation");
									continue;
								}

								// Draw a line to connect our parent to this current node
								g.drawLine(nx + NODE_WIDTH / 2, ny + NODE_HEIGHT, getNodeX(parentIndex) + NODE_WIDTH / 2, getNodeY(y - 1));
							}
						}
					}
					
					// Draw node background
					if (p.isElite(x))
						g.setColor(COL_ACCENT_2);
					else
						g.setColor(COL_ACCENT);
					g.fillRoundRect(nx, ny, NODE_WIDTH, NODE_HEIGHT, NODE_ARC, NODE_ARC);
	
					// Draw id text
					txt = "" + i.getID();
				} else {
					
					// Draw generation text
					txt = "" + p.getGeneration();
				}
				g.setColor(COL_FG);
				g.drawString(txt, (int) (nx + (NODE_WIDTH - g.getFontMetrics().stringWidth(txt)) / 2),
						(int) (ny + (NODE_HEIGHT + g.getFontMetrics().getHeight() * 0.6) / 2));
			}
		}
	}

	private int getNodeX(int indexX) {
		return (indexX + 1) * (NODE_WIDTH + NODE_MARGIN_X);
	}

	private int getNodeY(int indexY) {
		return (history.getPopulationCount() - indexY - 1) * (NODE_HEIGHT + NODE_MARGIN_Y);
	}

	private String chromosomesToString(List<Chromosome> chromosomes) {
		String s = "";
		for (int j = 0; j < chromosomes.size(); j++) {
			Chromosome chromosome = chromosomes.get(j);
			if (j > 0)
				s += "_";

			for (int i = 0; i < chromosome.getLength(); i++) {
				if (i > 0)
					s += ",";
				String chromoStr = String.valueOf(chromosome.getGene(i));
				s += chromoStr.substring(0, Math.min(3, chromoStr.length()));
			}
		}
		return s;
	}

	public GenerationHistory getHistory() {
		return history;
	}

	public void setHistory(GenerationHistory history) {
		this.history = history;
	}

	public void mouseDragged(MouseEvent e) {
		
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}
}
