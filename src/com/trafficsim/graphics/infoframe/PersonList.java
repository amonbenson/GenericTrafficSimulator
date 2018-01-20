package com.trafficsim.graphics.infoframe;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.trafficsim.town.Person;

public class PersonList extends JList {
	public PersonList() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof Person) {
                	Person person = (Person) value;
                    ((JLabel) renderer).setText(person.getID() + ": " + person.getName());
                }
                return renderer;
            }
        });
	}
	
	public void updateList(ArrayList<Person> persons) {
		setListData(persons.toArray());
		setBorder(BorderFactory.createTitledBorder("Persons (" + persons.size() + "):"));
	}
}
