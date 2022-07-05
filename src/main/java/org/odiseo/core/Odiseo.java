/*
 * Odiseo.java - loads the Odiseo system
 * Copyright (C) 2000-2001 Inigo Gonzalez
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

import org.odiseo.swing.*;

import javax.swing.JDesktopPane;
import java.awt.Frame;

/**
 * Loads the Odiseo system class.
 * @author Inigo
 * @version 1.0
 */
public class Odiseo{

	private static boolean DESKTOP;
	private static OdiseoDesktop odiseoDesktop = null;

	/**
	 * Loads the Odiseo process system in the memory.
	 * @param args List or arguments:
	 * @param args args[0] - DESKTOP=YES or DESKTOP=NO
	 * @param args args[1] - jar files to execute
	 * @param args ...
	 * @param args args[n] - jar files to execute
	 */
	private Odiseo(String[] args){
		System.out.println("Odiseo, Copyright (c) 2001, Inigo Gonzalez.");
		System.setErr(System.out);
		if (args.length == 0){
			System.out.println("Odiseo arguments failed. Use: javaw -jar odiseo desktop=yes/no [jar file path]*");
		} else {
			DESKTOP = args[0].toUpperCase().equals("DESKTOP=YES");
			if (Odiseo.isDESKTOP()) Odiseo.odiseoDesktop = new OdiseoDesktop(); //the desktop must be execute before to install de new Security Manager
			System.setSecurityManager(new OdiseoSecurityManager());
			System.out.println("Odiseo has been installed in the memory.");
			for(int i = 1; i < args.length; i++){
				OdiseoProcess op = OdiseoProcess.createProcess(args[i]);
			}
		}
	}

	/**
	 * Returns if the Odiseo system runs like a desktop pane
	 * @return True if the system runs like a desktop pane
	 */
	public static boolean isDESKTOP(){
		return DESKTOP;
	}

	/**
	 * Returns the Odiseo desktop
	 */
	public static OdiseoDesktop getDesktop(){
		return Odiseo.odiseoDesktop;
	}

	/**
	 * Shows the Odiseo's about dialog.
	 */
	public static void seeAboutOdiseo(){
		new AboutOdiseo();
	}

	public static void main(String[] args){
		new Odiseo(args);
	}
}