/*
 * ListNode.java - Interface impelmented by FileList's components
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

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * This interface must be implemented by all the classes that want to be <i>FileList</i>'s components.
 * @see ZipListNode
 * @see FileListNode
 * @version 1.0.1
 */
public interface ListNode{
	/**
	 * Returns the name of the object represented by this class.
	 * @return The name of the object.
	 */
	public String getFileName();

	/**
	 * Returns the full name of the object represented by this class.
	 * @return The full name of the object.
	 * @version 1.1
	 */
	public String getFullName();

	/**
	 * Returns the type name of the object represented by this class.
	 * @return The type name of the object.
	 */
	public String getTypeName();

	/**
	 * Returns the viewer name of the object represented by this class.
	 * @return The viewer name of the object.
	 */
	public String getViewerName();

	/**
	 * Returns the big icon that represents an object.
	 */
	public Object getBigIcon();

	/**
	 * Returns the small icon that represents an object.
	 */
	public Object getSmallIcon();

	/**
	 * Returns the path of the jar file to execute an application
	 */
	public String getUrl();

	/**
	 * Returns the url associated to the file
	 */
	public URL getURL();

	/**
	 * Returns the class name to execute in the jar file
	 */
	public String getClassName();

	/**
	 * Returns the object represented by this class, usually a file.
	 */
	public Object getObject();

	/**
	 * Returns the fileNode's children.
	 */
	public ListNode[] getChildren();

	/**
	 * Returns the fileNode's children.
	 * @filter the filter to apply
	 */
	public ListNode[] getChildren(String filter);

	/**
	 * Returns true if the object represents a directory
	 */
	public boolean isDirectory();

	/**
	 * Returns the length or the size of this object
	 */
	public long length();

	/**
	 * Returns the date of this object
	 */
	public long lastModified();
	
	/**
	 * Compares two listNodes lexicographically
	 * @see java.lang.String
	 */
	public int compareTo(ListNode listNode);

	/**
	 * Creates the popup menu for this list node
	 * @return the popup menu
	 */
	public JPopupMenu getSinglePopupMenu(FileList fileList);

	/**
	 * Creates the popup menu for a list of listNodes
	 * @param listNodes the list that will be modified by this popup menu
 	 * @return the popup menu
	 */
	public JPopupMenu getMultiPopupMenu(FileList fileList, ListNode[] listNodes);

	/**
	 * Returns the input stream to read this file
	 * @return the input stream to read the content of this file o zipEntry.
	 */
	public InputStream getInputStream();
}
