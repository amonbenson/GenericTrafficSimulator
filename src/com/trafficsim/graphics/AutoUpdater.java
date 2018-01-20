package com.trafficsim.graphics;

import java.util.logging.Level;

import javax.swing.JFrame;

import com.trafficsim.sim.Simulation;
import com.trafficsim.town.Town;

public class AutoUpdater implements Runnable {

	private static final long MIN_TICK_SPEED = 1;
	private static final long MAX_TICK_SPEED = 500;
	private static final double MULT_TICK_SPEED = 1.3;
	
	private SimulationFrameLauncher frameLauncher; // FrameLauncher context

	private long tickSpeed;
	private boolean running;
	
	public AutoUpdater(SimulationFrameLauncher frameLauncher) {
		this.frameLauncher = frameLauncher;
		
		tickSpeed = 30;
		running = false;
	}
	
	public void faster() {
		setTickSpeed((long) (getTickSpeed() / MULT_TICK_SPEED) - 1);
	}
	
	public void slower() {
		setTickSpeed((long) (getTickSpeed() * MULT_TICK_SPEED) + 1);
	}
	
	public long getTickSpeed() {
		return tickSpeed;
	}
	
	public void setTickSpeed(long tickSpeed) {
		if (tickSpeed > MAX_TICK_SPEED) tickSpeed = MAX_TICK_SPEED;
		if (tickSpeed < MIN_TICK_SPEED) tickSpeed = MIN_TICK_SPEED;
		this.tickSpeed = tickSpeed;
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
				frameLauncher.getTownDesktopPane().getTown().update();
				
				// Render
				frameLauncher.getFrame().repaint();
				
				// Wait a tick
				Thread.sleep(tickSpeed);
			} catch (Exception ex) {
				Simulation.logger.log(Level.SEVERE, "Error while auto updating simulation!", ex);
			}
		}
	}

	public boolean isRunning() {
		return running;
	}
}
