package com.trafficsim.town;

public enum PersonRoutingOption {
	/**
	 * Der Start ist zufällig und das Ende auch.
	 * 
	 */
	RANDOM_START_RANDOM_END,
	/**
	 * Der Start liegt irgendwo in einem Haus, diese Wahrscheinlichkeit ist abhängig 
	 * von dem Wert <code>numberOfPersons</code> des Hauses.
	 * 
	 */
	HOUSE_START_RANDOM_END,
	/**
	 * Die Start liegt irgendwo in einem Haus, diese Wahrscheinlichkeit ist abhängig
	 * von dem Wert <code>numberOfPersons</code> des Hauses.
	 * Das Ende liegt auch auf einem Haus, diese Wahrscheinlichkeit ist abhängig
	 * von dem Wert <code></code> des Hauses.
	 */
	HOUSE_START_HOUSE_END;
	
	public static PersonRoutingOption getDefault() {
		return HOUSE_START_RANDOM_END;
	}
	
}
