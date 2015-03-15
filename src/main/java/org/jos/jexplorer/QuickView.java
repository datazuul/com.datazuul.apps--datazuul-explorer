/*
 * QuickView.java - Interface for all views
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

import java.awt.Component;
import javax.swing.JFrame;
import java.io.File;

/**
 * This interface must be implemented by all plugins that want to be a jexplorer's quickView
 * A quickView can be a full featured program, not only a quick view.
 * @version 1.0.1
 */
public interface QuickView{

	/**
	 * Returns the view name.
	 */
	public String getName();

	/**
	 * Returns the internal view name.
	 * This name is the same of the QuickView Plugin main class
	 */
	//public String getInternalName();

	/**
	 * Returns the quickview gui where to show the file types.
	 */
	public Component getQuickView();

	/**
	 * Shows the file in the view.
	 * @param listNode the node of the FileList (file oz zipEntry) to show or to edit in the plugin
	 */
	public void showFile(ListNode listNode);

	/**
	 * This function is called when JExplorer ends.
	 * @param file the file to show or to edit in the plugin
	 */
	public void terminate();

	/**
	 * Returns the dialog to display when the Jexplorer's menu request for the plugin about window
	 * @param parent the JExplorer's window
	 * @return the 'about as' dialog
	 */
	public void showAboutWindow(JFrame parent);

}
