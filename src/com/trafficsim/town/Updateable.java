package com.trafficsim.town;

public interface Updateable {
	public void update(Town t); //Einen Tick +
	public void revert(Town t); //Einen Tick -
}
