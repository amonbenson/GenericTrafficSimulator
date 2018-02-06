package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;

public class HouseTile extends Tile {

	private ArrayList<Person> persons;
	/*
	 * Gibt die Anzahl an Personen an, welche in diesem Haus leben.
	 */
	private int numberPersons;
	/*
	 * Gibt den Wert/Faktor an, wie interessant dieser Ort für eine mögliche Ankunft ist.
	 * Die Zahl allein sagt nichts über die Häufigkeit an ankommenden Personen aus,
	 * die Wahrscheinlichkeit, dass eine Person hierhin fahren möchte, beträgt
	 * 		factorInterest/town.calcAllInterest();
	 * Natürlich muss bei der Personen-Weg-Generierung dieser Wert nicht beachtet werden.
	 * Diese Option kann in PersonRoutingOption eingestellt werden. (<code>HOUSE_START_HOUSE_END</code>)
	 */
	private float factorInterest;
	
	public HouseTile(int x, int y, int numberPersons, float factorInterest) {
		super(x, y);
		this.persons = new ArrayList<Person>(numberPersons);
		this.numberPersons = numberPersons;
		this.factorInterest = factorInterest;
	}

	public ArrayList<Person> getPersons() {
		return persons;
	}

	public int getNumberPersons() {
		return numberPersons;
	}
	
	public float getFactorInterest() {
		return factorInterest;
	}
	
	public void setPersons(ArrayList<Person> persons) {
		this.persons = persons;
	}

	public void setNumberPersons(int numberPersons) {
		this.numberPersons = numberPersons;
	}
	
	public void setFactorInterest(float factorInterest) {
		this.factorInterest = factorInterest;
	}

	@Override
	public String toString() {
		return 	super.toString()+"Personen: "+Arrays.toString(persons.toArray())+"\n"
				+"Anzahl wohnender Personen: "+numberPersons+"\n"
				+"Interessenfaktor: "+factorInterest+"\n";
	}
}
