/*
 * ZipListNode - 
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

import java.util.zip.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * FileList's nodes that represent zip or jar entries.
 * @version 1.0.1
 */
class ZipListNode implements ListNode{
	private ZipEntry zip = null;
	private FileType fileType = null;
	private File file = null;

	private ZipFile zipFile = null;
	/**
	 * Creates a node from a zip entry.
	 * @param zip the zip entry to represent.
	 * @file the zip file
	 */
	public ZipListNode(ZipEntry zip, File file){
		this.zip = zip;
		this.file = file;
		if (!zip.isDirectory()){
			String name = zip.getName();
			int i = name.lastIndexOf(".");
			if (i > 0){
				String extension = name.substring(i+1).toLowerCase();
				fileType = FileType.getFileType(extension);
			}
			if (fileType == null){
				fileType = FileType.DEFAULT_FILE_TYPE;
			}
		} else {
			fileType = FileType.DEFAULT_FOLDER_TYPE;
		}
	}

	/**
	 * Return the name of the file represented by this class.
	 * @return The name of the file.
	 */
	public String getFileName(){
		return zip.getName();
	}

	public String getTypeName(){
		return "Zip/Jar file entry";
	}

	public String getFullName(){
		return file.getPath() + "!" + getFileName();
	}

	public String getViewerName(){
		return fileType.getViewerName();
	}

	public Object getBigIcon(){
		return fileType.getBigIcon();
	}
	
	public Object getSmallIcon(){
		return fileType.getSmallIcon();
	}

	public String getUrl(){
		return null;
	}

	public URL getURL(){
		try {
			return file.toURL();
		} catch(MalformedURLException mfue){
			return null;
		}
	}

	public String getClassName(){
		return null;
	}

	public Object getObject(){
		return zip;
	}

	public void closeZipFile() throws IOException{
		if (zipFile != null) zipFile.close();
	}
	
	public ListNode[] getChildren(){
		return getChildren("");
	}
	
	public ListNode[] getChildren(String filter){
		return null;
	}

	public boolean isDirectory(){
		return zip.isDirectory();
	}

	public long length(){
		return zip.getSize();
	}
	
	public long lastModified(){
		return zip.getTime();
	}

	public int compareTo(ListNode listNode){
		String s1 = toString();
		String s2 = listNode.toString();
		return s1.compareToIgnoreCase(s2);
	}

	public String toString(){
		return zip.toString();
	}

	/**
	 * Creates the popup menu for a file
	 * @return the popup menu
	 */
	public JPopupMenu getSinglePopupMenu(FileList fileList){
		JPopupMenu menu = new JPopupMenu("Zip");
		menu.add(new FilePropertiesAction());
		menu.add(new ExtractFileAction(fileList));
		return menu;
	}

	/**
	 * Creates the popup menu for a list of files
 	 * @return the popup menu
	 */
	public JPopupMenu getMultiPopupMenu(FileList fileList, ListNode[] listNodes){
		JPopupMenu menu = new JPopupMenu("Zip");
		ZipEntry[] zipEntries = new ZipEntry[listNodes.length];
		for(int i = 0; i<listNodes.length; i++){
			zipEntries[i] = (ZipEntry)listNodes[i].getObject();
		}
		menu.add(new ExtractFilesAction(fileList));
		return menu;
	}

	public InputStream getInputStream(){
		try{
			zipFile = new ZipFile(file);
			return zipFile.getInputStream(zip);
		} catch(ZipException ze){
			System.err.println(ze);
			return null;
		} catch(IOException ioe){
			System.err.println(ioe);
			return null;
		} catch(IllegalStateException ise){
			System.err.println(ise);
			return null;
		}
	}

	//Actions
	class FilePropertiesAction extends AbstractAction{
		public FilePropertiesAction(){
			super(I18n.getString("mnuFFilePropertiesWindow"));
		}
		public void actionPerformed(ActionEvent e){
			FilePropertiesWindow fpw = new FilePropertiesWindow(ZipListNode.this);
		}
	}

	class ExtractFileAction extends AbstractAction implements JFileCopy.CopyListener{
		private ZipEntry[] zipEntries = new ZipEntry[1];

		public ExtractFileAction(FileList fileList){
			super(I18n.getString("mnuFExtractTo"));
			zipEntries[0] = (ZipEntry)fileList.getSelectedListNode().getObject();
		}

		public void actionPerformed(ActionEvent e){
			if (zipEntries[0]!= null){
				File folder = file.getParentFile();
				SelectFolderWindow copyWindow = new SelectFolderWindow(I18n.getString("mnuFExtractTo"), folder);
				folder = copyWindow.getSelectedFolder();
				if (folder != null){
					try{
						JFileExtract fileExtract = new JFileExtract(new ZipFile(file), zipEntries, folder);
						fileExtract.setCopyListener(this);
					} catch(ZipException ze){
						System.err.println(ze);
					} catch(IOException ioe){
						System.err.println(ioe);
					}
				}
			}
		}
		public void endProcess(){
			//fileList.refresh(true);
			//fileList.fireContentChangeListener();
		}

		public void cancelProcess(){
			//fileList.refresh(true);
			//fileList.fireContentChangeListener();
		}
	}

	class ExtractFilesAction extends AbstractAction implements JFileCopy.CopyListener{
		//zips entries to extract
		private ZipEntry[] zipEntries = null;

		public ExtractFilesAction(FileList fileList){
			super(I18n.getString("mnuFExtractTo"));
			ListNode[] listNodes = fileList.getSelectedListNodes();
			zipEntries = new ZipEntry[listNodes.length];
			for(int i = 0; i<listNodes.length; i++){
				zipEntries[i] = (ZipEntry)listNodes[i].getObject();
			}
		}

		public void actionPerformed(ActionEvent e){
			if (zipEntries != null){
				File folder = file.getParentFile();
				SelectFolderWindow copyWindow = new SelectFolderWindow(I18n.getString("mnuFExtractTo"), folder);
				folder = copyWindow.getSelectedFolder();
				if (folder != null){
					try {
						JFileExtract fileExtract = new JFileExtract(new ZipFile(file), zipEntries, folder);
						fileExtract.setCopyListener(this);
					} catch(ZipException ze){
						System.err.println(ze);
					} catch(IOException ioe){
						System.err.println(ioe);
					}
				}
			}
		}
		public void endProcess(){
			//fileList.refresh(true);
			//fileList.fireContentChangeListener();
		}

		public void cancelProcess(){
			//fileList.refresh(true);
			//fileList.fireContentChangeListener();
		}
	}
}
