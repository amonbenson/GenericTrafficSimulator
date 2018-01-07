package de.amonbenson.generic;

public interface Simulator {

	/**
	 * Create a simulation and run it, until a fitness value is found.
	 * 
	 * @param individual
	 *            The individual from which the simulation is going to be
	 *            generated
	 * 
	 * @return The fitness value for the current simulation
	 */
	public double simulate(Individual individual);

	/**
	 * Check if population has met termination condition
	 * 
	 * @param population
	 * @return boolean True if termination condition met, otherwise, false
	 */
	public boolean isTerminationConditionMet(Population population);
}
