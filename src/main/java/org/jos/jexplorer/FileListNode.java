/*
 * FileListNode - 
 * Copyright (C) 2000-2001 I�igo Gonz�lez
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
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

/**
 * FileList's nodes that represent files and directories.
 * @version 1.0.1
 */
public class FileListNode implements ListNode{

	private File file = null;
	private FileType fileType = null;

	/**
	 * Creates a FileListNode for the root file.
	 */
	public FileListNode(){
		fileType = FileType.DEFAULT_FOLDER_TYPE;
	}
	
	/**
	 * Creates a node from a file or directory.
	 * @param file file to represent.
	 */
	public FileListNode(File file){
		this.file = file;
		if (!file.isDirectory()){
			String name = file.getPath();
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
		String name = file.getName();
		if (name.length() == 0){
			name = file.getPath();
		}
		return name;
	}

	public String getFullName(){
		return file.getPath();
	}

	public String getTypeName(){
		return fileType.getTypeName();
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

	//to execute the associated file
	public String getUrl(){
		return fileType.getUrl();
	}

	//to execute the associated file
	public String getClassName(){
		return fileType.getClassName();
	}

	public URL getURL(){
		try {
			return file.toURL();
		} catch(MalformedURLException mfue){
			return null;
		}
	}

	public Object getObject(){
		return file;
	}

	public ListNode[] getChildren(){
		return getChildren("");
	}

	/**
	 * Returns the list of files from the directory represents by this object.
	 * @filter the filter to apply. Only accepts hello* or *hello
	 */
	public ListNode[] getChildren(String filter){
		File[] files = null;
		if (file == null){
			files = File.listRoots();
		} else if (filter.length() > 0 && !filter.equals("*.*")) {
			files = file.listFiles(createFilter(filter));
		} else {
			files = file.listFiles();
		}
		ListNode[] nodes = null;
		if (files != null){
			nodes = new ListNode[files.length];
			for(int i = 0; i < files.length; i++){
				nodes[i] = new FileListNode(files[i]);
			}
		} else if (fileType.getTypeName().equals("Zip file") || fileType.getTypeName().equals("Jar file")){
			nodes = listZips(file, new ZipListFilter(filter));
		}
		return nodes;
	}

	/**
	 * Returns the zip entries from a zip file
	 */
	private ListNode[] listZips(File file, ZipListFilter filter){
		try{
			ZipFile zipFile = new ZipFile(file);
			ArrayList array = new ArrayList();
			int i = 0;
			for (Enumeration e = zipFile.entries(); e.hasMoreElements();){
				ZipEntry zipEntry = (ZipEntry)e.nextElement();
				if (!zipEntry.isDirectory()){
					ZipListNode zip = new ZipListNode(zipEntry, file);
					if (filter.accept(zip))	array.add((ListNode)zip);
					i++;
				}
			}
			zipFile.close();
			if (i > 0) {
				return (ListNode[])array.toArray(new ListNode[1]);
			} else {
				return null;
			}
		} catch (IOException e){
			return null;
		}
	}

	public boolean isDirectory(){
		if (file == null){
			return true;
		} else{
			return file.isDirectory();
		}
	}

	public long length(){
		if (file == null){
			return 0;
		} else {
			return file.length();
		}
	}

	public long lastModified(){
		if (file == null){
			return 0;
		} else {
			return file.lastModified();
		}
	}

	public int compareTo(ListNode listNode){
		String s1 = toString();
		String s2 = listNode.toString();
		return s1.compareToIgnoreCase(s2);
	}

	public String toString(){
		if (file == null){
			return I18n.getString("MY_PC");
		} else {
			return file.toString();
		}
	}

	/**
	 * Creates the popup menu for a file
	 * @return the popup menu
	 */
	public JPopupMenu getSinglePopupMenu(FileList fileList){
		JPopupMenu menu = new JPopupMenu();
		menu.add(new FilePropertiesAction());
		menu.addSeparator();
		menu.add(new CopyToAction(fileList));
		menu.add(new RenameAction(fileList));
		menu.addSeparator();
		menu.add(new DeleteAction(fileList));
		menu.addSeparator();
		menu.add(fileList.getAction(fileList.COPY_ACTION));
		menu.add(fileList.getAction(FileList.CUT_ACTION));
		menu.add(fileList.getAction(FileList.PASTE_ACTION));
		menu.addSeparator();
		menu.add(fileList.getAction(FileList.ZIP_ACTION));
		return menu;
	}

	/**
	 * Creates the popup menu for a list of files
	 * @param objects list of files where to apply the actions
 	 * @return the popup menu
	 */
	public JPopupMenu getMultiPopupMenu(FileList fileList, ListNode[] listNodes){
		File[] files = new File[listNodes.length];
		for(int i = 0; i<listNodes.length; i++){
			files[i] = (File)listNodes[i].getObject();
		}
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CopyToAction(fileList, files));
		menu.addSeparator();
		menu.add(new DeleteFilesAction(fileList, files));
		menu.addSeparator();
		menu.add(fileList.getAction(fileList.COPY_ACTION));
		menu.add(fileList.getAction(FileList.CUT_ACTION));
		menu.add(fileList.getAction(FileList.PASTE_ACTION));
		menu.addSeparator();
		menu.add(fileList.getAction(FileList.ZIP_ACTION));
		return menu;
	}

	public InputStream getInputStream(){
		try{
			FileInputStream fis = new FileInputStream(file);
			return (InputStream)fis;
		} catch(FileNotFoundException fnfe){
			return null;
		}
	}

	private FileFilter createFilter(String filter){
		return new FileListFilter(filter);
	}

	/**
	 * Only acepts *.*, *hello or hello*
	 * @see ZipListFilter
	 */
	class FileListFilter implements FileFilter{
		private String filter = "";

		public FileListFilter(String filter){
			this.filter = filter;
		}

		public boolean accept(File pathname){
			if (pathname.isDirectory()){
				return true;
			} else {
				String name = pathname.getName();
				if (filter.startsWith("*")){
					return name.endsWith(filter.substring(1));
				} else if (filter.endsWith("*")){
					return name.startsWith(filter.substring(0, filter.length()-1));
				} else {
					return true;
				}
			}
		}
	}

	/**
	 * Filter for zip entries. Only acepts *.*, *hello or hello*
	 * @see FileListFilter
	 */
	class ZipListFilter{ // implements FileFilter{
		private String filter = "";

		public ZipListFilter(String filter){
			this.filter = filter;
		}

		public boolean accept(ZipListNode pathname){
			if (pathname.isDirectory()){
				return true;
			} else if (filter.equals("*.*")) {
				return true;
			} else {
				String name = pathname.getFileName();
				if (filter.startsWith("*")){
					return name.endsWith(filter.substring(1));
				} else if (filter.endsWith("*")){
					return name.startsWith(filter.substring(0, filter.length()-1));
				} else {
					return true;
				}
			}
		}
	}
	
	//Actions
	class FilePropertiesAction extends AbstractAction{
		public FilePropertiesAction(){
			super(I18n.getString( "mnuFFilePropertiesWindow"));
		}
		public void actionPerformed(ActionEvent e){
			FilePropertiesWindow fpw = new FilePropertiesWindow(FileListNode.this);
		}
	}

	class RenameAction extends AbstractAction{
		private FileList fileList = null;
		
		public RenameAction(FileList fileList){
			super(I18n.getString("mnuFRename"));
			this.fileList = fileList;
		}
		public void actionPerformed(ActionEvent e){
			File file = (File)FileListNode.this.getObject();
			if (file != null){
				//String newName = JOptionPane.showInputDialog(null, I18n.getString("RenameWindow") + " " + file.getName(), I18n.getString("mnuFRename"), JOptionPane.QUESTION_MESSAGE);
				String newName = showInputDialog(file);
				if (newName != null){
					File newFile = new File(file.getParent(), newName);
					if (newFile.exists()){
						JOptionPane.showMessageDialog(null, I18n.getString("RenameWarning1"));
					} else if (file.renameTo(newFile)){
						fileList.refresh(false, true);
					} else {
						JOptionPane.showMessageDialog(null, I18n.getString("RenameWarning2"));
					}
				}
			}
		}

		/**
		 * Shows a input dialog
		 * @return the new name or null (if the user presses No)
		 */
		private String showInputDialog(File file){
			JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jp.add(new JLabel(I18n.getString("RenameWindow")));
			JTextField text = new JTextField(file.getName(), 25);
			text.setSelectionStart(0);
			text.setSelectionEnd(file.getName().length());
			jp.add(text);
			JOptionPane jop = new JOptionPane(jp, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
			JDialog dialog = jop.createDialog(null, I18n.getString("mnuFRename"));
			dialog.show();
			Integer res = (Integer)jop.getValue();
			if (res.intValue() == JOptionPane.YES_OPTION) {
				return text.getText();
			} else {
				return null;
			}
		}
	}

	class DeleteAction extends AbstractAction implements JFileCopy.CopyListener{
		private FileList fileList = null;
		
		public DeleteAction(FileList fileList){
			super(I18n.getString("mnuFDelete"));
			this.fileList = fileList;
		}
		public void actionPerformed(ActionEvent e){
			File file = (File)FileListNode.this.getObject();
			if (file != null){
				if (JOptionPane.showConfirmDialog(null, I18n.getString("DeleteWindow"), I18n.getString("mnuFDelete"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					File[] files = new File[1];
					files[0] = file;
					JFileCopy fileCopy = new JFileCopy(files);
					fileCopy.setCopyListener(this);
				}
			}
		}

		public void endProcess(){
			fileList.refresh(true, false);
			fileList.fireContentChangeListener();
		}

		public void cancelProcess(){
			endProcess();
		}
	}

	class CopyToAction extends AbstractAction implements JFileCopy.CopyListener{
		/**
		 * List of files where to apply the copy action
		 */
		private File[] files = null;
		private FileList fileList = null;
		
		public CopyToAction(FileList fileList){
			super(I18n.getString("mnuFCopyTo"));
			files = new File[1];
			files[0] = (File)FileListNode.this.getObject();
			this.fileList = fileList;
		}
		
		public CopyToAction(FileList fileList, File[] files){
			super(I18n.getString("mnuFCopyTo"));
			this.files = files;
			this.fileList = fileList;
		}

		public void actionPerformed(ActionEvent e){
			if (files != null){
				File folder = files[0].getParentFile();
				SelectFolderWindow copyWindow = new SelectFolderWindow(I18n.getString("mnuFCopyTo"), folder);
				folder = copyWindow.getSelectedFolder();
				if (folder != null){
					JFileCopy fileCopy = new JFileCopy(files, folder);
					fileCopy.setCopyListener(this);
				}
			}
		}

		public void endProcess(){
			fileList.refresh(true, true);
			//fileList.fireContentChangeListener();
		}

		public void cancelProcess(){
			endProcess();
		}
	}

	class DeleteFilesAction extends AbstractAction implements JFileCopy.CopyListener{
		/**
		 * List of files where to apply the delete action
		 */
		private File[] files = null;
		private FileList fileList = null;

		public DeleteFilesAction(FileList fileList, File[] files){
			super(I18n.getString("mnuFDelete"));
			this.files = files;
			this.fileList = fileList;
		}
		public void actionPerformed(ActionEvent e){
			if (files != null){
				if (JOptionPane.showConfirmDialog(null, I18n.getString("DeleteWindow"), I18n.getString("mnuFDelete"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					JFileCopy fileCopy = new JFileCopy(files);
					fileCopy.setCopyListener(this);
				}
			}
		}

		public void endProcess(){
			fileList.refresh(true, true);
		}

		public void cancelProcess(){
			fileList.refresh(true, true);
		}
	}

}
