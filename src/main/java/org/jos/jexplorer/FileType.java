/*
 * FileType.java - 
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

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Contents the Jos file types. This class helps to JExplorer to find the icons for diferents file types.
 * In future version, it will help JExplorer to know the path where find the executable files for this file type.
 * @version 1.0.1
 */
public class FileType{

	private String name = null;
	private ImageIcon bigIcon = null;
	private ImageIcon smallIcon = null;
	private String viewer = null; //viewer name
	private String url;
	private String className;

	private static Hashtable hash = new Hashtable();

	public static FileType DEFAULT_FILE_TYPE = new FileType("Default file", ThemesManager.getImage("bigFile.gif"));
	public static FileType DEFAULT_FOLDER_TYPE = new FileType("Default folder", ThemesManager.getImage("bigFolder.gif"));

	static {
		refresh();
	};

	/**
	 * Creates the list from the xml file
	 */
	public static void refresh(){
		try{
			String xmlFile = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "fileTypes.xml";
			SAXParserFactory saxF = SAXParserFactory.newInstance();
			SAXParser saxP = saxF.newSAXParser();
			DefaultHandler fileTypeHandler = new FileTypeHandler();
			File file = new File(xmlFile);
			saxP.parse(file, fileTypeHandler);
		} catch(FileNotFoundException fnfe){
			fnfe.printStackTrace();
			//System.err.println("user.dir/resources/filetypes.xml not Found");
		} catch(ParserConfigurationException pce){
			pce.printStackTrace();
		} catch(SAXException se){
			se.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	/**
	 * Creates a filetype with the specific name and icon image.
	 * @param name the type name
	 * @param image the icon image
	 */
	private FileType(String name, ImageIcon image){
		this.name = name;
		if (image != null){
			bigIcon = image;
			smallIcon = new SmallIcon(bigIcon);
		}
		this.viewer = null;
		this.url = null;
		this.className = null;
	}

	/**
	 * Creates a filetype with the specific name and icon path.
	 * @param name the type name or description
	 * @param path the icon path
	 * @param viewer viewer name (class name)
	 * @param url to launch the file (url to a jar file)
	 * @param clazz Internal class in the jar file where to execute the main class
	 */
	protected FileType(String name, String path, String viewer, String url, String clazz){
		this.name = name;
		if (path != null){
			bigIcon = new ImageIcon(System.getProperty("user.dir") + File.separator + path);
			smallIcon = new SmallIcon(bigIcon);
		}
		this.viewer = viewer;
		this.url = url;
		this.className = clazz;
	}

	/**
	 * finds the file type for a given type. This type, usually, will be a file extension
	 * @param type file type or file extension
	 * @return the file type of a given extension
	 */
	public static FileType getFileType(String type){
		return (FileType)hash.get(type);
	}

	/**
	 * Adds a new file type to the hash table
	 * @param fileType the new file type
	 * @param type the extension name. This extension will be the key
	 */
	public static void addFileType(FileType fileType, String ext){
		hash.put(ext, fileType);
	}

	/**
	 * Clears the hash table
	 */
	public static void clearList(){
		hash.clear();
	}

	public String getTypeName(){
		return name;
	}

	public String getViewerName(){
		return viewer;
	}

	public Icon getBigIcon(){
		return bigIcon;
	}

	public Icon getSmallIcon(){
		return smallIcon;
	}

	public String getUrl(){
		return url;
	}

	public String getClassName(){
		return className;
	}

	/**
	 * Paints a small icon from the big icon.
	 */
	class SmallIcon extends ImageIcon{
		public static final int SMALL_WIDTH = 16;
		public static final int SMALL_HEIGHT = 16;

		private ImageIcon icon;

		public SmallIcon(ImageIcon icon){
			this.icon = icon;
		}

		public int getIconWidth(){
			return SMALL_WIDTH;
		}

		public int getIconHeight(){
			return SMALL_HEIGHT;
		}

		public void paintIcon(Component c, Graphics g, int x, int y){
			g.drawImage(icon.getImage(), x, y, SMALL_WIDTH, SMALL_HEIGHT, null);
		}
	}
}

class FileTypeHandler extends DefaultHandler{

	public void startDocument(){
		FileType.clearList();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if (qName == "ext") {
			AttributesImpl attr = new AttributesImpl(attributes);
			String ext = attr.getValue("name");
			String name = attr.getValue("label");
			String icon = attr.getValue("icon");
			String qview = attr.getValue("qview");
			String url = attr.getValue("url");
			String clazz = attr.getValue("class");
			FileType fileType = new FileType(name, icon, qview, url, clazz);
			FileType.addFileType(fileType, ext);
		}
	}

	public void endDocument(){}
}
