package com.trafficsim.graphics;

import javax.swing.JDesktopPane;
import javax.swing.JLabel;

import com.trafficsim.town.Person;

public class PersonInfoFrame extends InfoFrame {

	private Person person;
	
	JLabel idLabel;
	
	public PersonInfoFrame(TownDesktopPane rootDesktop, Person person, int dx, int dy) {
		super(rootDesktop, person.getName(), dx, dy);
		this.person = person;
		
		idLabel = new JLabel("ID: " + person.getID());
		add(idLabel);
		
		packFrame();
	}

	public Person getPerson() {
		return person;
	}

}
