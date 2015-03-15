/*
 * SelectionModel - 
 * The control that shows the content of a folder
 * FileTree.java - This class shows a directory tree
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
import java.util.*;
import java.io.File;

/**
 * Maneja la selección de fileViews.
 */
class SelectionModel{

	/**
	 * Estructura interna donde se guardan los ficehros que están seleccionados
	 */
	private Vector fileViews = new Vector();

	/**
	 * Añade una vista de fichero a la selección.
	 * @param fileView vista de fichero que se añade a la selección. El fichero cambia de color en la pantalla.
	 */
	public void add(FileView fileView){
		fileViews.add(fileView);
		fileView.setSelected(true);
	}

	/**
	 * Borrar una vista de fichero de la selección.
	 * Remove a fileView from the selection.
	 * @param fileView vista de fichero que se quiere borrar de la selección. El fichero cambia de color en la pantalla.
	 * @param fileView fileView to remove from the selection.
	 */
	public void remove(FileView fileView){
		fileView.setSelected(false);
		fileViews.remove(fileView);
	}

	/**
	 * Añade una lista de vistas de ficheros a la selección.
	 * @param fileViews lista de vistas de ficheros que se añadirán a la selección. Los ficheros cambiarán de color.
	 */
	public void add(FileView[] fileViews){
		for(int i = 0; i<fileViews.length; i++){
			this.fileViews.add(fileViews[i]);
			fileViews[i].setSelected(true);
		}
	}

	/**
	 * Borra la selección
	 */	
	public void clear(){
		for (Enumeration e = fileViews.elements() ; e.hasMoreElements() ;) {
			 FileView fileView = (FileView)e.nextElement();
			 fileView.setSelected(false);
		 }
		fileViews.clear();
	}

	/**
	 * Indica si el fichero dado está en la selección
	 * @param fileView vista de ficharo a identificar si se encuentra dentro de la selección.
	 * @return true si el vista de fichero está en la selección.
	 */
	public boolean isSelected(FileView fileView){
		return fileViews.contains(fileView);
	}

	/**
	 * Return the number of list nodes added to the selection
	 * @version 1.0.1
	 */
	public int getSize(){
		return fileViews.size();
	}

	/**
	 * Devuelve un array con todos los fileViews de la selección
	 * @return un array con todos los fileViews de la selección
	 */
	public Object[] getArray(){
		return fileViews.toArray();
	}

	/**
	 * Returns the List of files. It's used by the FileListSelection
	 * @return a list
	 * @see org.jos.jexplorer.FileListExplorer
	 */
	public List getFileList(){
		return fileViews;
	}

	/**
	 * Devuelve la lista de ficheros seleccionados.
	 * @return la lista de ficheros seleccionados, null en caso de que no haya 
	 * @return ficheros seleccionados.
	 */
	public File[] getFileArray(){
		File[] files = new File[fileViews.size()];
		int i = 0;
		for(Enumeration e = fileViews.elements(); e.hasMoreElements();){
			FileView fileView = (FileView)e.nextElement();
			files[i] = fileView.getFile();
			i = i + 1;
		}
		if (files.length>0){
			return files;
		} else {
			return null;
		}
	}

	/**
	 * Return the las file added to the list
	 * @return El último fichero añadido a al lista o null en caso de que no haya ningún fichero seleccionado.
	 */
	public File getSelectedFile(){
		if (fileViews.size()>0){
			FileView fileView = (FileView)fileViews.lastElement();
			return fileView.getFile();
		} else {
			return null;
		}
	}

	/**
	 * Returns the list of LisNodes selected
	 * @return a list of ListNodes
	 */
	public ListNode[] getListNodeArray(){
		if (fileViews.size()>0){
			ListNode[] listNodes = new ListNode[fileViews.size()];
			int i = 0;
			for(Enumeration e = fileViews.elements(); e.hasMoreElements();){
				FileView fileView = (FileView)e.nextElement();
				listNodes[i] = fileView.getListNode();
				i = i + 1;
			}
			return listNodes;
		} else {
			return null;
		}
	}

	/**
	 * Returns the last listNode added to the selection list.
	 * @return the selected Listnode
	 */
	public ListNode getSelectedListNode(){
		if (fileViews.size()>0){
			FileView fileView = (FileView)fileViews.lastElement();
			return fileView.getListNode();
		} else {
			return null;
		}		
	}
	
	/**
	 * Returns the last listNode added to the selection list.
	 * @return the selected fileView
	 */
	public FileView getSelectedFileView(){
		if (fileViews.size()>0){
			return (FileView)fileViews.lastElement();
		} else {
			return null;
		}		
	}
}
