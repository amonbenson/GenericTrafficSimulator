package com.trafficsim.graphics;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.town.Person;
import com.trafficsim.town.StreetTile;

public class StreetTileInfoFrame extends InfoFrame {

	private StreetTile tile, nextStation;
	
	private JButton openNextStation;
	private PersonList personList;
	
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

		add(new JLabel("Speed: " + FrameLauncher.round(tile.getMaxSpeed(), 3)));
		
		add(new JLabel("Waiting persons:"));
		personList = new PersonList();
		JScrollPane scroller = new JScrollPane(personList);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller);
		
		packFrame();
	}
	
	public void updatePersonList() {
		personList.updateList(tile.getPersons());
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
