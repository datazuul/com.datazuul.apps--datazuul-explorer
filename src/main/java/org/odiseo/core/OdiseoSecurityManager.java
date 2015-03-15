/*
 * OdiseoSecurityMasnager.java - 
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

import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.security.*;

import org.odiseo.swing.*;

public class OdiseoSecurityManager extends SecurityManager{

	public static final int STOP_VM = 20001;
	public static final int STOP_VM_NO_QUERY = 20002;
	private static final String MESSAGE_BYE = "Shutdown Odiseo, bye.";
	private static final String MESSAGE_NO_BYE = "You are not allowed to close Odiseo";

	public void checkCreateClassLoader() { }
	public void checkAccess(Thread g) { }
	public void checkAccess(ThreadGroup g) { }

	public void checkExit(int status) {
		
		if (status == STOP_VM){
			int res = JOptionPane.showConfirmDialog(null, "Do you want to halt the system?", "Odiseo", JOptionPane.YES_NO_OPTION);
			if (res == JOptionPane.NO_OPTION)	{
				throw new SecurityException(MESSAGE_NO_BYE);
			} else {
				System.out.println(MESSAGE_BYE);
			}
		} else if (status == STOP_VM_NO_QUERY){
			System.out.println(MESSAGE_BYE);
		} else {
			OdiseoProcess op = getCurrentProcess();
			if (op != null) op.kill();
			throw new SecurityException(MESSAGE_NO_BYE);
		}
	}

	public void checkExec(String cmd) { /* DEBUG throw new SecurityException(); */ }
	public void checkLink(String lib) { }
	public void checkRead(FileDescriptor fd) { }
	public void checkRead(String file) { }
	public void checkRead(String file, Object context) { }
	public void checkWrite(FileDescriptor fd) { }
	public void checkWrite(String file) { }
	public void checkDelete(String file) { }
	public void checkConnect(String host, int port) { }
	public void checkConnect(String host, int port, Object context) { }
	public void checkListen(int port) { }
	public void checkAccept(String host, int port) { }
	public void checkMulticast(InetAddress maddr) { }
	public void checkMulticast(InetAddress maddr, byte ttl) { }

	public void checkPropertyAccess(String key){
		OdiseoProcess op = getCurrentProcess();
		if (op != null){
			String v = op.getProperty(key);
			if (v != null) System.setProperty(key, v);
			//System.out.println("key=" + v);
		}
	}

	public void checkPermission(Permission perm){ }
	public void checkPermission(Permission perm, Object context){ }
	public void checkPrintJobAccess() { }
	public void checkSystemClipboardAccess() { }
	public void checkAwtEventQueueAccess() { }
	public void checkPackageAccess(String pkg) { }
	public void checkPackageDefinition(String pkg) { }
	public void checkSetFactory() { }
	public void checkMemberAccess(Class clazz, int which) { }
	public void checkSecurityAccess(String provider) { }

	public boolean checkTopLevelWindow(Object obj) {
		boolean check = super.checkTopLevelWindow(obj);
		if (obj instanceof Window){
			Window window = (Window)obj;
			OdiseoProcess op = getCurrentProcess();
			if (op != null) op.addWindow(window);
		}
		return check;
	}

	private OdiseoProcess getCurrentProcess(){
		OdiseoProcess op = null;
		Class[] classes = getClassContext();
		for(int i = 0; i < classes.length; i++){
			ClassLoader cl = classes[i].getClassLoader();
			if (cl instanceof OdiseoClassLoader){
				op = OdiseoProcess.getProcessByClassLoader((OdiseoClassLoader)cl);
				break;
			}
		}
		return op;
	}
}