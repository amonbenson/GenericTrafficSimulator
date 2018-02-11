package com.trafficsim.graphics.infoframe;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.trafficsim.graphics.TownDesktopPane;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.StreetTile;

public class HouseTileInfoFrame extends InfoFrame {

	private HouseTile tile;
	private StreetTile nextStation;
	
	private JButton openNextStation;
	private JLabel numPersons;
	
	public HouseTileInfoFrame(TownDesktopPane rootDesktop, HouseTile tile, int dx, int dy) {
		super(rootDesktop, null, dx, dy);
		this.tile = tile;
		nextStation = tile.getNextStation();

		setTitle("House Tile");
		
		openNextStation = new JButton("Show next Station");
		if (nextStation == null) {
			openNextStation.setEnabled(false); // Disable button if no station is near
		}
		add(openNextStation);
		
		numPersons = new JLabel("Persons: " + tile.getNumberPersons());
		add(numPersons);
		
		packFrame();
	}
	
	public StreetTile getNextStation() {
		return nextStation;
	}

	public void addNextStationActionListener(ActionListener listener) {
		openNextStation.addActionListener(listener);
	}
	
	public void removeNextStationActionListener(ActionListener listener) {
		openNextStation.removeActionListener(listener);
	}

	public HouseTile getTile() {
		return tile;
	}
	
}
