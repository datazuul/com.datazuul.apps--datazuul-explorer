/**
 * StatusBar.java - 
 * Copyright (C) 2001 Iñigo González
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
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

class StatusBar extends JPanel {
	private JLabel lblFileCount = new JLabel("Status Bar 1.0");
	private JLabel lblFilter = new JLabel("");

	public StatusBar(){
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		lblFileCount.setBorder(bb);
		lblFilter.setBorder(bb);
		setFilter("");
		add(lblFileCount);
		add(lblFilter);
	}

	public void setLength(int length){
		if (length > 0) {
			lblFileCount.setText(length + " " + I18n.getString("FILES"));
		}
	}

	public void setFilter(String filter){
		if (filter.length()>0) {
			lblFilter.setText(I18n.getString("FILTER") + " " + filter);
		} else {
			lblFilter.setText(I18n.getString("NO_FILTER"));
		}
	}
}