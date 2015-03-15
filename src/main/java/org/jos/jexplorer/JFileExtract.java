/* JFileExtract - The window to extract files from a zip or jar file
 * Copyright (C) 2001 Inigo Gonzalez
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
import java.util.zip.*;

/**
 * This class extracts files from a zip or jar file, showing a progress bar.
 * @version 1.2
 */
public class JFileExtract extends JFrame implements Runnable{
	//The progress bar that shows the progress files extract
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);

	//the listener for the cancel button
	private JFileCopy.CopyListener copyListener = null;

	//the owner zip or jar file.
	private ZipFile zip = null;

	//the zipEntries to extract
	private ZipEntry[] zipEntries = null;

	//the destination folder. If this variable is null the process is a deleted
	private File destinationFolder = null;

	//true if the process is canceled
	private boolean cancel = false;
	
	/**
	 * Display the window and run the extract process.
	 * @param zipEntries the files to extract
	 * @param destinationFolder the folder where to extract the file list.
	 * @param destinationFolder If this param is null then the process is a delete.
	 */
	public JFileExtract(ZipFile zip, ZipEntry[] zipEntries, File destinationFolder){
		this.zip = zip;
		this.zipEntries = zipEntries;
		this.destinationFolder = destinationFolder;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w){
				cancelProcess();
			}
		});

		JPanel paneOne = new JPanel(new BorderLayout());
		if (destinationFolder == null){
			setTitle(I18n.getString("DELETE_TITLE"));
			paneOne.add(new JLabel(I18n.getString("DELETE_TITLE")), BorderLayout.CENTER);		
		} else {
			setTitle(I18n.getString("EXTRACT_TITLE"));
			paneOne.add(new JLabel(I18n.getString("EXTRACT_TITLE_TO") + " " + destinationFolder.getPath()), BorderLayout.CENTER);
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
		progressBar.setMaximum(zipEntries.length);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setString(I18n.getString("READY"));

		setResizable(false);
		setSize(400, 150);
		setVisible(true);

		new Thread(this).start();
	}

	private void cancelProcess(){
		cancel = true;
	}
	/**
	 * Comienza la copia o el borrado. Inits the copy or the delete.
	 */
	public void run(){
		boolean cancelProcess = false;
		for(int i=0; i<zipEntries.length; i++){
			if (cancel) {
				cancelProcess = true;
				break;
			}
			progressBar.setValue(i + 1);
			if (zipEntries[i] != null){
				progressBar.setString(zipEntries[i].getName() + " " + progressBar.getPercentComplete()*100 + "%");
				//hay mirar que lo que se va a copiar no es padre del destination folder
				if (!copyFile(zipEntries[i], destinationFolder)){
					cancelProcess = true;
					break;
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
	 *@param file the file or folder to delete
	 */
	private boolean deleteFile(File file){
		try{
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

	private boolean deleteFolder(File folder) throws IOException {
		File[] files = folder.listFiles();
		for(int i = 0; i < files.length; i++){
			if (!deleteFile(files[i])){
				break;
			}
		}
		return folder.delete();
	}

	private String getFileName(ZipEntry zipEntry){
		String name = zipEntry.getName();
		int last = name.lastIndexOf("/");
		if (last == -1){
			return name;
		} else {
			return name.substring(last);
		}
	}

	/**
	 * Copy a file or a folder to a destination folder
	 * @param file the file or folder to copy
	 * @param destinationFolder folder where to copy the file or folder
	 * @return true if everything go, false if the user select cancel in the option panel.
	 * @return This is only if the file to copy exits.
	 */
	private boolean copyFile(ZipEntry zipEntry, File destinationFolder){
		try{
			boolean cancelCopy = false; //continue the action
			boolean cancelCopyFile = false; //continue the action but not copy the file
	
			//String fileName = getFileName(zipEntry);
			String entryName = zipEntry.getName();
			int last = entryName.lastIndexOf("/");
			if (last != -1){
				File folder = new File(destinationFolder.getPath(), entryName.substring(0, last));
				folder.mkdirs();
			}
			File newFile = new File(destinationFolder, entryName); // , fileName);
			if (newFile.exists()){
				int ans = JOptionPane.showConfirmDialog(this, I18n.getString("COPY_TITLE_TO") + " " + newFile.toString() + "\n" + I18n.getString("COPY_QUESTION"), I18n.getString("COPY_TITLE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ans == JOptionPane.YES_OPTION){
					if (newFile.isFile()) {
						deleteFile(newFile);
					} else {
						//deleteFolder(newFile);
					}
				} else if (ans == JOptionPane.NO_OPTION){
					cancelCopyFile = true;
				} else { //CANCEL_OPTION or CLOSED_OPTION
					cancelCopy = true;
				}
			}
			if (cancelCopy){
				return false;
			} else if (cancelCopyFile){
				return true;
			} else if (!zipEntry.isDirectory()){
				if (newFile.createNewFile()){
					copyBytes(zipEntry, newFile);
					return true; 
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
	}
	
	private void copyBytes(ZipEntry zipEntry, File newFile) throws IOException {
		InputStream is = zip.getInputStream(zipEntry);
		FileOutputStream fos = new FileOutputStream(newFile);
		byte[] buff = new byte[2048];
		int len = is.read(buff);
		while (len >-1){
			fos.write(buff, 0, len);
			len = is.read(buff);
		}
		is.close();
		fos.close();
	}

	/**
	 * To register a listener for the cancel button.
	 * @param copyListener this listener will be notified when the cancel button is pressed
	 */
	public void setCopyListener(JFileCopy.CopyListener copyListener){
		this.copyListener = copyListener;
	}

	/**
	 * To unregister the listener
	 */
	public void removeCopyListener(){
		copyListener = null;
	}
}
