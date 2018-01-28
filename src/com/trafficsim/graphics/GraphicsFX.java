package com.trafficsim.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JLabel;

public class GraphicsFX {
	public static final boolean IS_HIGH_DPI = Toolkit.getDefaultToolkit().getScreenResolution() >= 216;

	public static Font FONT_DEFAULT;
	public static Font FONT_BIG;
	
	public static int highDPI(int value) {
		if (IS_HIGH_DPI) return value * 2;
		return value;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    // If the value is NaN or Infinity, don't make any operations
	    if (!Double.isFinite(value)) return value;

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static Color changeAlpha(Color c, int alpha) {
		if (c == null) return null;
		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	
	public static void retrieveFont() {
		JLabel dummy = new JLabel();
		
		FONT_DEFAULT = dummy.getFont();
		if (FONT_DEFAULT == null) FONT_DEFAULT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
		
		FONT_BIG = FONT_DEFAULT.deriveFont(FONT_DEFAULT.getSize() * 2.5f);
	}
}
