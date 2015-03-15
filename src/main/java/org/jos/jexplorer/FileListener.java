/**
 * FileListener.java - 
 * Copyright (C) 2000 Iñigo González Rodríguez
 * inigomail@olemail.com
 * http://www.provider.domain/user
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

import java.io.*;

/**
 * The listener interface for receiving file events. The class that is interested 
 * in processing a file event implements this interface, and the object created with
 * that class is registered with a component, using the component's addFileListener
 * method. When the file event occurs, that object's folderChange method is invoked.
 * @see FileComponent
 * @see ListNode
 */

public interface FileListener{
	/**
	 * Invoked when the control jumps to another folder.
	 * @param folder the new folder that is being showed
	 */
	public void folderChange(File folder);
	
	/**
	 * Invoked when a folder changes its content.
	 * @param folder the folder that is being modified
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder);

	/**
	 * Invoked when a file or a folder is selected.
	 * @param listNode the list node that has been selected
	 * @version 1.0.1
	 */
	public void fileSelected(ListNode listNode);

	/**
	 * Invoked when a fileComponent ends its loading process.
	 * @version 1.1
	 */
	public void endLoadingProcess();

	/**
	 * Invoked when a file (not a folder) is selected with double - Click.
	 * @param listNode the list node that has been selected
	 * @version 1.0.1
	 */
	public void fileDoubleClickSelected(ListNode listNode);

}
