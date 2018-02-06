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

	public static final int NODE_HEIGHT = GraphicsFX.highDPI(50);
	public static final int NODE_WIDTH_MIN = GraphicsFX.highDPI(40);
	public static final int NODE_MARGIN_X = GraphicsFX.highDPI(10);
	public static final int NODE_MARGIN_Y = GraphicsFX.highDPI(70);
	public static final int NODE_ARC = GraphicsFX.highDPI(10);
	
	GenerationHistory history;
	
	private int scrolly, scrollsy;
	
	public DescendantTreePane() {
		scrolly = 0;
		scrollsy = 0;

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
		
		if (history == null) return;
		
		int nx, ny, nw, nh;
		nh = NODE_HEIGHT;
		nw = Math.max((getWidth() - NODE_MARGIN_X) / history.getPopulationSize() - NODE_MARGIN_X, NODE_WIDTH_MIN);
		
		for (int y = 0; y < history.getPopulationCount(); y++) {
			HPopulation p = history.getNthPopulation(y);
			
			HPopulation prevP = null;
			if (y > 0) prevP = history.getNthPopulation(y - 1);
			
			int iCount = p.getIndividualCount();
			
			ny = getNodeY(nh, y);
			
			for (int x = 0; x < iCount; x++) {
				HIndividual i = p.getIndividual(x);
				
				nx = getNodeX(nw, x);
				
				// Draw node
				if (p.isElite(x)) g.setColor(COL_ACCENT_2);
				else g.setColor(COL_ACCENT);
				g.fillRoundRect(nx, ny, nw, nh, NODE_ARC, NODE_ARC);
				
				// Draw text
				g.setColor(COL_FG);
				g.drawString("ID: " + i.getID(), (int) (nx + nw * 0.05), (int) (ny + nh * 0.3));
				g.drawString("" + chromosomesToString(i.getChromosomes()), (int) (nx + nw * 0.05), (int) (ny + nh * 0.8));
				
				// Draw parent connections
				if (prevP != null) {
					g.setStroke(new BasicStroke(GraphicsFX.highDPI(2), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
					if (p.isElite(x)) g.setColor(COL_ACCENT_2);
					else g.setColor(COL_ACCENT);
					
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
							g.drawLine(nx + nw / 2, ny + nh, getNodeX(nw, parentIndex) + nw / 2, getNodeY(nh, y - 1));
						}
					}
				}
			}
		}
		
		// Draw a top bar
		g.setColor(GraphicsFX.changeAlpha(COL_BG, 200));
		g.fillRect(0, 0, getWidth(), GraphicsFX.highDPI(50));
		
		// Draw the current generation
		g.setFont(GraphicsFX.FONT_BIG);
		g.setColor(COL_FG);
		
		String text = "Generation " + history.getPopulationCount() + "    Fitness " + GraphicsFX.round(history.getCurrentPopulation().getFitness(), 3);
		g.drawString(text, (getWidth() - g.getFontMetrics().stringWidth(text)) / 2, GraphicsFX.highDPI(35));
	}
	
	private int getNodeX(int nodeWidth, int indexX) {
		return NODE_MARGIN_X + (NODE_MARGIN_X + nodeWidth) * indexX;
	}
	
	private int getNodeY(int nodeHeight, int indexY) {
		return (history.getPopulationCount() - indexY - 1) * (nodeHeight + NODE_MARGIN_Y) + (getHeight() - nodeHeight) / 2 + scrolly;
	}

	private String chromosomesToString(List<Chromosome> chromosomes) {
		String s = "";
		for (int j = 0; j < chromosomes.size(); j++) {
			Chromosome chromosome = chromosomes.get(j);
			if (j > 0) s += "_";
			
			for (int i = 0; i < chromosome.getLength(); i++) {
				if (i > 0) s += ",";
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
		// Update scroll
		scrolly += e.getY() - scrollsy;
		scrollsy = e.getY();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		// Update scroll
		scrollsy = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
}
