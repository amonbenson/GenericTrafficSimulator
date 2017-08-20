package com.trafficsim.graphics;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class InfoFrame extends JInternalFrame implements AncestorListener, InternalFrameListener {

	public static final int WIDTH = 150;
	public static final int MIN_HEIGHT = 200;
	
	private TownDesktopPane rootDesktop;
	
	public InfoFrame(TownDesktopPane rootDesktop, String title, int dx, int dy) {
		// Set the title
		super(title);
		
		// Set the root desktop to use in the ancestor listeners
		this.rootDesktop = rootDesktop;
		
		// Add an ancestor listener to detect if the user moves the window arround
		addAncestorListener(this);
		addInternalFrameListener(this);
		
		// Tweak some settings
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setLocation(dx, dy);
		setSize(FrameLauncher.highDPI(WIDTH), FrameLauncher.highDPI(MIN_HEIGHT));
		setMinimumSize(getSize());
		setClosable(true);
		setMaximizable(false);
		setIconifiable(false);
		setResizable(true);

		// Copy the input and aciton maps to keep all key bindings from the desktop
		getRootPane().setInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, rootDesktop.getInputMap());
		getRootPane().setActionMap(rootDesktop.getActionMap());

		// Add the this frame to the desktop
		rootDesktop.add(this);
	}
	
	public TownDesktopPane getRootTownDesktopPane() {
		return rootDesktop;
	}
	
	public void packFrame() {
		pack();
		setSize(FrameLauncher.highDPI(WIDTH), Math.max(getHeight(), FrameLauncher.highDPI(MIN_HEIGHT)));
	}

	public void ancestorAdded(AncestorEvent event) {
	}

	public void ancestorRemoved(AncestorEvent event) {
	}

	public void ancestorMoved(AncestorEvent event) {
		rootDesktop.updateFocusElement();
		rootDesktop.repaint();
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		rootDesktop.updateFocusElement();
		rootDesktop.repaint();
	}

	public void internalFrameClosing(InternalFrameEvent e) {
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		rootDesktop.updateFocusElement();
		rootDesktop.repaint();
	}

	public void internalFrameIconified(InternalFrameEvent e) {
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		rootDesktop.updateFocusElement();
		rootDesktop.repaint();
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		rootDesktop.updateFocusElement();
		rootDesktop.repaint();
	}
}
