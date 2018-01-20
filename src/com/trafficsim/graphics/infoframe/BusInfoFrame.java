package com.trafficsim.graphics.infoframe;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.graphics.TownDesktopPane;
import com.trafficsim.town.Bus;
import com.trafficsim.town.Person;
import com.trafficsim.town.Waypoint;

public class BusInfoFrame extends InfoFrame {
	
	private Bus bus;
	
	private JLabel directionLabel;
	private StationList stationList;
	private PersonList personList;
	
	public BusInfoFrame(TownDesktopPane rootDesktop, Bus bus, int dx, int dy) {
		// Call super and set bus context, add an ancestor listener
		super(rootDesktop, "Bus: " + bus.getSchedule().getSchedule().getName(), dx, dy);
		this.bus = bus;
		
		// Create a direction info label
		directionLabel = new JLabel();
		add(directionLabel);

		// Create the station list
		stationList = new StationList();
		JScrollPane stationS = new JScrollPane(stationList);
		stationS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		stationS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(stationS);

		// Create the person list
		personList = new PersonList();
		JScrollPane personS = new JScrollPane(personList);
		personS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		personS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(personS);
		
		// Update person list and label and pack frame
		updateInfo();
		packFrame();
	}
	
	public void updateInfo() {
		directionLabel.setText("Direction: " + bus.getSchedule().getDirection().name());
		personList.updateList(bus.getPersons());
		stationList.updateList(bus.getSchedule().getSchedule().getStations());
	}
	
	public Waypoint getSelectedStation() {
		return (Waypoint) stationList.getSelectedValue();
	}
	
	public Person getSelectedPerson() {
		return (Person) personList.getSelectedValue();
	}
	
	public void addStationListSelectionListener(ListSelectionListener listener) {
		stationList.addListSelectionListener(listener);
	}
	
	public void removeStationListSelectionListener(ListSelectionListener listener) {
		stationList.removeListSelectionListener(listener);
	}
	
	public void addPersonListSelectionListener(ListSelectionListener listener) {
		personList.addListSelectionListener(listener);
	}
	
	public void removePersonListSelectionListener(ListSelectionListener listener) {
		personList.removeListSelectionListener(listener);
	}
	
	public Bus getBus() {
		return bus;
	}
}