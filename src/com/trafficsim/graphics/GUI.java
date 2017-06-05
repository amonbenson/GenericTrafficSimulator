package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.trafficsim.sim.Simulation;

public class GUI extends JComponent implements KeyListener {
	
	private Simulation simulation;
	private TownRenderer townRenderer;
	
	private AutoUpdater autoUpdater;
	
	public GUI() {
		
	}
	
	public void initListeners() {
		Component rootFrame = SwingUtilities.getRoot(this);
		if (rootFrame == null) throw new NullPointerException("GUI must be added to a frame");
		
		rootFrame.addKeyListener(this);
	}
	
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		
		autoUpdater = new AutoUpdater(simulation.getTown());
		townRenderer = new TownRenderer(simulation.getTown());
	}
	
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (townRenderer == null) {
			g.setColor(Color.gray);
			g.drawString("No town to display.", 40, 60);
			
			
		} else {
			townRenderer.render(getWidth(), getHeight(), g);
		}
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			// Update the town
			simulation.getTown().update();
			
			// Render
			repaint();
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}
}
