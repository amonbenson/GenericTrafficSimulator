package com.trafficsim.graphics;

import com.trafficsim.town.Town;

public class AutoUpdater implements Runnable {
	
	public static final long TICK_SPEED = 10; // Tick speed in ms
	
	private Town town;
	private boolean running;
	
	public AutoUpdater(Town town) {
		this.town = town;
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
				
			} catch (Exception ex) {
				
			}
		}
	}
}
