package com.trafficsim.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class UIWindow implements UIElement {

	public static final int UPPER_BORDER = 40, BORDER = 3;
	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 500;
	
	private GUI gui;
	
	private int x, y, width, height;
	private boolean visible;

	private boolean moving;
	private Point lastMousePos;
	
	public UIWindow(GUI gui) {
		this.gui = gui;
		
		x = 0;
		y = 0;
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;

		lastMousePos = new Point();
		moving = false;
		
		visible = false;
	}

	public void repaint(int screenW, int screenH, Graphics2D g) {
		// Return if not visible
		if (!visible) return;
		
		// Render some frame
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRect(getX(), getY(), getWidth(), getHeight());

		g.setStroke(new BasicStroke(5));
		g.setColor(new Color(0, 0, 0));
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		g.drawRect(getX(), getY(), getWidth(), UPPER_BORDER);
		g.setStroke(new BasicStroke(1));
		
		// Render the window content
		g.translate(getX() + BORDER, getY() + UPPER_BORDER);
		repaintContent(getWidth() - BORDER * 2, getHeight() - UPPER_BORDER - BORDER, g);
		g.translate(-getX() - BORDER, -getY() - UPPER_BORDER);
		
		// Render x button
		g.setColor(Color.red);
		g.fillOval(getX() + getWidth() - UPPER_BORDER, getY(), UPPER_BORDER, UPPER_BORDER);
	}
	
	public boolean addMouseInputEvent(MouseEvent e, GUI.MouseEventType type) {
		// Reset moving
		if (type == GUI.MouseEventType.RELEASED) moving = false;
		
		if (asRectangle().contains(e.getPoint())
				|| (type == GUI.MouseEventType.DRAGGED && moving)) {
			int tx = e.getX() - getX(), ty = e.getY() - getY();
			
			// Move this window to the front (last index in list)
			if (type == GUI.MouseEventType.PRESSED) {
				gui.removeUIWindow(this);
				gui.addUIWindow(this);
			}
			
			// mouse event in upper border
			if (ty < UPPER_BORDER && type == GUI.MouseEventType.PRESSED) {
				
				// X-Button was pressed
				if (tx > getWidth() - UPPER_BORDER) {
					gui.removeUIWindow(this);
				}
				
				// Start moving
				else {
					moving = true;
					lastMousePos = e.getPoint();
				}
				
			} else mouseInputEvent(e, tx - BORDER, ty - UPPER_BORDER, type);
			
			// UIWindow was moved
			if (moving && type == GUI.MouseEventType.DRAGGED) {
				setX(getX() + e.getX() - lastMousePos.x);
				setY(getY() + e.getY() - lastMousePos.y);
				lastMousePos = e.getPoint();
			}
			
			// repaint gui
			gui.repaint();

			// Event was catched, so return true
			return true;
		}
		
		// Reset last mouse pos
		lastMousePos = null;
		
		return false;
	}
	
	public abstract void repaintContent(int width, int height, Graphics2D g);
	
	public abstract void mouseInputEvent(MouseEvent e, int tx, int ty, GUI.MouseEventType type);
	
	public Rectangle asRectangle() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
