package com.trafficsim.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KListener implements KeyListener {
	private FrameLauncher frame;

	public KListener(FrameLauncher frame) {
		this.frame = frame;
	}
	
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			frame.simulation.getTown().update();
			frame.repaint();
		}
	}
}