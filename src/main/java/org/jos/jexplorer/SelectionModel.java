/*
 * SelectionModel - 
 * The control that shows the content of a folder
 * FileTree.java - This class shows a directory tree
 * Copyright (C) 2000 I�igo Gonz�lez
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
 * Maneja la selecci�n de fileViews.
 */
class SelectionModel{

	/**
	 * Estructura interna donde se guardan los ficehros que est�n seleccionados
	 */
	private Vector fileViews = new Vector();

	/**
	 * A�ade una vista de fichero a la selecci�n.
	 * @param fileView vista de fichero que se a�ade a la selecci�n. El fichero cambia de color en la pantalla.
	 */
	public void add(FileView fileView){
		fileViews.add(fileView);
		fileView.setSelected(true);
	}

	/**
	 * Borrar una vista de fichero de la selecci�n.
	 * Remove a fileView from the selection.
	 * @param fileView vista de fichero que se quiere borrar de la selecci�n. El fichero cambia de color en la pantalla.
	 * @param fileView fileView to remove from the selection.
	 */
	public void remove(FileView fileView){
		fileView.setSelected(false);
		fileViews.remove(fileView);
	}

	/**
	 * A�ade una lista de vistas de ficheros a la selecci�n.
	 * @param fileViews lista de vistas de ficheros que se a�adir�n a la selecci�n. Los ficheros cambiar�n de color.
	 */
	public void add(FileView[] fileViews){
		for(int i = 0; i<fileViews.length; i++){
			this.fileViews.add(fileViews[i]);
			fileViews[i].setSelected(true);
		}
	}

	/**
	 * Borra la selecci�n
	 */	
	public void clear(){
		for (Enumeration e = fileViews.elements() ; e.hasMoreElements() ;) {
			 FileView fileView = (FileView)e.nextElement();
			 fileView.setSelected(false);
		 }
		fileViews.clear();
	}

	/**
	 * Indica si el fichero dado est� en la selecci�n
	 * @param fileView vista de ficharo a identificar si se encuentra dentro de la selecci�n.
	 * @return true si el vista de fichero est� en la selecci�n.
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
	 * Devuelve un array con todos los fileViews de la selecci�n
	 * @return un array con todos los fileViews de la selecci�n
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
	 * @return El �ltimo fichero a�adido a al lista o null en caso de que no haya ning�n fichero seleccionado.
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
