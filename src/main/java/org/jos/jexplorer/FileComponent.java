/**
 * FileComponent.java - 
 * Copyright (C) 2000 Iñigo González Rodríguez
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
import java.util.*;
import java.io.File;

/**
 * Esta clase es la clase basa para todos los componentes del Jexplorer. Es decir, tanto
 * <i>LocationBar</i> como <i>FileList</i> como <i>FileTree</i> extenderán esta clase.
 * FileComponent is the base class for jexplorer file components. <i>LocationBar</i>, <i>
 * FileList</i>, <i>FolderList</i> extend this class.
 * @see LocationBar
 * @see FileList
 * @see FileTree
 */
public class FileComponent extends JPanel{ //JComponent{

	/**
	 * Indica si el evento changefolder debe ser invocado.
	 */
	public boolean activateListener = true;

	/**
	 * Lista de <i>Listeners</i> que recibirán los eventos que se produzcan en el FileTree.
	 * @see FileListener
	 */
	private Vector fileListeners = new Vector();
	
	/**
	 * Tell to listeners that the selected folder has changed
	 * @param folder the new folder
	 * @see FileListener
	 */
	protected void fireChangeListener(File folder){
		if (activateListener){
			for (Enumeration e = fileListeners.elements() ; e.hasMoreElements() ;){
				FileListener fileListener = (FileListener)e.nextElement();
				fileListener.folderChange(folder);
			}
		}
	}

	/**
	 * Avisa a los <i>listeners</i> de que la carpeta selecionada ha cambiado.
	 * @param folder la carpeta a la que se ha cambiado.
	 * @see FileListener
	 */
	protected void fireContentChangeListener(File folder){
		if (activateListener){
			for (Enumeration e = fileListeners.elements() ; e.hasMoreElements() ;){
				FileListener fileListener = (FileListener)e.nextElement();
				fileListener.folderContentChange(folder);
			}
		}
	}

	/**
	 * Avisa a los <i>listeners</i> de que se ha seleccionado un fichero.
	 * @param file el fichero seleccionado
	 * @see FileListener
	 * @version 1.0.1
	 */
	protected void fireFileSelectedListener(ListNode listNode){
		if (activateListener){
			for (Enumeration e = fileListeners.elements() ; e.hasMoreElements() ;){
				FileListener fileListener = (FileListener)e.nextElement();
				fileListener.fileSelected(listNode);
			}
		}
	}


	/**
	 * Calls to the <i>listeners</i> endLoadingProcess function.
	 * @see FileListener
	 * @version 1.1
	 */
	protected void fireEndLoadingProcess(){
		if (activateListener){
			for (Enumeration e = fileListeners.elements() ; e.hasMoreElements() ;){
				FileListener fileListener = (FileListener)e.nextElement();
				fileListener.endLoadingProcess();
			}
		}
	}

	/**
	 * Avisa a los <i>listeners</i> de que se ha seleccionado un fichero.
	 * @param file el fichero seleccionado
	 * @see FileListener
	 * @version 1.0.1
	 */
	protected void fireDoubleClickFileSelectedListener(ListNode listNode){
		if (activateListener){
			for (Enumeration e = fileListeners.elements() ; e.hasMoreElements() ;){
				FileListener fileListener = (FileListener)e.nextElement();
				fileListener.fileDoubleClickSelected(listNode);
			}
		}
	}

	/**
	 * Añade un nuevo <i>Listener<i> a la lista.
	 * @param fileListener Este <i>Listener<i> recibirá los eventos del control FileTree.
	 * @see FileListener
	 */
	public void addFileListener(FileListener fileListener){
		fileListeners.add(fileListener);
	}

	/**
	 * Borrar el <i>Listener<i> especificado de la lista.
	 * @param fileListener El <i>Listener<i> a borrar.
	 * @see FileListener
	 */
	public void removeFileListener(FileListener fileListener){
		fileListeners.remove(fileListener);
	}
}
