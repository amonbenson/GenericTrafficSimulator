package com.trafficsim.graphics.infoframe;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.trafficsim.town.Person;
import com.trafficsim.town.Waypoint;

public class StationList extends JList {
	public StationList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof Waypoint) {
                	Waypoint station = (Waypoint) value;
                    ((JLabel) renderer).setText("station: " + (int) (station.getX() - 0.5) + ", " + (int) (station.getY() - 0.5));
                }
                return renderer;
            }
        });
	}
	
	public void updateList(ArrayList<Waypoint> stations) {
		setListData(stations.toArray());
		setBorder(BorderFactory.createTitledBorder("Stations (" + stations.size() + "):"));
	}
}
