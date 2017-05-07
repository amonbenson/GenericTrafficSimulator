package com.trafficsim.town;

import java.util.ArrayList;

public class HouseTile extends Tile {

	private ArrayList<Person> persons;
	private int numberPersons;
	
	public HouseTile(int x, int y, int numberPersons) {
		super(x, y);
		this.persons = new ArrayList<Person>(numberPersons);
		this.numberPersons = numberPersons;
	}

	public ArrayList<Person> getPersons() {
		return persons;
	}

	public int getNumberPersons() {
		return numberPersons;
	}
	
	public void setPersons(ArrayList<Person> persons) {
		this.persons = persons;
	}

	public void setNumberPersons(int numberPersons) {
		this.numberPersons = numberPersons;
	}

}
