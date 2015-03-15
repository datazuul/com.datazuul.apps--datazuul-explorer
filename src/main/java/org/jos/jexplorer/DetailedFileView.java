/*
 * DetailedFileView - Implements the jexplorer's detailed view
 * The control that shows the content of a folder
 * FileTree.java - This class shows a directory tree
 * Copyright (C) 2000 Iñigo González
 * inigomail@olemail.com
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
import java.util.Date;

/**
 * Super class to manage the view of the files. This implementation represents a list with
 * the small icon and with the name of the file at the right of the icon.
 * @author Inigo Gonzalez
 * @version 1.0.1
 */
class DetailedFileView extends JComponent implements FileView{
	
	/**
	 * The file represented by this class.
	 */
	private ListNode fileNode;

	private int hgap;

	private String title;
	private String size;
	private String fileDate;
	private ImageIcon icon;

	/**
	 * Identifys if the file is selected in the file list.
	 */
	private boolean isSelected = false;


	/**
	 * Constructs a FileView for the specified file.
	 * @param file file to create the view.
	 */
	public DetailedFileView(ListNode fileNode){
		this(fileNode, 5);
	}

	/**
	 * Constructs a FileView for the specified file.
	 * @param file file to create the view.
	 * @param hgap horizontal gap.
	 */
	public DetailedFileView(ListNode fileNode, int hgap){

		this.fileNode = fileNode;
		this.hgap = hgap;
		title = fileNode.getFileName();
		long bytes = fileNode.length();
		if (bytes > 1024){
			size = bytes / 1024 + "Kb";
		} else {
			size = bytes + "b";
		}
		Date date = new Date(fileNode.lastModified());
		fileDate = date.toString();
		icon = ((ImageIcon)fileNode.getSmallIcon());
	}

	/**
	 * Returns the ListNode represented by this view.
	 * @return the listNode
 	 * @version 1.0.1
	 */
	public ListNode getListNode(){
		return fileNode;
	}

	/**
	 * Return the file represented by this view.
	 * @return the file
 	 * @version 1.0.1
	 */
	public File getFile(){
		return (File)fileNode.getObject();
	}
	
	/**
	 * Set the file view selected or not.
	 * @param selected true if the file view must be selected.
	 */
	public void setSelected(boolean selected){
		isSelected = selected;
		update(getGraphics());
	}

	public Dimension getPreferredSize(){
		Graphics g = getGraphics();
		FontMetrics fm = g.getFontMetrics();
		return new Dimension(100, fm.getMaxDescent() + fm.getMaxAscent());
	}

	/**
	 * Paints the representation of this file.
	 */
	public void paint(Graphics g){
		if (g != null){
			//in future versions i have to change this
			int iconWidth = icon.getIconWidth();
			int nameWidth = (getWidth()-iconWidth) / 2;
			int sizeWidth = nameWidth / 2;
			int dateWidth = sizeWidth;
			int selWidth = iconWidth + hgap + nameWidth;

			FontMetrics fm = g.getFontMetrics();
			int height = fm.getMaxDescent() + fm.getMaxAscent();

			Color old = g.getColor();
			if (isSelected){
				g.setColor(Color.orange);
			} else {
				g.setColor(Color.white);
			}
			g.fillRect(0, 0, selWidth, height);
			icon.paintIcon(this, g, 0, 0);

			g.setColor(old);
			g.drawString(title, iconWidth + hgap, fm.getMaxAscent());

			g.setColor(Color.white);
			g.fillRect(selWidth + hgap, 0, sizeWidth, height);
			g.setColor(old);
			g.drawString(size, selWidth + hgap, fm.getMaxAscent());

			g.setColor(Color.white);
			g.fillRect(selWidth + hgap + sizeWidth + hgap, 0, sizeWidth, height);
			g.setColor(old);
			g.drawString(fileDate, selWidth + hgap + sizeWidth + hgap, fm.getMaxAscent());
		}
	}

	public void update(Graphics g){
		paint(g);
	}
}
