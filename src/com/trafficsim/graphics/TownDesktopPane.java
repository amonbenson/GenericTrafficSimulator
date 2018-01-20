package com.trafficsim.graphics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.trafficsim.graphics.consolepane.ConsolePane;
import com.trafficsim.graphics.infoframe.BusInfoFrame;
import com.trafficsim.graphics.infoframe.HouseTileInfoFrame;
import com.trafficsim.graphics.infoframe.PersonInfoFrame;
import com.trafficsim.graphics.infoframe.PersonList;
import com.trafficsim.graphics.infoframe.StationList;
import com.trafficsim.graphics.infoframe.StreetTileInfoFrame;
import com.trafficsim.town.Bus;
import com.trafficsim.town.HouseTile;
import com.trafficsim.town.Person;
import com.trafficsim.town.Schedule;
import com.trafficsim.town.StreetTile;
import com.trafficsim.town.Tile;
import com.trafficsim.town.Town;
import com.trafficsim.town.Waypoint;

public class TownDesktopPane extends JDesktopPane implements MouseListener, MouseMotionListener, MouseWheelListener, ListSelectionListener, ActionListener, ComponentListener {

	public static final double BUS_DRAW_SIZE = 0.5;
	public static final double PERSON_DRAW_SIZE = 0.2;
	
	public static final int FRAME_LAYER_SPACE = 35;
	
	public static final int DEFAULT_TRANSPARENCY = 60;
	
	public static final double ZOOM_SPEED = 1.1;
	
	private SimulationFrameLauncher frameLauncherContext;
	private Town town;

	private double transX, transY, transZ;
	
	private int tileX, tileY;
	private double tileSize;

	private Bus focusBus;
	private Person focusPerson;
	private Tile focusTile;
	
	private JToolBar toolBar;
	private JToggleButton showRoutesButton, showRelationLinesButton, showNonCoveredTiles;
	
	private JLabel splashText;
	
	private int mouseXFlag, mouseYFlag, mouseDX, mouseDY;
	
	public TownDesktopPane(SimulationFrameLauncher frameLauncherContext, Town town) {
		super();
		
		this.frameLauncherContext = frameLauncherContext;
		this.town = town;
		
		// translation and zoom
		transX = 0.0;
		transY = 0.0;
		transZ = 1.0;
		
		// Tile position and size (updated when screen resizing)
		tileX = 10;
		tileY = 100;
		tileSize = 1.0;
		
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		setBackground(Color.black);
		
		// Create a custom desktop manager to prevent frames from beeing moved out of the desktop pane.
		DesktopManager manager = new DefaultDesktopManager() {
			@Override
			public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
				boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
				if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) {
					Container parent = f.getParent();
					Dimension parentSize = parent.getSize();
					int boundedX = (int) Math.min(Math.max(0, newX), parentSize.getWidth() - newWidth);
					int boundedY = (int) Math.min(Math.max(0, newY), parentSize.getHeight() - newHeight);
					f.setBounds(boundedX, boundedY, newWidth, newHeight);
				} else {
					f.setBounds(newX, newY, newWidth, newHeight);
				}
				if (didResize) {
					f.validate();
				}
			}

			protected boolean inBounds(JInternalFrame f, int newX, int newY, int newWidth, int newHeight) {
				if (newX < 0 || newY < 0)
					return false;
				if (newX + newWidth > f.getDesktopPane().getWidth())
					return false;
				if (newY + newHeight > f.getDesktopPane().getHeight())
					return false;
				return true;
			}
		};
		setDesktopManager(manager);
		
		// Create a toolbar and add its components
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frameLauncherContext.getFrame().add(BorderLayout.NORTH, toolBar);
		
		showRoutesButton = new JToggleButton("show routes", true);
		showRoutesButton.setFocusable(false);
		showRoutesButton.addActionListener(this);
		toolBar.add(showRoutesButton);
		
		showRelationLinesButton = new JToggleButton("show relation lines", true);
		showRelationLinesButton.setFocusable(false);
		showRelationLinesButton.addActionListener(this);
		toolBar.add(showRelationLinesButton);
		
		showNonCoveredTiles = new JToggleButton("show non covered tiles", true);
		showNonCoveredTiles.setFocusable(false);
		showNonCoveredTiles.addActionListener(this);
		toolBar.add(showNonCoveredTiles);
		
		// Create the splash text
		splashText = new JLabel("Splash");
		splashText.setForeground(Color.white);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		
		if (town == null) {
			g.setColor(Color.black);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.white);
			g.drawString("Nothing to display here.", GraphicsFX.highDPI(ConsolePane.BORDER_X), GraphicsFX.highDPI(ConsolePane.BORDER_Y));
			return;
		}
		
		Tile[][] tiles = town.getTiles();
		
		// Draw town tiles
		for (int x = 0; x < town.getSizeX(); x++) {
			for (int y = 0; y < town.getSizeY(); y++) {
				Tile tile = tiles[x][y];
				int dx = (int) (x * tileSize) + tileX;
				int dy = (int) (y * tileSize) + tileY;
				int ds = (int) tileSize + 1;
				
				// Draw the tile background
				if (tile instanceof StreetTile) {
					StreetTile s = (StreetTile) tile;
					if (s.isStation()) {
						g.setColor(Color.lightGray);
					} else {
						g.setColor(Color.gray);
					}
				}
				if (tile instanceof HouseTile) {
					g.setColor(new Color(0, 128, 0));
				}
				g.fillRect(dx, dy, ds, ds);
				
				// Draw the persons waiting on street tiles
				if (tile instanceof StreetTile)
					drawPersons(g, x + 0.15, y + 0.15, ((StreetTile) tile).getPersons().size());

				// Draw outline
				g.setColor(Color.black);
				g.drawRect(dx, dy, ds, ds);
				
				// Make redded out if not covered
				if (showNonCoveredTiles.isSelected()) {
					g.setColor(new Color(230, 30, 0, 128));
					
					if (tile.getNextStation(town.getTiles()) == null) {
						g.fillRect(dx, dy, ds, ds);
					}
				}
				
				// Draw the y coordinates
				if (x == 0) {
					g.setColor(Color.white);
					g.drawString("" + y, (int) (0.05 * tileSize) + tileX, (int) ((y + 0.5) * tileSize) + tileY);
				}
			}
			
			// Draw the x coordinates
			g.setColor(Color.white);
			g.drawString("" + x, (int) ((x + 0.5) * tileSize) + tileX, (int) (0.2 * tileSize) + tileY);
		}
		
		if (showRoutesButton.isSelected()) {
			g.setStroke(new BasicStroke(GraphicsFX.highDPI((int) (0.06 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
			
			// Draw route of focus bus
			if (focusBus != null) {
				
				Schedule schedule = focusBus.getSchedule().getSchedule();
				for (int i = 0; i < schedule.getWaypointSize() - 1; i++) {
					Waypoint w1 = schedule.getWaypoint(i);
					Waypoint w2 = schedule.getWaypoint(i + 1);

					g.setColor(new Color(0, 255, 0, DEFAULT_TRANSPARENCY));
					g.drawLine(
							(int) (w1.getX() * tileSize) + tileX, 
							(int) (w1.getY() * tileSize) + tileY, 
							(int) (w2.getX() * tileSize) + tileX, 
							(int) (w2.getY() * tileSize) + tileY
					);
					
					if (i > 0) {
						g.setColor(new Color(0, 255, 0));
						g.drawLine(
								(int) (w1.getX() * tileSize) + tileX, 
								(int) (w1.getY() * tileSize) + tileY,
								(int) (w1.getX() * tileSize) + tileX, 
								(int) (w1.getY() * tileSize) + tileY
						);
					}
				}
				
				// Draw the stations of the focus bus
				g.setColor(new Color(0, 255, 0));
				g.setStroke(new BasicStroke(GraphicsFX.highDPI((int) (0.12 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
				for (int i = 0; i < schedule.getStations().size(); i++) {
					Waypoint st = schedule.getStation(i);
					g.setColor(new Color(0, 255, 0));
					
					g.drawLine(
								(int) (st.getX() * tileSize) + tileX, 
								(int) (st.getY() * tileSize) + tileY,
								(int) (st.getX() * tileSize) + tileX, 
								(int) (st.getY() * tileSize) + tileY
					);
				}
			}
			
			g.setStroke(new BasicStroke(GraphicsFX.highDPI((int) (0.06 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
			
			// Draw route of focus person (draw a strait line from origin to target station)
			if (focusPerson != null) {
				g.setColor(new Color(255, 0, 0, DEFAULT_TRANSPARENCY));
			
				for (int i = 0; i < focusPerson.getRoute().getStations().size() - 1; i++) {
					Waypoint w1 = focusPerson.getRoute().getStations().get(i).getStation();
					Waypoint w2 = focusPerson.getRoute().getStations().get(i + 1).getStation();
					
					g.drawLine(
							(int) (w1.getX() * tileSize) + tileX,
							(int) (w1.getY() * tileSize) + tileY,
							(int) (w2.getX() * tileSize) + tileX,
							(int) (w2.getY() * tileSize) + tileY
					);
				}
			}
		}
		
		// Draw busses
		for (Bus b : town.getBusses()) {
			g.setColor(new Color(100, 100, 100));
			if (b == focusBus) g.setColor(Color.green);
			g.fillRect(
					(int) (b.getX() * tileSize - tileSize * BUS_DRAW_SIZE / 2) + tileX,
					(int) (b.getY() * tileSize - tileSize * BUS_DRAW_SIZE / 2) + tileY, 
					(int) (tileSize * BUS_DRAW_SIZE), (int) (tileSize * BUS_DRAW_SIZE) );

			// Draw the persons inside the bus
			drawPersons(g, b.getX() - 0.1, b.getY() - 0.1, b.getPersons().size());
		}
		
		// Draw lines to the internal frames ("relation lines")
		if (showRelationLinesButton.isSelected()) {
			g.setStroke(new BasicStroke(GraphicsFX.highDPI((int) (0.01 * tileSize)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0));
			
			for (JInternalFrame iFrame : getAllFrames()) {
				if (iFrame instanceof StreetTileInfoFrame) { // STREET TILE INFO
					StreetTileInfoFrame frame = (StreetTileInfoFrame) iFrame;
					frame.updateInfo(); // Update the person and bus schedule list
					
					g.setColor(Color.white);
					
					int sx = (int) ((frame.getTile().getX() + 0.5) * tileSize) + tileX;
					int sy = (int) ((frame.getTile().getY() + 0.5) * tileSize) + tileY;
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof HouseTileInfoFrame) { // HOUSE TILE INFO
					HouseTileInfoFrame frame = (HouseTileInfoFrame) iFrame;
					
					g.setColor(Color.white);
					
					int sx = (int) ((frame.getTile().getX() + 0.5) * tileSize) + tileX;
					int sy = (int) ((frame.getTile().getY() + 0.5) * tileSize) + tileY;
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof BusInfoFrame) { // BUS INFO
					BusInfoFrame frame = (BusInfoFrame) iFrame;
					frame.updateInfo(); // Update the bus info
	
					g.setColor(Color.white);
					if (frame.getBus() == focusBus) g.setColor(Color.green);
					
					int sx = (int) (frame.getBus().getX() * tileSize) + tileX;
					int sy = (int) (frame.getBus().getY() * tileSize) + tileY;
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
				
				if (iFrame instanceof PersonInfoFrame) { // PERSON INFO
					PersonInfoFrame frame = (PersonInfoFrame) iFrame;
	
					g.setColor(Color.white);
					if (frame.getPerson() == focusPerson) g.setColor(Color.red);
					
					int sx = (int) (frame.getPerson().getX() * tileSize) + tileX;
					int sy = (int) (frame.getPerson().getY() * tileSize) + tileY;
					int ex = frame.getX() + frame.getWidth() / 2;
					int ey = frame.getY() + frame.getHeight() / 2;
					
					g.drawLine(sx, sy, ex, ey);
				}
			}
		}
		
		// Repaint the person and info console
		frameLauncherContext.getInfoConsolePane().repaint();
		frameLauncherContext.getPersonConsolePane().repaint();
	}
	
	private void drawPersons(Graphics2D g, double x, double y, int numPersons) {
		if (numPersons <= 0) return;
		
		g.setColor(new Color(255, 200, 0));
		g.fillRect(
				(int) (x * tileSize - tileSize * PERSON_DRAW_SIZE / 2) + tileX,
				(int) (y * tileSize - tileSize * PERSON_DRAW_SIZE / 2) + tileY, 
				(int) (tileSize * PERSON_DRAW_SIZE), (int) (tileSize * PERSON_DRAW_SIZE) );
	
		g.setColor(Color.black);
		g.drawString("x" + numPersons, (int) ((x + PERSON_DRAW_SIZE / 2 + 0.05) * tileSize) + tileX, (int) ((y + 0.04) * tileSize) + tileY);
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

	public void createBusInfoFrame(Bus bus, int dx, int dy) {
		if (containsInfoFrame(bus)) return;
		
		// Create a bus info frame, add a person list selection listener
		BusInfoFrame frame = new BusInfoFrame(this, bus, dx, dy);
		frame.addStationListSelectionListener(this);
		frame.addPersonListSelectionListener(this);
		frame.setVisible(true);
		
		updateInternalFramePositions();
	}
	
	public void createPersonInfoFrame(Person person, int dx, int dy) {
		if (containsInfoFrame(person)) return;
		
		// Create a person info frame
		PersonInfoFrame frame = new PersonInfoFrame(this, person, dx, dy);
		frame.setVisible(true);
		
		updateInternalFramePositions();
	}
	
	public void createStreetTileInfoFrame(StreetTile tile, int dx, int dy) {
		if (containsInfoFrame(tile)) return;
		
		// Create a tile info frame
		StreetTileInfoFrame frame = new StreetTileInfoFrame(this, tile, dx, dy);
		frame.addNextStationActionListener(this);
		frame.addListSelectionListener(this);
		frame.setVisible(true);
		
		updateInternalFramePositions();
	}
	
	public void createHouseTileInfoFrame(HouseTile tile, int dx, int dy) {
		if (containsInfoFrame(tile)) return;
		
		// Create a tile info frame
		HouseTileInfoFrame frame = new HouseTileInfoFrame(this, tile, dx, dy);
		frame.addNextStationActionListener(this);
		frame.setVisible(true);
		
		updateInternalFramePositions();
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

	private void updateInternalFramePositions() {
		// (there may be a better way, but this one works quite nicely)
		for (JInternalFrame frame : getAllFrames())
			getDesktopManager().setBoundsForFrame(frame, frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());
	}
	
	private void updateTileTransform() {
		if (town == null) {
			repaint();
			return;
		}
		
		// Update tile size by screen dimensions
		if (getWidth() / town.getSizeX() > getHeight() / town.getSizeY()) {
			tileSize = getHeight() / (double) town.getSizeY();
			tileX = (int) ((getWidth() - town.getSizeX() * tileSize) / 2);
			tileY = 0;
		} else {
			tileSize = getWidth() / (double) town.getSizeX();
			tileX = 0;
			tileY = (int) ((getHeight() - town.getSizeY() * tileSize) / 2);
		}
		
		// Apply zoom and translation
		tileSize *= transZ;
		tileX += transX;
		tileY += transY;
		
		repaint();
	}

	public Town getTown() {
		return town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	public void mouseClicked(MouseEvent e) {
		if (town == null) return;
		
		if (e.getButton() == MouseEvent.BUTTON1) {
			// CLICK EVENT ON BUS
			Iterator<Bus> busIt = town.getBusses().iterator();
			while (busIt.hasNext()) {
				Bus bus = busIt.next();
				
				int busMidX = (int) (bus.getX() * tileSize) + tileX;
				int busMidY = (int) (bus.getY() * tileSize) + tileY;
				int busSize = (int) (tileSize * BUS_DRAW_SIZE);
	
				// Check if user clicked on bus
				if (new Rectangle(busMidX - busSize / 2, busMidY - busSize / 2, busSize, busSize).contains(e.getPoint())) {
					createBusInfoFrame(bus, busMidX + GraphicsFX.highDPI(FRAME_LAYER_SPACE), busMidY + GraphicsFX.highDPI(FRAME_LAYER_SPACE));
					return; // Return to avoid doubled mouse input events
				}
			}
			
			// CLICK EVENT ON TILE
			int tx = (int) ((e.getX() - tileX) / tileSize);
			int ty = (int) ((e.getY() - tileY) / tileSize);
			if (tx >= 0 && tx < town.getSizeX() && ty >= 0 && ty < town.getSizeY()) {
				Tile tile = town.getTiles()[tx][ty];
				if (tile instanceof StreetTile)
					createStreetTileInfoFrame(
							(StreetTile) tile,
							e.getX() + GraphicsFX.highDPI(FRAME_LAYER_SPACE),
							e.getY() + GraphicsFX.highDPI(FRAME_LAYER_SPACE)
					);
				if (tile instanceof HouseTile)
					createHouseTileInfoFrame(
							(HouseTile) tile,
							e.getX() + GraphicsFX.highDPI(FRAME_LAYER_SPACE),
							e.getY() + GraphicsFX.highDPI(FRAME_LAYER_SPACE)
					);
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		mouseDX = 0;
		mouseDY = 0;
		mouseXFlag = e.getX();
		mouseYFlag = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {
		mouseDX = e.getX() - mouseXFlag;
		mouseDY = e.getY() - mouseYFlag;
		mouseXFlag = e.getX();
		mouseYFlag = e.getY();
		
		if (SwingUtilities.isMiddleMouseButton(e)) {
			// Translate
			transX += mouseDX;
			transY += mouseDY;
			updateTileTransform();
		}
	}

	public void mouseMoved(MouseEvent e) {
		mouseDX = e.getX() - mouseXFlag;
		mouseDY = e.getY() - mouseYFlag;
		mouseXFlag = e.getX();
		mouseYFlag = e.getY();
		
		// nothing to do here
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (town == null) return;
		
		double zoomD = Math.pow(ZOOM_SPEED, -e.getWheelRotation());
		transZ *= zoomD;
		transX -= town.getSizeX() * tileSize * transZ * (zoomD - 1) / 2;
		transY -= town.getSizeX() * tileSize * transZ * (zoomD - 1) / 2;
		
		updateTileTransform();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() instanceof Component) {
			// Get the root internal frame for the current jlist
			Component rootFrame = SwingUtilities.getAncestorOfClass(JInternalFrame.class, (Component) e.getSource());
			
			// This will be called if something in a bus info frame was selected
			if (rootFrame instanceof BusInfoFrame) {
				BusInfoFrame source = (BusInfoFrame) rootFrame;
				
				// It was a station selected!
				if (e.getSource() instanceof StationList) {
					Waypoint station = source.getSelectedStation();
					if (station != null) {
						int sx = (int) (station.getX() - 0.5);
						int sy = (int) (station.getY() - 0.5);
						createStreetTileInfoFrame((StreetTile) town.getTiles()[sx][sy], source.getX() + FRAME_LAYER_SPACE, source.getY() + FRAME_LAYER_SPACE);
					}
				}
				
				// It was a person selected!
				if (e.getSource() instanceof PersonList) {
					Person person = source.getSelectedPerson();
					if (person != null) {
						// We now have a person, create an info frame
						createPersonInfoFrame(person, source.getX() + FRAME_LAYER_SPACE, source.getY() + FRAME_LAYER_SPACE);
					}
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
		else if (e.getSource() == showNonCoveredTiles) repaint();
		
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

	public void componentResized(ComponentEvent e) {
		// When this desktop pane resizes, we have to keep all internal frames inside the bounds and recalculate the map size
		updateInternalFramePositions();
		updateTileTransform();
	}

	public void componentMoved(ComponentEvent e) {
		
	}

	public void componentShown(ComponentEvent e) {
		
	}

	public void componentHidden(ComponentEvent e) {
		
	}
}
