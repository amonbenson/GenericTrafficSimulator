package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ConsolePane extends JPanel {
	
	public static final int DEFAULT_WIDTH = 200;
	
	public static final int DEFAULT_X = 10, DEFAULT_Y = 20, DEFAULT_PART_SIZE = 50;
	
	private ArrayList<String> lines;
	
	public ConsolePane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(FrameLauncher.highDPI(DEFAULT_WIDTH), 1));
		
		clear();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		int sx = FrameLauncher.highDPI(DEFAULT_X);
		int sy = FrameLauncher.highDPI(DEFAULT_Y);
		int dx = FrameLauncher.highDPI(DEFAULT_PART_SIZE);
		int dy = getFontMetrics(getFont()).getHeight();
		
		g.setColor(Color.white);
		for (String line : lines) {
			String[] parts = line.split("\t");
			sx = DEFAULT_X;
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
				g.drawString(part, sx, sy);
				sx += dx;
			}
			sy += dy;
		}
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}
	
	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}
	
	public void append(String line) {
		lines.add(line);
	}
	
	public void clear() {
		lines = new ArrayList<String>();
	}
}
