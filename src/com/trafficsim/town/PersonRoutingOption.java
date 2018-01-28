package com.trafficsim.town;

public enum PersonRoutingOption {
	/**
	 * Der Start ist zufällig und das Ende auch.
	 * 
	 */
	RANDOM_START_RANDOM_END;
	/**
	 * Der Start liegt irgendwo in einem Haus, diese Wahrscheinlichkeit ist abhängig 
	 * von dem Wert <code>numberOfPersons</code> des Hauses.
	 * 
	 */
	//HOUSE_START_RANDOM_END;
	
	public static PersonRoutingOption getDefault() {
		return RANDOM_START_RANDOM_END;
	}
	
}
