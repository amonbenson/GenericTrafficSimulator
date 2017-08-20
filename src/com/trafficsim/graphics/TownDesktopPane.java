package com.trafficsim.graphics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.town.Bus;
import com.trafficsim.town.Event;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Person;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class TownDesktopPane extends JDesktopPane implements MouseListener, ListSelectionListener, ActionListener {

	public static final double BUS_SIZE = 0.5;
	public static final int FRAME_LAYER_SPACE = 35;
	
	private FrameLauncher frameLauncherContext;
	private Town town;
	
	private double tileSize;

	private Bus focusBus;
	private Person focusPerson;
	private Tile focusTile;
	
	private JToolBar toolBar;
	private JLabel timeLabel;
	private JToggleButton showRoutesButton, showRelationLinesButton;
	
	public TownDesktopPane(FrameLauncher frameLauncherContext, Town town) {
		super();
		
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
		tileSize = 1.0;
		
		addMouseListener(this);
		
		toolBar = new JToolBar();
		toolBar.setFloatable(true);
		frameLauncherContext.getFrame().add(BorderLayout.NORTH, toolBar);
		
		timeLabel = new JLabel("Time");
		toolBar.add(timeLabel);
		toolBar.addSeparator();
		
		showRoutesButton = new JToggleButton("show routes", true);
		showRoutesButton.setFocusable(false);
		showRoutesButton.addActionListener(this);
		toolBar.add(showRoutesButton);
		
		showRelationLinesButton = new JToggleButton("show relation lines", true);
		showRelationLinesButton.setFocusable(false);
		showRelationLinesButton.addActionListener(this);
		toolBar.add(showRelationLinesButton);
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
					g.setColor(new Color(0, 128, 0));
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
		
		if (showRoutesButton.isSelected()) {
			g.setStroke(new BasicStroke(FrameLauncher.highDPI((int) (0.06 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
			
			// Draw route of focus bus
			if (focusBus != null) {
				g.setColor(new Color(0, 255, 0, 128));
				
				Schedule schedule = focusBus.getSchedule().getSchedule();
				for (int i = 0; i < schedule.getWaypointSize() - 1; i++) {
					Waypoint w1 = schedule.getWaypoint(i);
					Waypoint w2 = schedule.getWaypoint(i + 1);
					g.drawLine(
							(int) (w1.getX() * tileSize), 
							(int) (w1.getY() * tileSize), 
							(int) (w2.getX() * tileSize), 
							(int) (w2.getY() * tileSize)
					);
				}
			}
			
			// Draw route of focus person (draw a strait line from origin to target station)
			if (focusPerson != null) {
				g.setColor(new Color(255, 0, 0, 128));
			
				Waypoint w1 = focusPerson.getRoute().getOrigin().toWaypoint();
				Waypoint w2 = focusPerson.getRoute().getTarget().toWaypoint();
				g.drawLine(
						(int) ((w1.getX() + 0.5) * tileSize), 
						(int) ((w1.getY() + 0.5) * tileSize), 
						(int) ((w2.getX() + 0.5) * tileSize), 
						(int) ((w2.getY() + 0.5) * tileSize)
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
		
		// Draw lines to the internal frames ("relation lines")
		if (showRelationLinesButton.isSelected()) {
			g.setStroke(new BasicStroke(FrameLauncher.highDPI((int) (0.01 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
			
			for (JInternalFrame iFrame : getAllFrames()) {
				if (iFrame instanceof StreetTileInfoFrame) { // STREET TILE INFO
					StreetTileInfoFrame frame = (StreetTileInfoFrame) iFrame;
					frame.updatePersonList(); // Update the person list
					
					g.setColor(Color.white);
					
					int sx = (int) ((frame.getTile().getX() + 0.5) * tileSize);
					int sy = (int) ((frame.getTile().getY() + 0.5) * tileSize);
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof HouseTileInfoFrame) { // HOUSE TILE INFO
					HouseTileInfoFrame frame = (HouseTileInfoFrame) iFrame;
					
					g.setColor(Color.white);
					
					int sx = (int) ((frame.getTile().getX() + 0.5) * tileSize);
					int sy = (int) ((frame.getTile().getY() + 0.5) * tileSize);
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof BusInfoFrame) { // BUS INFO
					BusInfoFrame frame = (BusInfoFrame) iFrame;
					frame.updatePersonList(); // Update the person list
	
					g.setColor(Color.white);
					if (frame.getBus() == focusBus) g.setColor(Color.green);
					
					int sx = (int) (frame.getBus().getX() * tileSize);
					int sy = (int) (frame.getBus().getY() * tileSize);
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof PersonInfoFrame) { // PERSON INFO
					PersonInfoFrame frame = (PersonInfoFrame) iFrame;
	
					g.setColor(Color.white);
					if (frame.getPerson() == focusPerson) g.setColor(Color.red);
					
					int sx = (int) (frame.getPerson().getX() * tileSize); // TODO: Person.getXY() funktioniert noch nicht?
					int sy = (int) (frame.getPerson().getY() * tileSize);
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
			}
		}
		
		// Draw the current time
		timeLabel.setText("Time: " + town.getTime());
		
		// Repaint the event console
		frameLauncherContext.getEventConsolePane().repaint();
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
		if (containsInfoFrame(bus)) return;
		
		// Create a bus info frame, add a person list selection listener
		BusInfoFrame frame = new BusInfoFrame(this, bus, dx, dy);
		frame.addListSelectionListener(this);
		frame.setVisible(true);
	}
	
	private void createPersonInfoFrame(Person person, int dx, int dy) {
		if (containsInfoFrame(person)) return;
		
		// Create a person info frame
		PersonInfoFrame frame = new PersonInfoFrame(this, person, dx, dy);
		frame.setVisible(true);
	}
	
	private void createStreetTileInfoFrame(StreetTile tile, int dx, int dy) {
		if (containsInfoFrame(tile)) return;
		
		// Create a tile info frame
		StreetTileInfoFrame frame = new StreetTileInfoFrame(this, tile, dx, dy);
		frame.addNextStationActionListener(this);
		frame.addListSelectionListener(this);
		frame.setVisible(true);
	}
	
	private void createHouseTileInfoFrame(HouseTile tile, int dx, int dy) {
		if (containsInfoFrame(tile)) return;
		
		// Create a tile info frame
		HouseTileInfoFrame frame = new HouseTileInfoFrame(this, tile, dx, dy);
		frame.addNextStationActionListener(this);
		frame.setVisible(true);
	}

	private boolean containsInfoFrame(Object object) {
		for (JInternalFrame frame : getAllFrames()) {
			if (frame instanceof BusInfoFrame && object instanceof Bus) {
				if (((BusInfoFrame) frame).getBus() == (Bus) object) return true;
			}
			if (frame instanceof PersonInfoFrame && object instanceof Person) {
				if (((PersonInfoFrame) frame).getPerson() == (Person) object) return true;
			}
			if (frame instanceof StreetTileInfoFrame && object instanceof StreetTile) {
				if (((StreetTileInfoFrame) frame).getTile() == (Tile) object) return true;
			}
			if (frame instanceof HouseTileInfoFrame && object instanceof HouseTile) {
				if (((HouseTileInfoFrame) frame).getTile() == (Tile) object) return true;
			}
		}
		return false;
	}

	public Town getTown() {
		return town;
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
				createBusInfoFrame(bus, busMidX + FrameLauncher.highDPI(FRAME_LAYER_SPACE), busMidY + FrameLauncher.highDPI(FRAME_LAYER_SPACE));
				return; // Return to avoid doubled mouse input events
			}
		}
		
		// CLICK EVENT ON TILE
		int tileX = (int) (e.getX() / tileSize);
		int tileY = (int) (e.getY() / tileSize);
		if (tileX >= 0 && tileX < town.getSizeX() && tileY >= 0 && tileY < town.getSizeY()) {
			Tile tile = town.getTiles()[tileX][tileY];
			if (tile instanceof StreetTile)
				createStreetTileInfoFrame(
						(StreetTile) tile,
						e.getX() + FrameLauncher.highDPI(FRAME_LAYER_SPACE),
						e.getY() + FrameLauncher.highDPI(FRAME_LAYER_SPACE)
				);
			if (tile instanceof HouseTile)
				createHouseTileInfoFrame(
						(HouseTile) tile,
						e.getX() + FrameLauncher.highDPI(FRAME_LAYER_SPACE),
						e.getY() + FrameLauncher.highDPI(FRAME_LAYER_SPACE)
				);
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
			
			// This will be called if someone in a street tile info frame's person list was selected
			if (rootFrame instanceof StreetTileInfoFrame) {
				StreetTileInfoFrame source = (StreetTileInfoFrame) rootFrame;
				Person person = source.getSelectedPerson();
				if (person != null) {
					// We now have a person, create an info frame
					createPersonInfoFrame(person, source.getX() + FRAME_LAYER_SPACE, source.getY() + FRAME_LAYER_SPACE);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// Events on tool bar toggles will cause a repaint
		if (e.getSource() == showRoutesButton) repaint();
		else if (e.getSource() == showRelationLinesButton) repaint();
		
		// The action event may be caused by an info frame
		else if (e.getSource() instanceof Component) {
			// Get the root internal frame for the current jlist
			Component rootFrame = SwingUtilities.getAncestorOfClass(JInternalFrame.class, (Component) e.getSource());
			
			// This will be called if the "open next station" button was pressed
			if (rootFrame instanceof StreetTileInfoFrame) {
				StreetTileInfoFrame source = (StreetTileInfoFrame) rootFrame;
				
				StreetTile nextStation = source.getNextStation();
				createStreetTileInfoFrame(
						nextStation,
						(int) ((nextStation.getX() + 0.5) * tileSize + FRAME_LAYER_SPACE),
						(int) ((nextStation.getY() + 0.5) * tileSize + FRAME_LAYER_SPACE)
				);
			}
			if (rootFrame instanceof HouseTileInfoFrame) {
				HouseTileInfoFrame source = (HouseTileInfoFrame) rootFrame;
				
				StreetTile nextStation = source.getNextStation();
				createStreetTileInfoFrame(
						nextStation,
						(int) ((nextStation.getX() + 0.5) * tileSize + FRAME_LAYER_SPACE),
						(int) ((nextStation.getY() + 0.5) * tileSize + FRAME_LAYER_SPACE)
				);
			}
		}
	}
}
