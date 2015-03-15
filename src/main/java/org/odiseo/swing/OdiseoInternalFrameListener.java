/*
 * OdiseoInternalFrameListener.java -  
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

package org.odiseo.swing;

import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

public class OdiseoInternalFrameListener implements InternalFrameListener{

	private WindowListener wl;
	//private Window win;

//	public OdiseoInternalFrameListener(Window win, WindowListener wl){
//		this.win = win;
//		this.wl = wl;
//	}

	public OdiseoInternalFrameListener(WindowListener wl){
		this.wl = wl;
	}

	//Invoked when an internal frame is activated.
	public void internalFrameActivated(InternalFrameEvent e){
		//wl.windowActivated(new WindowEvent(win, WindowEvent.WINDOW_ACTIVATED));
		wl.windowActivated(null);
	}

	//Invoked when an internal frame has been closed.
	public void internalFrameClosed(InternalFrameEvent e){
		//wl.windowClosed(new WindowEvent(win, WindowEvent.WINDOW_CLOSED));
		wl.windowClosed(null);
	}

	//Invoked when an internal frame is in the process of being closed.
	public void internalFrameClosing(InternalFrameEvent e){
		//wl.windowClosing(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
		wl.windowClosing(null);
	}

	//Invoked when an internal frame is de-activated.
	public void internalFrameDeactivated(InternalFrameEvent e){
		//wl.windowDeactivated(new WindowEvent(win, WindowEvent.WINDOW_DEACTIVATED));
		wl.windowDeactivated(null);
	}

	//Invoked when an internal frame is de-iconified.
	public void internalFrameDeiconified(InternalFrameEvent e){
		//wl.windowDeiconified(new WindowEvent(win, WindowEvent.WINDOW_DEICONIFIED));
		wl.windowDeiconified(null);
	}

	//Invoked when an internal frame is iconified.
	public void internalFrameIconified(InternalFrameEvent e){
		//wl.windowIconified(new WindowEvent(win, WindowEvent.WINDOW_ICONIFIED));
		wl.windowIconified(null);
	}

	//Invoked when a internal frame has been opened
	public void internalFrameOpened(InternalFrameEvent e){
		wl.windowOpened(null);
		//wl.windowOpened(new WindowEvent(null, WindowEvent.WINDOW_OPENED));
	}
}