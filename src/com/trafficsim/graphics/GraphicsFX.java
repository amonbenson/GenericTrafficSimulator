package com.trafficsim.graphics;

import java.awt.Toolkit;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GraphicsFX {
	public static final boolean IS_HIGH_DPI = Toolkit.getDefaultToolkit().getScreenResolution() >= 216;
	
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
}
