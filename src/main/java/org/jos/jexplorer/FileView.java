/*
 * FileView.java - 
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

import java.io.File;

/**
 * This Interface must be implemented by all file views
 * @see BriefFileView
 * @see IconFileView
 */
interface FileView {

	/**
	 * Return the ListNode represented by this view.
	 * @return the listNode
 	 * @version 1.0.1
	 */
	public ListNode getListNode();

	/**
	 * Return the file represented by this view.
	 * @return the listNode
 	 */
	public File getFile();
	
	/**
	 * Set if this view is selected  or not.
	 * @param selected if it's true the file view will be selected
	 */	
	public void setSelected(boolean selected);
}
