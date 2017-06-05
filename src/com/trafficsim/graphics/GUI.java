package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Bus;

public class GUI extends JComponent implements KeyListener, MouseListener, MouseMotionListener {
	
	private Simulation simulation;
	private TownRenderer townRenderer;
	
	private AutoUpdater autoUpdater;
	
	private ArrayList<UIWindow> windows;
	
	public GUI() {
		windows = new ArrayList<UIWindow>();
	}
	
	public void initListeners() {
		Component rootFrame = SwingUtilities.getRoot(this);
		if (rootFrame == null) throw new NullPointerException("GUI must be added to a frame");
		
		rootFrame.addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void setSimulation(Simulation simulation) {
		if (simulation == null) return; // Simulation may also be null
		
		this.simulation = simulation;
		
		autoUpdater = new AutoUpdater(this, simulation.getTown());
		townRenderer = new TownRenderer(this, simulation.getTown());
	}
	
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// Render the town
		if (townRenderer == null) {
			g.setColor(Color.gray);
			g.drawString("No town to display.", 40, 60);
			
			
		} else {
			townRenderer.repaint(getWidth(), getHeight(), g);
		}
		
		// Draw all UI Windows
		Iterator<UIWindow> it = windows.iterator();
		while (it.hasNext()) {
			UIWindow window = it.next();
			window.repaint(getWidth(), getHeight(), g);
		}
	}
	
	public void addUIWindow(UIWindow window) {
		windows.add(window);
	}
	
	public void removeUIWindow(UIWindow window) {
		windows.remove(window);
	}
	
	public int getUIWindowCount() {
		return windows.size();
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		// Move one tick
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			try {
				// Update the town
				simulation.getTown().update();
				
				// Render
				repaint();
			} catch (Exception ex) {
				Simulation.logger.log(Level.SEVERE, "Error while auto updating simulation!", ex);
			}
		}
		
		// Toggle auto update
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (autoUpdater.isRunning()) autoUpdater.stop();
			else autoUpdater.start();
		}
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void mouseClicked(MouseEvent e) {
		// ui windows
		ListIterator<UIWindow> it = windows.listIterator(windows.size());
		while (it.hasPrevious()) {
			if (it.previous().addMouseInputEvent(e, MouseEventType.CLICKED)) return;
		}
		
		townRenderer.addMouseInputEvent(e, MouseEventType.CLICKED);
	}

	public void mousePressed(MouseEvent e) {
		// ui windows
		ListIterator<UIWindow> it = windows.listIterator(windows.size());
		while (it.hasPrevious()) {
			if (it.previous().addMouseInputEvent(e, MouseEventType.PRESSED)) return;
		}

		townRenderer.addMouseInputEvent(e, MouseEventType.PRESSED);
	}

	public void mouseReleased(MouseEvent e) {
		// ui windows
		ListIterator<UIWindow> it = windows.listIterator(windows.size());
		while (it.hasPrevious()) {
			if (it.previous().addMouseInputEvent(e, MouseEventType.RELEASED)) return;
		}

		townRenderer.addMouseInputEvent(e, MouseEventType.RELEASED);
	}

	public void mouseDragged(MouseEvent e) {
		// ui windows
		ListIterator<UIWindow> it = windows.listIterator(windows.size());
		while (it.hasPrevious()) {
			if (it.previous().addMouseInputEvent(e, MouseEventType.DRAGGED)) return;
		}

		townRenderer.addMouseInputEvent(e, MouseEventType.DRAGGED);
	}

	public void mouseMoved(MouseEvent e) {
		// ui windows
		ListIterator<UIWindow> it = windows.listIterator(windows.size());
		while (it.hasPrevious()) {
			if (it.previous().addMouseInputEvent(e, MouseEventType.MOVED)) return;
		}

		townRenderer.addMouseInputEvent(e, MouseEventType.MOVED);
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
	
	public enum MouseEventType {
		CLICKED, PRESSED, RELEASED, DRAGGED, MOVED;
	}
}
