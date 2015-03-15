/*
 * Favorites - Load the favorite menu from the xml file
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

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class Favorites {
/**
 * Creates the Favorites menu from an xml file. the xml file must have this tags:
 * <code>
 * <favorite>
 * <item url=".." title=".."/>
 * <menu title="..">
 *   <item url="..." title=".."/>
 *   <menu title="..">
 *     <item url=".." title=".."/>
 *   </menu>
 * </menu>
 * </favorite>
 * </code>
 * @version 1.2
 */

	private static Icon iconItem = ThemesManager.getImage("smallFile.gif");
	private static Icon iconMenu = ThemesManager.getImage("smallCloseFolder.gif");
	private String xmlFile;
	private JMenu menu = null;

	private FileComponent fileComponent = new FileComponent();

	/**
	 * Creates the favorite menu from the xml file.
	 */
	public Favorites(String xmlFile){
		this.xmlFile = xmlFile;
		refresh();
	}

	/**
	 * Creates the menu from the xml file
	 */
	public void refresh(){
		try{
			SAXParserFactory saxF = SAXParserFactory.newInstance();
			SAXParser saxP = saxF.newSAXParser();
			DefaultHandler menuHandler = new MenuHandler();
			File file = new File(xmlFile);
			saxP.parse(file, menuHandler);
			MenuHandler mh = (MenuHandler)menuHandler;
			menu = mh.getMenu();
		} catch(FileNotFoundException fnfe){
			//fnfe.printStackTrace();
			System.err.println("user.dir/jexplorer/favorites.xml not Found");
		} catch(ParserConfigurationException pce){
			pce.printStackTrace();
		} catch(SAXException se){
			se.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	/**
	 * Returns the menu
	 */
	public JMenu getFavoriteMenu(){
		return menu;
	}

	public void addFileListener(FileListener fileListener){
		fileComponent.addFileListener(fileListener);
	}

	public void removeFileListener(FileListener fileListener){
		fileComponent.removeFileListener(fileListener);
	}

	class MenuHandler extends DefaultHandler{

		private JMenu menu = null;
		private Stack menuStack = new Stack();

		public JMenu getMenu(){
			return menu;
		}

		public void startDocument(){
			menu = new JMenu(I18n.getString("mnuFavorites"));
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
			if (qName == "menu") {
				AttributesImpl attr = new  AttributesImpl(attributes);
				String name = attr.getValue("title");
				
				JMenu menu = new JMenu(name);
				menu.setIcon(iconMenu);
				menuStack.push(menu);
			} else if (qName == "item") {
				AttributesImpl attr = new  AttributesImpl(attributes);
				String name = attr.getValue("title");
				String url = attr.getValue("url");
				ExecAction exec = new ExecAction(name, iconItem, url);
				if (menuStack.empty()){
					menu.add(exec);
				} else {
					JMenu menu = (JMenu)menuStack.peek();
					menu.add(exec);
				}
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException{
			if (qName == "menu") {
				JMenu m = (JMenu)menuStack.pop();
				if (menuStack.empty()){
					menu.add(m);
				} else {
					JMenu menu = (JMenu)menuStack.peek();
					menu.add(m);
				}
			}
		}
	}

	class ExecAction extends AbstractAction{
		private String url;

		public ExecAction(String name, Icon icon, String url){
			super(name);
			putValue(Action.SMALL_ICON, icon);
			this.url = url;
		}
		public void actionPerformed(ActionEvent e){
			if (url != null){
				File folder = new File(url);
				if (folder.exists()){
					 fileComponent.fireChangeListener(folder);
				} else {
					JOptionPane.showMessageDialog(null, I18n.getString("FAV_ERROR_OLD_FOLDER"), I18n.getString("FAV_ERROR_TITLE"), JOptionPane.ERROR_MESSAGE); 
				}
			} else {
				JOptionPane.showMessageDialog(null, I18n.getString("FAV_ERROR_WRONG_FAV"), I18n.getString("FAV_ERROR_TITLE"), JOptionPane.ERROR_MESSAGE); 
			}
		}
	}
}
