/*
 * FavoritesOrganizer.java - Organizer for the JExplorer's favorites links
 * Copyright (C) 2000-2001 Iñigo González
 * sensei@hispavista.com
 * http://www.geocities.com/innigo.geo
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jos.jexplorer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

class FavoritesOrganizer extends JDialog{

	private JTextField txtTitle = new JTextField();
	private JTree tree = new JTree();

	public FavoritesOrganizer(String xmlFile){
		this(xmlFile, "");
	}

	public FavoritesOrganizer(String xmlFile, String newTitle){
		super((Frame)null, "Favorites' Organizer", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cancel();
			}
		});

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createTitle(), BorderLayout.NORTH);
		getContentPane().add(createTree(), BorderLayout.CENTER);
		getContentPane().add(createButtons(), BorderLayout.EAST);

		txtTitle.setText(newTitle);

 		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation(new Point((screen.width - frameSize.width)/ 2, (screen.height - frameSize.height) /2));
		setVisible(true);
	}

	private JPanel createTitle(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel("Agregar con título:"), BorderLayout.WEST);
		jp.add(txtTitle, BorderLayout.CENTER);
		jp.add(new JButton("Agregar"), BorderLayout.EAST);
		return jp;
	}

	private JPanel createTree(){
		JPanel jp = new JPanel(new BorderLayout());
		jp.add(new JLabel("Agregar en la Carpeta:"), BorderLayout.NORTH);
		jp.add(new JScrollPane(tree), BorderLayout.CENTER);
		return jp;
	}

	private JPanel createButtons(){
		JPanel jp = new JPanel(new BorderLayout());
		Box box = Box.createVerticalBox();
		box.add(new JButton("Nueva Carpeta..."));
		box.add(new JButton("Cambiar Nombre..."));
		box.add(new JButton("Aceptar"));
		box.add(new JButton("Cancelar"));
		jp.add(box, BorderLayout.CENTER);
		return jp;
	}

	private void cancel(){
		dispose();
	}
}