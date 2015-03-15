/*
 * OdiseoProcess.java - 
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

public class OdiseoProcess implements Runnable, WindowListener, InternalFrameListener{

	private static int lastID = 0;
	private static Hashtable processList = new Hashtable();

	private int id;
	private File jarFile; //process jarfile (execute file)
	private String[] args;
	private Thread thread;
	private ThreadGroup threadGroup;
	private OdiseoClassLoader odiseoClassLoader;
	private Vector windows = new Vector(); //list of windows and JInternalFrames
	private Properties props; //process' properties
	private boolean isRunFinalization = false; //true when the run method has finished

	private static Vector processListeners = new Vector();

	private OdiseoProcess(String filePath, String[] args){
		this.jarFile = new File(filePath);
		this.args = args;
		id = lastID++;
		createProperties();
		threadGroup = new ThreadGroup(jarFile.getAbsolutePath());
		odiseoClassLoader = createClassLoader(jarFile);
		//Execute
		thread = new Thread(threadGroup, this);
		thread.setContextClassLoader(odiseoClassLoader);
		thread.start();
	}

/**
	 * Creates an Odiseo Process and execute it with no arguments.
	 * @param jarFilePath the url to a jar file (Odiseo execute file). If this parameter is null or zero length.
	 * @param jarFilePath then this functions returns null.
	 * @return an OdiseoProcess or null if the jarFilePath param has an invalid value.
	 */
	public static OdiseoProcess createProcess(String jarFilePath){
		return createProcess(jarFilePath, new String[]{""});
	}

	/**
	 * Creates an Odiseo Process.
	 * @param jarFilePath the url to a jar file (Odiseo execute file). If this parameter is null or zero length.
	 * @param jarFilePath then this functions returns null.
	 * @param args argument to execute the jar file
	 * @return an OdiseoProcess or null if the jarFilePath param has an invalid value.
	 */
	public static OdiseoProcess createProcess(String jarFilePath, String[] args){
		if (jarFilePath.length() == 0){
			return null;
		} else {
			OdiseoProcess op = new OdiseoProcess(jarFilePath, args);
			OdiseoProcess.processList.put(new Integer(op.getId()), op);
			op.fireOdiseoProcessListenerAdd();
			return op;
		}
	}

	/**
	 * kills the jar file.
	 * @param args the Arguments to use in the main methd
	 */
	public void kill(){
		OdiseoProcess.processList.remove(new Integer(id));
		if (processList.isEmpty()) System.exit(OdiseoSecurityManager.STOP_VM_NO_QUERY);
		fireOdiseoProcessListenerKill();
		clearWindows();
		odiseoClassLoader = null;
		destroyThreadGroup();
	}

	/**
	 * Returns the id for this process
	 */
	public int getId(){
		return id;
	}

	/**
	 * Returns the class loader for this process
	 */
	public OdiseoClassLoader getClassLoader(){
		return odiseoClassLoader;
	}

	/**
	 * Returns the properties asigned to this process
	 */
	public String getProperty(String key){
		return props.getProperty(key);
	}

	/**
	 * Returns the process name.
	 * the process name is the jarFilePath
	 */
	public String getName(){
		//return jarFile.getPath().substring(jarFilePath.lastIndexOf(File.separator)+1);
		return jarFile.getName();
	}

	/**
	 * Returns all the executed processes 
	 */
	public static Enumeration elements(){
		return processList.elements();
	}

	/**
	 * Adds an open window use by this process.
	 * When the process will be killed this windows will be disposed.
	 */
	public void addWindow(Window w){
		windows.add(w);
		w.addWindowListener(this);
	}

	/**
	 * Adds an open window use by this process.
	 * When the process will be killed this windows will be disposed.
	 */
	public void addWindow(JInternalFrame w){
		windows.add(w);
		w.addInternalFrameListener(this);
	}

	/**
	 * Destroys the thread group
	 */
	private void destroyThreadGroup(){
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);
		for (int i = 0; i<threads.length; i++)	{
			threads[i].interrupted();
		}
		ThreadGroup[] threadGroups = new ThreadGroup[threadGroup.activeGroupCount()];
		threadGroup.enumerate(threadGroups);
		for (int i = 0; i<threadGroups.length; i++)	{
			threadGroups[i].destroy();
		}
		try{
			threadGroup.destroy();
		} catch(IllegalThreadStateException  itse){}
		threadGroup = null;
	}

	/**
	 * Disposes all the windows opened by this process.
	 */
	private void clearWindows(){
		for(Enumeration e = windows.elements(); e.hasMoreElements();){
			Object obj = e.nextElement();
			if (obj instanceof Window){
				Window w = (Window)obj;
				w.removeWindowListener(this);
				try{ w.dispose(); } catch(Exception ex){}
				w = null;
			} else if (obj instanceof JInternalFrame){
				JInternalFrame w = (JInternalFrame)obj;
				w.removeInternalFrameListener(this);
				try{ w.dispose(); } catch(Exception ex){}
				w = null;
			}
		}
		windows.clear();
		windows = null;
	}

	/**
	 * Brings the process' windows to the front.
	 * this function may be called when in the dock the process is selected.
	 */
	public void seeProcess(){
		deiconifiedProcess();
		for(Enumeration e = windows.elements(); e.hasMoreElements();){
			Object obj = e.nextElement();
			if (obj instanceof Window)	{
				Window w = (Window)obj;
				try{ w.toFront(); } catch(Exception ex){}
			} else if (obj instanceof JInternalFrame){
				JInternalFrame w = (JInternalFrame)obj;
				try{ w.toFront(); } catch(Exception ex){}
			}
		}
	}

	/**
	 * Deiconified all the process' windows.
	 * this function may be called when in the dock the process is selected.
	 */
	public void deiconifiedProcess(){
		for(Enumeration e = windows.elements(); e.hasMoreElements();){
			Object obj = e.nextElement();
			if (obj instanceof Frame){
				Frame f = (Frame)obj;
				try{ f.setState(Frame.NORMAL); } catch(Exception ex){}
			} else if (obj instanceof JInternalFrame){
				JInternalFrame f = (JInternalFrame)obj;
				try{ f.setIcon(false); } catch(Exception ex){}
			}
		}
	}

	/**
	 * Iconified all the process' windows.
	 * this function may be called when in the dock the process is selected.
	 */
	public void minimizeProcess(){
		for(Enumeration e = windows.elements(); e.hasMoreElements();){
			Object obj = e.nextElement();
			if (obj instanceof Frame){
				Frame f = (Frame)obj;
				try{ f.setState(Frame.NORMAL); } catch(Exception ex){}
			} else if (obj instanceof JInternalFrame){
				JInternalFrame f = (JInternalFrame)obj;
				try{ f.setIcon(true); } catch(Exception ex){}
			}
		}
	}

	public static void addOdiseoProcessListener(OdiseoProcessListener l){
		processListeners.add(l);
	}
	
	public static void removeOdiseoProcessListener(OdiseoProcessListener l){
		processListeners.remove(l);
	}

	private void fireOdiseoProcessListenerAdd(){
		OdiseoProcessEvent ope = new OdiseoProcessEvent(this);
		for(Enumeration e = OdiseoProcess.processListeners.elements(); e.hasMoreElements();){
			OdiseoProcessListener l = (OdiseoProcessListener)e.nextElement();
			try{ l.odiseoProcessAdded(ope); } catch (Exception ee){}
		}
	}
	
	private void fireOdiseoProcessListenerKill(){
		OdiseoProcessEvent ope = new OdiseoProcessEvent(this);
		for(Enumeration e = OdiseoProcess.processListeners.elements(); e.hasMoreElements();){
			OdiseoProcessListener l = (OdiseoProcessListener)e.nextElement();
			try{ l.odiseoProcessKilled(ope); } catch (Exception ee){}
		}
	}

	/**
	 * Returns a process by a class loader
	 */
	public static OdiseoProcess getProcessByClassLoader(ClassLoader cl){
		return getProcessByClassLoader((OdiseoClassLoader)cl);
	}

	/**
	 * Returns a process by a class loader
	 */
	public static OdiseoProcess getProcessByClassLoader(OdiseoClassLoader ocl){
		OdiseoProcess op = null;
		for(Enumeration e = processList.elements(); e.hasMoreElements();){
			op = (OdiseoProcess)e.nextElement();
			if (ocl.equals(op.getClassLoader())){
				break;
			}
		}
		return op;
	}

	/**
	 * Returns a process by an id
	 */
	public static OdiseoProcess getProcessById(int id){
		return (OdiseoProcess)processList.get(new Integer(id));
	}

	private synchronized boolean isProcessStopped(){
		//return isRunFinalization && windows.isEmpty(); //the run method is finished and not windows are loaded
		return false;
	}

	//implements the Runnable interface
	public void run(){
		try{
			odiseoClassLoader.execute(args);
		} catch(ClassNotFoundException cnfe){
			cnfe.printStackTrace();
		} catch(NoSuchMethodException nsme){
			nsme.printStackTrace();
		} catch(InvocationTargetException ite){
			ite.printStackTrace();
		}
		isRunFinalization = true;
		if (isProcessStopped()) kill();
	}

	//implements the WindowListener interface 
	public void windowActivated(WindowEvent e){}

	public void windowClosed(WindowEvent e){
		Window w = (Window)e.getSource();
		w.removeWindowListener(this);
		windows.remove(w);
		w = null;
		if (isProcessStopped()) kill();
	}

	public void windowClosing(WindowEvent e){}

	public void windowDeactivated(WindowEvent e){}

	public void windowDeiconified(WindowEvent e){}

	public void windowIconified(WindowEvent e){}

	public void windowOpened(WindowEvent e){}

	//Implements InternalFrameListener
	public void internalFrameActivated(InternalFrameEvent e){}

	public void internalFrameClosed(InternalFrameEvent e) {
		JInternalFrame w = (JInternalFrame)e.getSource();
		w.removeInternalFrameListener(this);
		windows.remove(w);
		w = null;
		if (isProcessStopped()) kill();
	}

	public void internalFrameClosing(InternalFrameEvent e){}

	public void internalFrameDeactivated(InternalFrameEvent e){}
	
	public void internalFrameDeiconified(InternalFrameEvent e){}
	
	public void internalFrameIconified(InternalFrameEvent e){}
	
	public void internalFrameOpened(InternalFrameEvent e){}

	/**
	 * Creates the class loader for the jar file
	 * @param jarFilePath the path to the jar file
	 */
	private OdiseoClassLoader createClassLoader(File jarFile){
		try{
			URL url = jarFile.toURL();
			OdiseoClassLoader ocl = new OdiseoClassLoader(url);
			return ocl;
		} catch(MalformedURLException mue){
			mue.printStackTrace();
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
		return null;
	}

	/**
	 * Puts the default system properties for this process
	 */
	private void createProperties(){
		props = new Properties(System.getProperties());
		String userdir = jarFile.getAbsolutePath();
		userdir = userdir.substring(0, userdir.lastIndexOf(File.separator));
		//System.out.println("userdir=" + userdir);
		props.setProperty("user.dir", userdir);
		props.setProperty("os.name", "Odiseo (" + System.getProperty("os.name") + ")");
		props.setProperty("os.version", "1.0 (" + System.getProperty("os.version") + ")");
	}

	public String toString(){
		return jarFile.getPath() + ", id=" + id;
	}
}