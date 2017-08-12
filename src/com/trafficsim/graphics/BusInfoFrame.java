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
	private JList personList;
	
	public BusInfoFrame(TownDesktopPane rootDesktop, Bus bus, int dx, int dy) {
		// Call super and set bus context, add an ancestor listener
		super(rootDesktop, "Bus: " + bus.getSchedule().getSchedule().getName(), dx, dy);
		this.bus = bus;
		
		// Create the person list and a custom cell renderer for displaying the person's id and name
		personList = new JList();
	    personList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		personList.setCellRenderer(new DefaultListCellRenderer() {
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
		updatePersonList();
		JScrollPane scroller = new JScrollPane(personList);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller);
		
		packFrame();
	}
	
	public void updatePersonList() {
		personList.setListData(bus.getPersons().toArray());
		personList.setBorder(BorderFactory.createTitledBorder("Personen (" + bus.getPersons().size() + "):"));
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