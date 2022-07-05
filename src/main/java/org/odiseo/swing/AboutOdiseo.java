/*
 * AboutOdiseo.java - Odiseo's desktop about window
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

/**
 * Implements the Odiseo desktop about window
 */
public class AboutOdiseo extends JDialog{

	/**
	 * Creates the Odiseo about window.
	 * This may be before the security manager be installed.
	 */
	public AboutOdiseo(){
		super();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JLabel(getImageIcon("images/about.gif")), BorderLayout.CENTER);
		setResizable(false);
		setModal(true);
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		jp.add(new JButton(new ExitAction()));
		getContentPane().add(jp, BorderLayout.SOUTH);
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		setLocation(new Point((screen.width - frameSize.width)/ 2, (screen.height - frameSize.height) /2));
		setVisible(true);
	}

	private ImageIcon getImageIcon(String path){
		URL url = getClass().getResource(path);
		if (url != null){
			return new ImageIcon(url);
		} else {
			return null;
		}
	}

	class ExitAction extends AbstractAction{
		public ExitAction(){
			putValue(Action.NAME, "OK");
		}

		public void actionPerformed(ActionEvent e){
			AboutOdiseo.this.dispose();
		}
	}
}