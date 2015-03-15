/*
 * QViewLoader.java - Class loader for quick-viewers
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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import java.lang.reflect.*;

/**
 * Loads class from a QuickView. It's a JarClassLoader
 * @version 1.0.1
 * @author Inigo Gonzalez
 */
class QViewLoader extends URLClassLoader{

	private URL url;

	/**
	 * Creates a classLoader for a quickView.
	 * @file the jar file
	 */
	public QViewLoader(URL url, ClassLoader parent){
		super(new URL[] { url }, parent);
		this.url = url;
	}

	/**
	 * Returns the application entry point.
	 * @return the main class name
	 */
	public String getMainClassName() throws IOException{
		URL jarURL = new URL("jar", "", url + "!/");
		JarURLConnection uc = (JarURLConnection)jarURL.openConnection();
		Attributes attr = uc.getMainAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}

	public void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		Class c = loadClass(name);
		Method m = c.getMethod("main", new Class[] { args.getClass() });
		m.setAccessible(true);
		int mods = m.getModifiers();
		if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)){
			try{
				m.invoke(null, new Object[] { args });
			} catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}
	}

	public String toString(){
		return "QViewLoader 1.0 by InigoGonzalez";
	}
}
