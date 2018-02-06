package com.trafficsim.graphics.ga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.trafficsim.generic.Blueprint;
import com.trafficsim.genericalgorithm.BlueprintConverter;
import com.trafficsim.genericalgorithm.FrameLauncher;
import com.trafficsim.graphics.GraphicsFX;
import com.trafficsim.graphics.ga.history.GenerationHistory;
import com.trafficsim.graphics.ga.history.HIndividual;
import com.trafficsim.graphics.ga.history.HPopulation;
import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class DescendantTreePane extends JComponent implements MouseListener, MouseMotionListener {

	public static final Color COL_BG = new Color(70, 70, 70);
	public static final Color COL_FG = new Color(255, 255, 255);
	public static final Color COL_ACCENT = new Color(239, 108, 38);
	public static final Color COL_ACCENT_2 = new Color(239, 51, 37);

	public static final int PANEL_MARGIN = GraphicsFX.highDPI(7);

	public static final int NODE_HEIGHT = GraphicsFX.highDPI(25);
	public static final int NODE_WIDTH = GraphicsFX.highDPI(50);
	public static final int NODE_MARGIN_X = GraphicsFX.highDPI(10);
	public static final int NODE_MARGIN_Y = GraphicsFX.highDPI(70);
	public static final int NODE_ARC = GraphicsFX.highDPI(10);

	private FrameLauncher frameLauncherContext;
	
	private GenerationHistory history;
	private JScrollPane scroller;

	public DescendantTreePane() {
		history = null;
		scroller = null;

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

		if (scroller == null) {
			if (getParent().getParent() instanceof JScrollPane) {
				scroller = (JScrollPane) getParent().getParent();
			}
		}

		int nx, ny;

		for (int y = 0; y < history.getPopulationCount(); y++) {
			ny = getNodeY(y);

			// Continue if offscreen
			if (ny + NODE_HEIGHT + NODE_MARGIN_Y < 0)
				continue;
			if (ny > getHeight())
				continue;

			HPopulation p = history.getNthPopulation(y);

			HPopulation prevP = null;
			if (y > 0)
				prevP = history.getNthPopulation(y - 1);

			int iCount = p.getIndividualCount();

			for (int x = 0; x < iCount; x++) {
				nx = getNodeX(x);

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
							g.drawLine(nx + NODE_WIDTH / 2, ny + NODE_HEIGHT, getNodeX(parentIndex) + NODE_WIDTH / 2,
									getNodeY(y - 1));
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
				String txt = "" + i.getID();
				g.setColor(COL_FG);
				g.drawString(txt, (int) (nx + (NODE_WIDTH - g.getFontMetrics().stringWidth(txt)) / 2),
						(int) (ny + (NODE_HEIGHT + g.getFontMetrics().getHeight() * 0.6) / 2));
			}

			// Draw left GEN panel
			nx = PANEL_MARGIN;
			if (scroller != null) {
				nx -= scroller.getX() + getX();
			}
			
			g.setColor(GraphicsFX.changeAlpha(COL_BG, 200));
			g.fillRect(nx - PANEL_MARGIN, ny - NODE_MARGIN_Y, NODE_WIDTH + NODE_MARGIN_X, NODE_HEIGHT + NODE_MARGIN_Y);

			// Draw generation text
			g.setColor(COL_FG);
			String txt = "" + p.getGeneration();
			g.drawString(txt, (int) (nx + (NODE_WIDTH - g.getFontMetrics().stringWidth(txt)) / 2),
					(int) (ny + (NODE_HEIGHT + g.getFontMetrics().getHeight() * 0.6) / 2));
		}

		// Update preferred size
		setPreferredSize(new Dimension(
				(history.getPopulationSize() + 1) * (NODE_WIDTH + NODE_MARGIN_X) - NODE_MARGIN_X + PANEL_MARGIN * 2,
				history.getPopulationCount() * (NODE_HEIGHT + NODE_MARGIN_Y) - NODE_MARGIN_Y + PANEL_MARGIN * 2));
	}

	private int getNodeX(int indexX) {
		return (indexX + 1) * (NODE_WIDTH + NODE_MARGIN_X) + PANEL_MARGIN;
	}

	private int getNodeY(int indexY) {
		return (history.getPopulationCount() - indexY - 1) * (NODE_HEIGHT + NODE_MARGIN_Y) + PANEL_MARGIN;
	}

	public GenerationHistory getHistory() {
		return history;
	}

	public void setHistory(GenerationHistory history) {
		this.history = history;
	}

	public void setFrameLauncherContext(FrameLauncher frameLauncherContext) {
		this.frameLauncherContext = frameLauncherContext;
	}

	public void mouseDragged(MouseEvent e) {
		if (scroller == null) return;
		
		// TODO: Scroll
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
		// Return if we have no frame launcher
		if (frameLauncherContext == null) return;
		
		// Get our node position
		int nodeX = (e.getX() - PANEL_MARGIN) / (NODE_WIDTH + NODE_MARGIN_X) - 1;
		int nodeY = (e.getY() - PANEL_MARGIN) / (NODE_HEIGHT + NODE_MARGIN_Y);
		
		if (nodeX < 0) return;
		if (nodeX > history.getPopulationSize() - 1) return;
		if (nodeY < 0) return;
		if (nodeY > history.getPopulationCount() - 1) return;
		
		// Get the corresponding h-individual
		HPopulation p = history.getNthPopulation(history.getMaxPopulationCount() - nodeY - 1);
		HIndividual i = p.getIndividual(nodeX);
		
		// Pause ga
		frameLauncherContext.gaFrameLauncher.pauseButton.setSelected(true);
		frameLauncherContext.gaFrameLauncher.blockGA();
		
		// Load the town
		Simulation simulation = new Simulation(new Town(frameLauncherContext.map.length, frameLauncherContext.map[0].length, frameLauncherContext.random));
		Town town = simulation.getTown();
		town.generateTiles(frameLauncherContext.map);
		
		Blueprint testing = BlueprintConverter.convert(i.getChromosomes(), frameLauncherContext.map, frameLauncherContext.random);
		town.setBlueprint(testing);

		testing.generate(simulation.getTown());
		town.applyBlueprint();
		
		frameLauncherContext.simFrameLauncher.setSimulation(simulation);
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
