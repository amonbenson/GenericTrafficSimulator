package com.trafficsim.graphics.infoframe;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.graphics.SimulationFrameLauncher;
import com.trafficsim.graphics.TownDesktopPane;
import com.trafficsim.town.Person;
import com.trafficsim.town.StreetTile;

public class StreetTileInfoFrame extends InfoFrame {

	private StreetTile tile, nextStation;
	
	private JButton openNextStation;
	
	private PersonList personList;
	private BusScheduleList busList;
	
	public StreetTileInfoFrame(TownDesktopPane rootDesktop, StreetTile tile, int dx, int dy) {
		super(rootDesktop, null, dx, dy);
		this.tile = tile;
		nextStation = tile.getNextStation(rootDesktop.getTown().getTiles());

		setTitle("Street Tile");
		
		openNextStation = new JButton("Show next Station");
		if (tile == nextStation || nextStation == null) {
			openNextStation.setEnabled(false); // Disable button if tile is a station or no station is near
		}
		add(openNextStation);

		add(new JLabel("Speed: " + SimulationFrameLauncher.round(tile.getMaxSpeed(), 3)));
		
		personList = new PersonList();
		JScrollPane personS = new JScrollPane(personList);
		personS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		personS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(personS);
		
		busList = new BusScheduleList();
		JScrollPane busS = new JScrollPane(busList);
		busS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		busS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(busS);
		
		packFrame();
	}
	
	public void updateInfo() {
		personList.updateList(tile.getPersons());
		busList.updateList(tile.getSchedules());
	}
	
	public StreetTile getNextStation() {
		return nextStation;
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

	public void addNextStationActionListener(ActionListener listener) {
		openNextStation.addActionListener(listener);
	}
	
	public void removeNextStationActionListener(ActionListener listener) {
		openNextStation.removeActionListener(listener);
	}

	public StreetTile getTile() {
		return tile;
	}
}
