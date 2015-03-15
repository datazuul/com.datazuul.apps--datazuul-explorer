package com.datazuul.apps.jexplorer.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FileMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	public FileMenu() {
		super();
		
		setText("Datei");
		setMnemonic(KeyEvent.VK_D);
		
		// Menu items
		JMenuItem menuItem;
		
		menuItem = new JMenuItem();
		menuItem.setText("Neu");
		add(menuItem);
		
		addSeparator();
		
		menuItem = new JMenuItem();
		menuItem.setText("Schließen");
		add(menuItem);
	}
}
