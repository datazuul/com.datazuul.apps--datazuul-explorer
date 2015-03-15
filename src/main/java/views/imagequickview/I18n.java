package views.imagequickview;

/*
 * I18n.java - Internationalization support
 * Copyright (C) 2000 I�igo Gonz�lez
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

import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

public final class I18n{
	private static ResourceBundle resources;

	static {
		try {
			resources = ResourceBundle.getBundle("ImageQuickView"); //, Locale.getDefault());
		} catch (MissingResourceException mre) {
			System.err.println("ImageQuickView.properties not found!!!");
			//System.exit(1);
		}
	}

	/**
	 * Load an Icon from the file system or from the jar file (if Jexplorer is in a Jar file)
	 * @param app the class from this class get the resource(??)
	 * @param name path to the icon
	 * @return an ImageIcon
	 * @version 1.0.1
	 */
	public static ImageIcon getImageIcon(Object app, String name){
		URL url = app.getClass().getResource(name);
		if (url != null){
			return new ImageIcon(url);
		} else {
			return null;
		}
	}

	/**
	 * Return a string from the properties file
	 * @param key key to find
	 * @return the text asociated with the key
	 */
	public static String getString(String key){
		try{
			return resources.getString(key);
		} catch(Exception e){
			return key;
		}
	}
}
