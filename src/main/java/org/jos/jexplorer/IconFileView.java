/*
 * IconFileView - 
 * Copyright (C) 2000 Iñigo González
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

import java.awt.*;
import javax.swing.*;
import java.io.File;

/**
 * This class shows a file like a big icon.
 */
class IconFileView extends JLabel implements FileView {

	/**
	 * El fichero representado por esta clase.
	 * The file represented by this class.
 	 * @version 1.0.1
	 */
	private ListNode fileNode;
	
	/**
	 * Construye un IconFileView para el fichero especificado.
	 * Construct a IconFileView for the specified file.
	 * @param file fichero del cual se va a crear una vista.
	 * @param file file to create the view.
	 * @version 1.0.1
	 */
	public IconFileView(ListNode fileNode){
		super();
		this.fileNode = fileNode;
		
		setText(fileNode.getFileName());
		setIcon((ImageIcon)fileNode.getBigIcon());
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}

	/**
	 * Devuelve el fichero representado por esta vista.
	 * Return the file represented by this view.
	 * @return El fichero representado.
	 * @return The file represented.
	 * @version 1.0.1
	 */
	public File getFile(){
		return (File)fileNode.getObject();
	}

	/**
	 * Return the ListNode represented by this view.
	 * @return the listNode
 	 * @version 1.0.1
	 */
	public ListNode getListNode(){
		return fileNode;
	}

	/**
	 * Set the file view selected or not.
	 * @param selected true if the file view must be selected.
	 */
	public void setSelected(boolean selected){
		if (selected){
			setOpaque(true);
			setBackground(Color.orange);
		} else {
			setOpaque(false);
		}
		repaint();
	}

}
