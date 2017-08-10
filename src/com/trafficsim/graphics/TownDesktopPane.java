package com.trafficsim.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Person;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;

public class TownDesktopPane extends JDesktopPane implements MouseListener {

	public static final double BUS_SIZE = 0.5;
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	private double tileSize;
	
	public TownDesktopPane(FrameLauncher frameLauncherContext, Town town) {
		super();
		
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
		tileSize = 1.0;
		
		addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		
		Tile[][] tiles = town.getTiles();
		
		tileSize = Math.min(getWidth() / (double) town.getSizeX(), getHeight() / (double) town.getSizeY());
		
		// Draw town tiles
		for (int x = 0; x < town.getSizeX(); x++) {
			for (int y = 0; y < town.getSizeY(); y++) {
				Tile tile = tiles[x][y];
				int dx = (int) (x * tileSize);
				int dy = (int) (y * tileSize);
				int ds = (int) tileSize + 1;
				
				if (tile instanceof StreetTile) {
					StreetTile s = (StreetTile) tile;
					if (s.isStation()) {
						g.setColor(Color.LIGHT_GRAY);
					} else {
						g.setColor(Color.gray);
					}
				}
				if (tile instanceof HouseTile) {
					g.setColor(Color.green);
				}
				g.fillRect(dx, dy, ds, ds);
				if (tile instanceof HouseTile) {
					g.setColor(Color.white);
					g.drawString(String.valueOf(((HouseTile) tile).getNumberPersons()), dx+(int)(tileSize/2), dy+(int)(tileSize/2));
				} else if (tile instanceof StreetTile) {
					g.setColor(Color.white);
					g.drawString(String.valueOf(((StreetTile) tile).getPersons().size()), dx+(int)(tileSize/2), dy+(int)(tileSize/2));
				}
				
				g.setColor(Color.black);
				g.drawRect(dx, dy, ds, ds);
			}
		}
		
		// Draw busses
		for (Bus b : town.getBusses()) {
			g.setColor(Color.black);
			g.fillRect((int) (b.getX() * tileSize - tileSize * BUS_SIZE / 2), (int) (b.getY() * tileSize - tileSize * BUS_SIZE / 2), 
					(int) (tileSize * BUS_SIZE), (int) (tileSize * BUS_SIZE) );
		}
		
		// Draw lines to the internal frames
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(3));
		for (JInternalFrame iFrame : getAllFrames()) {
			if (iFrame instanceof BusFrame) {
				BusFrame frame = (BusFrame) iFrame;
				frame.updatePersonList(); // Update the person list
				
				int sx = (int) (frame.getBus().getX() * tileSize);
				int sy = (int) (frame.getBus().getY() * tileSize);
				int ex = frame.getX() + frame.getWidth() / 2;
				int ey = frame.getY() + frame.getHeight() / 2;
				
				g.drawLine(sx, sy, ex, ey);
			}
		}
	}

	private void createBusInfoWindow(Bus bus, int dx, int dy) {
		// Create an internal frame and use the bus name as the title
		BusFrame frame = new BusFrame(bus, dx, dy);

		// Copy the input and aciton maps to keep all key bindings
		frame.getRootPane().setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, getInputMap());
		frame.getRootPane().setActionMap(getActionMap());

		// Add the frame to the desktop and make visible
		add(frame);
		frame.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		// CLICK EVENT ON BUS
		Iterator<Bus> busIt = town.getBusses().iterator();
		while (busIt.hasNext()) {
			Bus bus = busIt.next();
			
			int busMidX = (int) (bus.getX() * tileSize);
			int busMidY = (int) (bus.getY() * tileSize);
			int busSize = (int) (tileSize * BUS_SIZE);

			// Check if user clicked on bus
			if (new Rectangle(busMidX - busSize / 2, busMidY - busSize / 2, busSize, busSize).contains(e.getPoint())) {
				createBusInfoWindow(bus, busMidX + 20, busMidY + 20);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
	
	private class BusFrame extends JInternalFrame implements AncestorListener {
		
		private static final int WIDTH = 300;
		
		private Bus bus;
		
		private JList personList;
		
		public BusFrame(Bus bus, int dx, int dy) {
			// Set the name and bus context, add an ancestor listener
			super("Bus: " + bus.getSchedule().getSchedule().getName());
			this.bus = bus;
			addAncestorListener(this);
			
			// Create the person list
			personList = new JList();
			personList.setCellRenderer(new DefaultListCellRenderer() {
	            @Override
	            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	                if (renderer instanceof JLabel && value instanceof Person) {
	                    // Here value will be of the Type 'CD'
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
			
			// Tweak some settings
			setLocation(dx, dy);
			setClosable(true);
			setMaximizable(false);
			setIconifiable(false);
		}
		
		private void updatePersonList() {
			personList.setListData(bus.getPersons().toArray());
			personList.setBorder(BorderFactory.createTitledBorder("Personen (" + bus.getPersons().size() + "):"));
			pack();
			setSize(WIDTH, getHeight());
		}
		
		public Bus getBus() {
			return bus;
		}

		public void ancestorAdded(AncestorEvent event) {
			
		}

		public void ancestorRemoved(AncestorEvent event) {
			TownDesktopPane.this.repaint();
		}

		public void ancestorMoved(AncestorEvent event) {
			// This will be called when the frame is opened and moved arround. Update the desktop.
			TownDesktopPane.this.repaint();
		}
	}
}
