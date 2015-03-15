/*
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Pantalla de selección de carpetas.
 * Esta pantalla se utiliza en la opción de copiar y de mover de JExplorer
 * aunque puede ser utilizada por cualquier otra aplicación.
 */
public class SelectFolderWindow implements FileListener{

	private JDialog win;
	private LocationBar locationBar = new LocationBar();
	private FileTree fileTree = new FileTree();
	private File selectedFolder = null;

	/**
	 * Constructor de la pantalla.
	 * @param title El título a poner en la pantalla de búsqueda.
	 * @param sourcePath path con el que se quiere inicializar esta pantalla.
	 */
	public SelectFolderWindow(String title, File sourcePath){
		win = new JDialog();
		win.setTitle(title);
		win.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		win.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cancel();
			}
		});
		win.getContentPane().setLayout(new BorderLayout());

		JPanel main = new JPanel(new BorderLayout());
		main.add(locationBar, BorderLayout.NORTH);
		main.add(fileTree, BorderLayout.CENTER);
		win.getContentPane().add(main, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton okButton = new JButton(I18n.getString("OK"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				win.dispose();
			}
		});
		JButton cancelButton = new JButton(I18n.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel();
			}
		});
		buttons.add(okButton);
		buttons.add(cancelButton);
		win.getContentPane().add(buttons, BorderLayout.SOUTH);

		locationBar.addFileListener(fileTree);
		locationBar.addFileListener(this);
		fileTree.addFileListener(locationBar);
		fileTree.addFileListener(this);
		fileTree.setViewZipJarAsFolders(false);

 		win.setSize(300, 375);
		win.setModal(true);
		if (sourcePath != null){
			selectedFolder = sourcePath;
			locationBar.setFile(selectedFolder);
			fileTree.goToFolder(selectedFolder);
		}
		win.setVisible(true);
	}

	private void cancel(){
		selectedFolder = null;
		win.dispose();
	}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileListener
	 */
	public void folderChange(File folder){
		selectedFolder = folder;
	}

	/**
	 * Invoked when a file or a folder is selected.
	 * @param listNode the node that has been selected
 	 * @see FileListener
	 * @version 1.0.1
	 */
	public void fileSelected(ListNode listNode){}

	public void endLoadingProcess(){}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileListener
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder){}

	public void fileDoubleClickSelected(ListNode listNode){}

	/**
	 * Devuelve la carpeta seleccionada.
	 * se usará esta función para conocer el resultado de la selección.
	 * Si el resultado es null significará que se ha pulsado cancelar.
	 * @return Carpeta seleccionado o null en caso de no haber seleccionado ninguna.
	 */
	public File getSelectedFolder(){
		return selectedFolder;
	}
}
