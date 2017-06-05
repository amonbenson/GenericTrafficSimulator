package com.trafficsim.graphics;

import java.awt.Graphics2D;

public interface UIElement {
	public void update(int screenW, int screenH, int delta);
	public void render(int screenW, int screenH, Graphics2D g);
}
