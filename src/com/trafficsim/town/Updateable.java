package com.trafficsim.town;

public interface Updateable {
	public void update(); //Einen Tick +
	public void revert(); //Einen Tick -
}
