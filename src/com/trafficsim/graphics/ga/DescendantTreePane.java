package com.trafficsim.graphics.ga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;

public class DescendantTreePane extends JComponent {

	public static final Color COL_BG = new Color(70, 70, 70);
	public static final Color COL_FG = new Color(255, 255, 255);
	public static final Color COL_ACCENT = new Color(239, 108, 38);
	
	public static final int NODE_HEIGHT = GraphicsFX.highDPI(50);
	public static final int NODE_MARGIN_X = GraphicsFX.highDPI(10);
	public static final int NODE_MARGIN_Y = GraphicsFX.highDPI(70);
	public static final int NODE_ARC = GraphicsFX.highDPI(10);
	
	GenerationHistory history;
	
	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		
		// Draw background
		g.setColor(COL_BG);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (history == null) return;
		
		int nx, ny, nw, nh;
		nh = NODE_HEIGHT;
		nw = (getWidth() - NODE_MARGIN_X) / history.getPopulationSize() - NODE_MARGIN_X;
		
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
				g.setColor(COL_ACCENT);
				g.fillRoundRect(nx, ny, nw, nh, NODE_ARC, NODE_ARC);
				
				// Draw text
				g.setColor(COL_FG);
				g.drawString("ID: " + i.getID(), (int) (nx + nw * 0.05), (int) (ny + nh * 0.3));
				g.drawString("" + chromosomeToString(i.getChromosome()), (int) (nx + nw * 0.05), (int) (ny + nh * 0.8));
				
				// Draw parent connections
				if (prevP != null) {
					g.setStroke(new BasicStroke(GraphicsFX.highDPI(2), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
					g.setColor(COL_ACCENT);
					
					long[] parentIDs = i.getParentIDs();
					for (long pid : parentIDs) {
						int parentIndex = prevP.indexOf(pid);
						if (parentIndex == -1) {
							GAFrameLauncher.logger.warning("Parent ID wasn't found in previous generation");
							continue;
						}
						
						g.drawLine(nx + nw / 2, ny + nh, getNodeX(nw, parentIndex) + nw / 2, getNodeY(nh, y - 1));
					}
				}
			}
		}
	}
	
	private int getNodeX(int nodeWidth, int indexX) {
		return NODE_MARGIN_X + (NODE_MARGIN_X + nodeWidth) * indexX;
	}
	
	private int getNodeY(int nodeHeight, int indexY) {
		return (history.getPopulationCount() - indexY - 1) * (nodeHeight + NODE_MARGIN_Y) + (getHeight() - nodeHeight) / 2;
	}

	private String chromosomeToString(int[] chromosome) {
		String s = "";
		for (int i : chromosome) s += i;
		return s;
	}

	public GenerationHistory getHistory() {
		return history;
	}

	public void setHistory(GenerationHistory history) {
		this.history = history;
	}
}
