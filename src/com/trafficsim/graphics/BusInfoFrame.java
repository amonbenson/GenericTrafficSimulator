package com.trafficsim.graphics;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Person;

public class BusInfoFrame extends InfoFrame {
	
	private Bus bus;
	private PersonList personList;
	
	public BusInfoFrame(TownDesktopPane rootDesktop, Bus bus, int dx, int dy) {
		// Call super and set bus context, add an ancestor listener
		super(rootDesktop, "Bus: " + bus.getSchedule().getSchedule().getName(), dx, dy);
		this.bus = bus;
		
		// Create the person list
		personList = new PersonList();
		updatePersonList();
		JScrollPane scroller = new JScrollPane(personList);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller);
		
		packFrame();
	}
	
	public void updatePersonList() {
		personList.updateList(bus.getPersons());
	}
	
	public Person getSelectedPerson() {
		return (Person) personList.getSelectedValue();
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		personList.addListSelectionListener(listener);
	}
	
	public void removeListSelectionListener(ListSelectionListener listener) {
		personList.removeListSelectionListener(listener);
	}
	
	public Bus getBus() {
		return bus;
	}
}