package com.trafficsim.graphics;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public interface UIElement {
	public void repaint(int screenW, int screenH, Graphics2D g);
	public boolean addMouseInputEvent(MouseEvent e, GUI.MouseEventType type);
}
