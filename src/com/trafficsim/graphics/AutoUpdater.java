package com.trafficsim.graphics;

import java.util.logging.Level;

import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class AutoUpdater implements Runnable {
	
	public static final long TICK_SPEED = 10; // Tick speed in ms
	
	private GUI gui;
	private Town town;
	
	private boolean running;
	
	public AutoUpdater(GUI gui, Town town) {
		this.gui = gui;
		this.town = town;
		
		running = false;
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}

	public void run() {
		while (running) {
			try {
				// Update the town
				town.update();
				
				// Render
				gui.repaint();
				
				// Wait a tick
				Thread.sleep(TICK_SPEED);
			} catch (Exception ex) {
				Simulation.logger.log(Level.SEVERE, "Error while auto updating simulation!", ex);
			}
		}
	}

	public boolean isRunning() {
		return running;
	}
}
