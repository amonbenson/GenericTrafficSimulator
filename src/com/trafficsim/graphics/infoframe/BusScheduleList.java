package com.trafficsim.graphics.infoframe;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.trafficsim.town.Schedule;
import com.trafficsim.town.Waypoint;

public class BusScheduleList extends JList {
	public BusScheduleList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof Schedule) {
                	Schedule schedule = (Schedule) value;
                    ((JLabel) renderer).setText(schedule.getName());
                }
                return renderer;
            }
        });
	}
	
	public void updateList(ArrayList<Schedule> schedules) {
		setListData(schedules.toArray());
		setBorder(BorderFactory.createTitledBorder("Bus Routes (" + schedules.size() + "):"));
	}
}
