/*
 * OdiseoClassLoader.java - 
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

package org.odiseo.core;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.jar.*;
import java.util.*;

public class OdiseoClassLoader extends URLClassLoader{

	private URL url; //the jar file name
	private String mainClass; //the main-class from the manifest file
	private Hashtable cache = new Hashtable(); //saves the loaded classes

	/**
	 * Creates a ClassLoader for the specified url.
	 * The url must be an executable jarfile. When this classLoader
	 * executes the jarfile will use the arguments to execute the main method.
	 * @param url Url to a jar file.
	 * @param args arguments to execute the main method.
	 * @verion 1.0
	 */
	public OdiseoClassLoader(URL url){
		super(new URL[]{ url });
		this.url = url;
		this.mainClass = getMainClassName(url);
	}

	/**
	 * Executes the jarFile with no arguments.
	 * @version 1.0
	 */
	public void execute() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		this.execute(new String[] {""});
	}

	/**
	 * Executes the jarFile.
	 * @param args this args will be used in the main method of the main class.
	 * @version 1.0
	 */
	public void execute(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		invokeClass(mainClass, args);
	}

	/**
	 * Retrieves the main class from the jarfile
	 * It will use the manifest property Main-Class. JExplorer uses it to know if a jar file is an executable file.
	 * @param url Url to a jarFile
	 */
	public static String getMainClassName(URL url){
		try{
			URL jarURL = new URL("jar", "", url + "!/");
			JarURLConnection uc = (JarURLConnection)jarURL.openConnection();
			Attributes attr = uc.getMainAttributes();
			return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
		} catch(MalformedURLException mfue){
			return null;
		} catch(IOException ioe){
			return null;
		}
	}
	
	/**
	 * Executes the main method in the main class
	 * @param name the class name. This values has been read from the manifest file
	 * @param args the arguments
	 */
	private void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException{
		Class c = loadClass(name);
		//System.out.println("name=" + name);
		//System.out.println("args=" + args);
		Method m = c.getMethod("main", new Class[] { args.getClass() });
		m.setAccessible(true);
		try{
			m.invoke(null, new Object[] { args });
		} catch(IllegalAccessException e){
			e.printStackTrace();
		}
	}

	public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException{
		if (Odiseo.isDESKTOP()){
			Class clazz = (Class)cache.get(name);
			if (clazz == null){
				if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("org.omg.") || name.startsWith("com.sun.") || name.startsWith("sun.") || name.startsWith("sunw.")){
					if (name.equals("java.awt.Frame") || name.equals("java.awt.Dialog") || name.equals("javax.swing.JDialog") || name.equals("javax.swing.JFrame") || name.equals("javax.swing.JWindow")){
						clazz = findEspecialClass(name);
					} else if (name.equals("java.awt.Window")){
						clazz = findEspecialClass(name);
						cache.put(name, clazz);
					} else {
						clazz = findSystemClass(name);
					}
					if (resolve){
						resolveClass(clazz);
					}
				} else {
					//clazz = findClass(name);
					clazz = super.loadClass(name, resolve);
				}
				cache.put(name, clazz);
			}
			return clazz;
		} else {
			return super.loadClass(name, resolve);
		}
	}

	private synchronized Class findEspecialClass(String name){
		try{
			byte[] b = loadClassData(name);
			Class clazz = defineClass(null, b, 0, b.length);
			return clazz;
		}catch (IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads Data from a class file
	 * @name the class name to look for
	 * @return the class' binary content
	 */
	private synchronized byte[] loadClassData(String name) throws IOException{
		String fileName = name.replace('.', '/') + ".class";
		URL url = getClass().getResource("txt/doc.txt");
		String strURL = url.toString();
		String path = strURL.substring(0, strURL.indexOf("!")) + "!/";
		url = new URL(path + fileName);
		//System.out.println("url=" + url);
		BufferedInputStream in = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b = -1;
		while ((b = in.read()) != -1)
			out.write(b);
		return out.toByteArray();
	}
}