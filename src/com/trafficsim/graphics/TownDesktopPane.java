package com.trafficsim.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Person;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class TownDesktopPane extends JDesktopPane implements MouseListener, ListSelectionListener {

	public static final double BUS_SIZE = 0.5;
	public static final int FRAME_LAYER_SPACE = 70;
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	private double tileSize;

	private Bus focusBus;
	private Person focusPerson;
	
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
		
		// Draw route of focus bus
		if (focusBus != null) {
			g.setColor(new Color(0, 255, 0, 128));
			g.setStroke(new BasicStroke((int) (0.1 * tileSize)));
			for (int i = 0; i < focusBus.getSchedule().getSchedule().getWaypointSize() - 1; i++) {
				Waypoint w1 = focusBus.getSchedule().getSchedule().getWaypoint(i);
				Waypoint w2 = focusBus.getSchedule().getSchedule().getWaypoint(i + 1);
				g.drawLine(
						(int) (w1.getX() * tileSize), 
						(int) (w1.getY() * tileSize), 
						(int) (w2.getX() * tileSize), 
						(int) (w2.getY() * tileSize)
				);
			}
		}
		
		// Draw busses
		for (Bus b : town.getBusses()) {
			g.setColor(Color.black);
			if (b == focusBus) g.setColor(Color.green);
			g.fillRect((int) (b.getX() * tileSize - tileSize * BUS_SIZE / 2), (int) (b.getY() * tileSize - tileSize * BUS_SIZE / 2), 
					(int) (tileSize * BUS_SIZE), (int) (tileSize * BUS_SIZE) );
		}
		
		// Draw lines to the internal frames
		g.setColor(Color.white);
		g.setStroke(new BasicStroke(3));
		for (JInternalFrame iFrame : getAllFrames()) {
			if (iFrame instanceof BusInfoFrame) {
				BusInfoFrame frame = (BusInfoFrame) iFrame;
				frame.updatePersonList(); // Update the person list
				
				int sx = (int) (frame.getBus().getX() * tileSize);
				int sy = (int) (frame.getBus().getY() * tileSize);
				int ex = frame.getX() + frame.getWidth() / 2;
				int ey = frame.getY() + frame.getHeight() / 2;
				
				g.drawLine(sx, sy, ex, ey);
			}
		}
	}
	
	public void updateFocusElement() {
		JInternalFrame focus = getSelectedFrame();
		if (focus == null) {
			focusBus = null;
			focusPerson = null;
			return;
		}
		
		if (focus instanceof BusInfoFrame) {
			focusBus = ((BusInfoFrame) focus).getBus();
			focusPerson = null;
		}
		if (focus instanceof PersonInfoFrame) {
			focusBus = null;
			focusPerson = ((PersonInfoFrame) focus).getPerson();
		}
	}

	private void createBusInfoFrame(Bus bus, int dx, int dy) {
		if (containsBusInfoFrame(bus)) return;
		
		// Create a bus info frame, add a person list selection listener
		BusInfoFrame frame = new BusInfoFrame(this, bus, dx, dy);
		frame.addListSelectionListener(this);
		frame.setVisible(true);
	}
	
	private void createPersonInfoFrame(Person person, int dx, int dy) {
		if (containsPersonInfoFrame(person)) return;
		
		// Create a person info frame
		PersonInfoFrame frame = new PersonInfoFrame(this, person, dx, dy);
		frame.setVisible(true);
	}

	private boolean containsBusInfoFrame(Bus bus) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof BusInfoFrame) {
				// Return true if the frame's bus matches the bus argument (then we already have
				// a bus info frame that matches this bus).
				if (((BusInfoFrame) frame).getBus() == bus) return true;
			}
		}
		return false;
	}

	private boolean containsPersonInfoFrame(Person person) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof PersonInfoFrame) {
				// Return true if the frame's person matches the person argument (then we already have
				// a person info frame that matches this person).
				if (((PersonInfoFrame) frame).getPerson() == person) return true;
			}
		}
		return false;
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
				createBusInfoFrame(bus, busMidX + FRAME_LAYER_SPACE, busMidY + FRAME_LAYER_SPACE);
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

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() instanceof Component) {
			// Get the root internal frame for the current jlist
			Component rootFrame = SwingUtilities.getAncestorOfClass(JInternalFrame.class, (Component) e.getSource());
			
			// This will be called if someone in a bus info frame's person list was selected
			if (rootFrame instanceof BusInfoFrame) {
				BusInfoFrame source = (BusInfoFrame) rootFrame;
				Person person = source.getSelectedPerson();
				if (person != null) {
					// We now have a person, create an info frame
					createPersonInfoFrame(person, source.getX() + FRAME_LAYER_SPACE, source.getY() + FRAME_LAYER_SPACE);
				}
			}
		}
	}
}
