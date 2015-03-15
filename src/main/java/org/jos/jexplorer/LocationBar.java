/*
 * LocationBar.java -
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Implementa un campo de texto donde se puede escribir una dirección de un fichero y
 * si existe genera un <i>changeFolder</i> en los Filecompoennt
 */
public class LocationBar extends FileComponent implements FileListener{
	private JTextField location = new JTextField();

	/**
	 * Constructor de la barra <i>locationBar</i>. Esta control implementa
	 */
	public LocationBar(){
		setLayout(new BorderLayout(0, 0));
		add(new JLabel(I18n.getString("Location")), BorderLayout.WEST);
		add(location, BorderLayout.CENTER);
		location.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER){
					File file = new File(location.getText());
					if (file.exists()){
						fireChangeListener(file);
					}
				}
			}
		});
	}

	/**
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileComponent
	 */
	public void folderChange(File folder){
		if (folder != null){
			location.setText(folder.toString());
		} else {
			location.setText("");//FileTree.ROOT_NAME
		}
	}

	/**
	 * Implements the FileListener interface.
	 * This method is invoked when another <i>FileComponent</i> changes
	 * @folder the new folder to show.
 	 * @see FileComponent
	 * @version 1.0.1
	 */
	public void folderContentChange(File folder){}

	/**
	 * Implements the FileListener interface.
 	 * @see FileComponent
	 * @version 1.0.1
	 */
	public void fileSelected(ListNode listNode){
	}

	public void endLoadingProcess(){}

	public void fileDoubleClickSelected(ListNode listNode){}

	/**
	 * Esta función se utiliza para inicializar, en cualquier momento, el control con un fichero.
	 * No genera el evento folderChange en las clases registradas como FileListeners
	 * @param file Carpeta con la que inicializar, en cualquier momento, el control.
	 */
	public void setFile(File file){
		location.setText(file.toString());
	}

	/**
	 * Devuelve el texto qur hay en el campo de texto. Este texto puede no indicar un fichero.
	 * @return the text contents in the textbox.
	 */
	public String getText(){
		return location.getText();
	}
}
