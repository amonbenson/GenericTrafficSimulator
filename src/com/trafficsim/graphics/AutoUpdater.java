package com.trafficsim.graphics;

import java.util.logging.Level;

import javax.swing.JFrame;

import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class AutoUpdater implements Runnable {
	
	public static final long TICK_SPEED = 20; // Tick speed in ms
	
	private JFrame frame;
	private Town town;
	
	private boolean running;
	
	public AutoUpdater(JFrame frame, Town town) {
		this.frame = frame;
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
				frame.repaint();
				
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
