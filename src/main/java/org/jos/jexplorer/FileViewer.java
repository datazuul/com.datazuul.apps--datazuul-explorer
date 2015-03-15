/*
 * FileViewer.java - Viewer component
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

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class FileViewer extends JPanel{

	/**
	 * A list with the loaded viewers
	 */
	private Vector views = null;
	private QuickView selectedView = null; //the viewer selected

	public FileViewer(){
		super(new BorderLayout());
	}

	/**
	 * Load views from its jar files.
	 */
	public void loadViews(){
		if (views == null){
			File viewsFolder = new File(System.getProperty("user.dir") + File.separator + "views");
			//File viewsFolder = new File("c:\\jexplorer\\bin\\views");
			if (viewsFolder.exists()){
				File[] jars = viewsFolder.listFiles(new FilenameFilter(){
					public boolean accept(File dir, String name){
						return (name.endsWith(".jar"));
					}
				});
				if (jars.length > 0){
					views = new Vector(jars.length);
					for(int i = 0; i<jars.length; i++){
						try{
							QViewLoader qvLoader = new QViewLoader(jars[i].toURL(), getClass().getClassLoader());
							//String className = qvLoader.getMainClassName();
							String className = jars[i].getName();
							className = className.substring(0, className.length()-4);
							Class clazz = qvLoader.loadClass(className);
							views.add(clazz.newInstance());
						} catch(Exception e){
							System.err.println(e);
						}
					}
				}
			}
		}
	}

	/**
	 * Loads the appropiate viewer for the list node
	 */
	public boolean showFile(ListNode listNode){
		String viewer = listNode.getViewerName();
		return showFile(listNode, viewer);
	}

	/**
	 * Loads the appropiate viewer for the list node
	 * @param listNode the file to show
	 * @param viewer the viewer name to use.
	 */
	public boolean showFile(ListNode listNode, String viewer){
		boolean isOk = false;

		if (views != null){
			for (Enumeration e = views.elements(); e.hasMoreElements();) {
				QuickView view = (QuickView)e.nextElement();
				Class clazz = view.getClass();
				if (clazz.getName().equals(viewer)){
					if (selectedView != view){
						if (selectedView != null) selectedView.terminate();
						selectedView = view;
						removeAll();
						add(new JScrollPane(selectedView.getQuickView()), BorderLayout.CENTER);
					}
					selectedView.showFile(listNode);
					isOk = true;
					break;
				}
			}
		}
		return isOk;
	}

	public void clearSelectedView(){
		selectedView = null;
	}

	public Vector getViews(){
		return views;
	}
}
