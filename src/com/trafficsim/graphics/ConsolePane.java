package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public abstract class ConsolePane extends JPanel implements MouseListener {
	
	public static final int DEFAULT_WIDTH = 290;
	
	public static final int BORDER_X = 10, BORDER_Y = 20, TAB_SIZE = 120;
	
	private ArrayList<String> lines;
	private int sx, sy, dx, dy;
	
	public ConsolePane() {
		addMouseListener(this);
		
		sx = FrameLauncher.highDPI(BORDER_X);
		sy = FrameLauncher.highDPI(BORDER_Y);
		dx = FrameLauncher.highDPI(TAB_SIZE);
		dy = getFontMetrics(getFont()).getHeight();
		
		clear();
	}
	
	public void updatePreferredSize() {
		// If we have no lines or the console is added nowhere, return
		if (lines == null) return;
		if (getParent() == null) return;
		
		// Get an appropriate size for our pane
		Dimension size = new Dimension(
				FrameLauncher.highDPI(DEFAULT_WIDTH),
				lines.size() * dy + sy * 2
		);
		
		// If the pane is already that size, return (don't wanna paint things more than once and cause lag)
		if (getPreferredSize().width == size.width && getPreferredSize().height == size.height) return;
		
		// Set the new size and update the overlaying scroll pane
		setPreferredSize(size);
		getParent().revalidate();
		getParent().repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		// Clear background
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// Temporary coordinates, to draw each line
		int x = sx;
		int y = sy;
		
		for (String line : lines) {
			g.setColor(Color.white);
			
			String[] parts = line.split("\t");
			x = BORDER_X;
			for (String part : parts) {
				if (part.startsWith("%%")) part.replace("%%", "%");
				else if (part.startsWith("%")) {
					// Get a hex color string
					int hex = Integer.parseInt(part.substring(1, 7), 16);
					part = part.substring(7);
					
					// Set the color
					g.setColor(new Color(
							(hex & 0xFF0000) >> 16,
							(hex & 0xFF00) >> 8,
							(hex & 0xFF)
					));

				}
				g.drawString(part, x, y);
				x += dx;
			}
			y += dy;
		}
		
		// Update the dimensions
		updatePreferredSize();
	}
	
	public abstract void lineClicked(int line, String content);
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public void append(String line) {
		lines.add(line);
	}
	
	public int getNumLines() {
		return lines.size();
	}
	
	public void clear() {
		lines = new ArrayList<String>();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		int line = (int) ((double) (e.getY() - sy) / dy + 0.8);
		if (line < 0 || line >= getNumLines()) return;
		
		lineClicked(line, lines.get(line));
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
}
