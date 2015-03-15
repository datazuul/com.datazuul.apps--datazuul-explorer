/* JFileCopy - The window to copy or delete files
 * Copyright (C) 2000-2001 Inigo Gonzalez
 * sensei@hispavista.com
 * http://www.geocities.com/innigo.geo
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.jos.jexplorer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;

/**
 * Esta clase implementa las funciones de copiar y borrar ficheros.
 * Muestra una pantalla de progreso donde se indica el progreso de la copia o borrado y 
 * permite cancelar la operación.
 * This class copy and delete files. Shows a window with a progress bar.
 */
public class JFileCopy extends JFrame implements Runnable{
	//The progress bar that shows the progress file copy
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);

	//The listener for the cancel button
	private CopyListener copyListener = null;

	//The files to copy or to delete
	private File[] files = null;

	//The destination folder. If this variable is null the process is a deleted
	private File destination = null;

	//True if the process is canceled
	private boolean cancel = false;

	/**
	 * Shows the window and run a DELETE process.
	 *@param files the files to delete
	 */
	public JFileCopy(File[] files){
		this(files, null);
	}

	/**
	 * Shows the window and run the COPY process.
	 * @param files the files to copy
	 * @param destination the folder where to copy the file list. 
	 * @param destination If this param is null then the process is to delete.
	 */
	public JFileCopy(File[] files, File destination){
		this.files = files;
		this.destination = destination;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w){
				cancelProcess();
			}
		});

		JPanel paneOne = new JPanel(new BorderLayout());
		if (destination == null){
			setTitle(I18n.getString("DELETE_TITLE"));
			paneOne.add(new JLabel(I18n.getString("DELETE_TITLE")), BorderLayout.CENTER);		
		} else {
			setTitle(I18n.getString("COPY_TITLE"));
			paneOne.add(new JLabel(I18n.getString("COPY_TITLE_TO") + " " + destination.getPath()), BorderLayout.CENTER);
		}
		paneOne.add(progressBar, BorderLayout.SOUTH);

		JPanel paneTwo = new JPanel();
		JButton cancel = new JButton(I18n.getString("CANCEL"));
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if (copyListener != null){
					copyListener.cancelProcess();
				}
				cancelProcess();
			}
		});
		paneTwo.add(cancel);
		getContentPane().add(paneOne, BorderLayout.CENTER);
		getContentPane().add(paneTwo, BorderLayout.SOUTH);

		progressBar.setMinimum(0);
		progressBar.setMaximum(files.length);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setString(I18n.getString("READY"));

		showWindow();	
		new Thread(this).start();
	}

	private void cancelProcess(){
		cancel = true;
	}

	/**
	 * Shows the window with the progress bar
	 */
	private void showWindow(){
		setResizable(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(400, 150);
		setSize(frameSize);
		setLocation(new Point((screen.width - frameSize.width) / 2, (screen.height - frameSize.height) / 2));
		setVisible(true);
	}

	/**
	 * Comienza la copia o el borrado. Inits the copy or the delete.
	 */
	public void run(){
		boolean cancelProcess = false;
		for(int i=0; i<files.length; i++){
			if (cancel) {
				cancelProcess = true;
				break;
			}
			progressBar.setValue(i + 1);
			if (files[i] != null){
				progressBar.setString(files[i].getName() + " " + progressBar.getPercentComplete()*100 + "%");
				if (destination == null) {
					if (!deleteFile(files[i])){
						cancelProcess = true;
						break;
					}
				} else {
					if (!copyFile(files[i], destination)){
						cancelProcess = true;
						break;
					}
				}
			}
		}
		if (copyListener != null){
			if (cancelProcess){
				copyListener.cancelProcess();
			} else {
				copyListener.endProcess();
			}
		}
		dispose();
	}

	/**
	 *delete a file or a folder
	 *@param file tho file or folder to delete
	 */
	private boolean deleteFile(File file){
		try{
			if (!file.exists()){
				JOptionPane.showMessageDialog(null, I18n.getString("DELETE_WARNING2"), I18n.getString("COPY_TITLE"), JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (file.isFile()){
				if (!file.delete()){
					JOptionPane.showMessageDialog(this, I18n.getString("DELETE_WARNING"), I18n.getString("DELETE_TITLE"), JOptionPane.WARNING_MESSAGE);
					return false;
				} else{
					return true;
				}
			} else {
				return deleteFolder(file);
			}
		} catch(IOException ioe){
			return false;
		}
	}

	/**
	 * Copy a file or a folder to a destination folder
	 * @param file the file or folder to copy
	 * @param destination folder where to copy the file or folder
	 * @return true if everything go, false if the user select cancel in the option panel.
	 * @return This is only if the file to copy exits.
	 */
	private boolean copyFile(File file, File destination){
		try{
			if (!file.exists()){
				JOptionPane.showMessageDialog(null, I18n.getString("COPY_WARNING3"), I18n.getString("COPY_TITLE"), JOptionPane.ERROR_MESSAGE);
				return false;
			}
			boolean cancelCopy = false; //continue the action
			boolean cancelCopyFile = false; //continue the action but not copy the file

			String fileName = file.getName();
			if (fileName.length() == 0){
				fileName = file.getPath();
			}
			File newFile = new File(destination, fileName);
			if (isParent(file, destination)){
				JOptionPane.showMessageDialog(null, I18n.getString("COPY_WARNING1"), I18n.getString("COPY_TITLE"), JOptionPane.ERROR_MESSAGE);
				cancelCopy = true;
			} else if (newFile.getParentFile().equals(file.getParentFile())){
				JOptionPane.showMessageDialog(null, I18n.getString("COPY_WARNING2"), I18n.getString("COPY_TITLE"), JOptionPane.ERROR_MESSAGE);
				cancelCopy = true;
			} else if (newFile.exists()){
				int ans = JOptionPane.showConfirmDialog(this, I18n.getString("COPY_TITLE_TO") + " " + newFile.toString() + "\n" + I18n.getString("COPY_QUESTION"), I18n.getString("COPY_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ans == JOptionPane.YES_OPTION){
					if (newFile.isFile()) {
						deleteFile(newFile);
					} else {
						deleteFolder(newFile);
					}
				} else if (ans == JOptionPane.NO_OPTION){
					cancelCopyFile = true;
				} else { //CANCEL_OPTION or CLOSED_OPTION
				   cancelCopy = true;
				}
			}
			if (cancelCopy){
				return false;
			} else {
				if (cancelCopyFile){
					return true;
				} else if (file.isFile()){
					if (newFile.createNewFile()){
						copyBytes(file, newFile);
						return newFile.setLastModified(file.lastModified());
					} else {
						return false;
					}
				} else { //is a directory
					if (newFile.mkdir()){
						copyFolder(file, newFile);
						return true;
					} else {   
						return false;
					}
				}
			}
		} catch(IOException ioe){
			return false;
		}
	}
	
	private boolean copyFolder(File sourceFolder, File destination) throws IOException {
		boolean res = true;
		File[] files = sourceFolder.listFiles();

		for(int i = 0; i < files.length; i++){
			res = copyFile(files[i], destination);
			if (!res){
				break;
			}
		}
		return res;
	}

	private boolean deleteFolder(File folder) throws IOException {
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++){
			if (!deleteFile(files[i])){
				break;
			}
		}
		return folder.delete();
	}
	
	private void copyBytes(File file, File newFile) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(newFile);
		byte[] buff = new byte[2048];
		int len = fis.read(buff);
		while (len >-1){
			fos.write(buff, 0, len);
			len = fis.read(buff);
		}
		fis.close();
		fos.close();
	}

	/**
	 * Devuelve verdadero si el parámetro child es hijo de parent en la estructura de directorios.
	 * Return true if the parameter named child is child of the parent parameter in the directoy structure.
	 * @param parent fichero padre.
	 * @param child el fichero a mirar si es hijo de parent dentro de la estructura de directorios
	 * @return verdadero si child es hijo de parent dentro de la estructura de directorios.
	 * @return true if the chil is a child of parent in the directory structure.
	 */
	private boolean isParent(File parent, File child){
		String parentPath = parent.getPath();
		String childPath = child.getPath();
		return childPath.startsWith(parentPath);
	}

	/**
	 * To register a listener for the cancel button.
	 * @param copyListener this listener will be notified when the cancel button is pressed
	 */
	public void setCopyListener(CopyListener copyListener){
		this.copyListener = copyListener;
	}

	/**
	 * To unregister the listener
	 */
	public void removeCopyListener(){
		copyListener = null;
	}

	/**
	 * Este <i>Interface</i> implementa un <i>listener</i> para las opciones de cancelar y finalizar el proceso de copia o borrado.
	 * Interface for the cancel button and finish process listener.
	 * Para utilizarlo se ha de escribir JFileCopy.CopyListener.
	 */
	public interface CopyListener{
		/**When the process has finish fires the endProcess
		 */
		public void endProcess();

		/**When the cancel button is pressed this listener fired the cancelPressed function
		 */
		public void cancelProcess();
	}
}
