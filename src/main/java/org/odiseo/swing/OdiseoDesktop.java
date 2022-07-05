/*
 * OdiseoDesktop.java - Odiseo's virtual desktop
 * Copyright (C) 2000-2001 Inigo Gonzalez
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

package org.odiseo.swing;

import java.awt.*;
import javax.swing.*;

import org.odiseo.core.*;

/**
 * Implements the Odiseo desktop window
 */
public class OdiseoDesktop extends JDesktopPane{
	private JWindow w;
	//private static int id = 0; to support several virtual desktops

	/**
	 * Creates an Odiseo desktop window.
	 * This may be before the security manager be installed.
	 */
	public OdiseoDesktop(){
		w = new JWindow();
		w.getContentPane().setLayout(new BorderLayout());
		w.getContentPane().add(this, BorderLayout.CENTER);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		w.setBounds(0, 0, screen.width, screen.height);
		w.setVisible(true);
		System.out.println("Odiseo Desktop window is running");
	}

	/**
	 * Returns the container of this window
	 */
	public Container getContainer(){
		return w.getContentPane();
	}
}