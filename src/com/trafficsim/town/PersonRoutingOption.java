package com.trafficsim.town;

public enum PersonRoutingOption {
	/**
	 * Der Start ist zuf�llig und das Ende auch.
	 * 
	 */
	RANDOM_START_RANDOM_END;
	
	public static PersonRoutingOption getDefault() {
		return RANDOM_START_RANDOM_END;
	}
	
}
