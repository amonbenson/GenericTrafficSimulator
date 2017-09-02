package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ConsolePane extends JPanel implements MouseListener {
	
	public static final int DEFAULT_WIDTH = 250;
	
	public static final int BORDER_X = 10, BORDER_Y = 20, TAB_SIZE = 60;
	
	private ArrayList<String> lines;
	private int sx, sy, dx, dy;
	
	public ConsolePane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(FrameLauncher.highDPI(DEFAULT_WIDTH), 1));
		
		addMouseListener(this);

		sx = FrameLauncher.highDPI(BORDER_X);
		sy = FrameLauncher.highDPI(BORDER_Y);
		dx = FrameLauncher.highDPI(TAB_SIZE);
		dy = getFontMetrics(getFont()).getHeight();
		
		clear();
	}

	@Override
	public void paintComponent(Graphics g) {
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
